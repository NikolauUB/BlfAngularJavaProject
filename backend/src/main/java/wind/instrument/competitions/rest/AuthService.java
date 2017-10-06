package wind.instrument.competitions.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wind.instrument.competitions.configuration.SessionParameters;
import wind.instrument.competitions.data.UserEntity;
import wind.instrument.competitions.middle.BotCheckException;
import wind.instrument.competitions.middle.Question;
import wind.instrument.competitions.middle.ReservedUsernames;
import wind.instrument.competitions.rest.model.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Supplies authorization and profile creation|updating through rest
 */

@RestController
@Transactional
public class AuthService {

    private static Logger LOG = LoggerFactory.getLogger(AuthService.class);
    /**
     * Russian messages
     */
    private static ResourceBundle bundle = ResourceBundle.getBundle("Messages");
    /**
     * Single user with admin privileges
     */
    //todo add user with such name with migration
    public static String ADMIN_USERNAME = "NikolayUB";

    @Autowired
    private HttpSession httpSession;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Qualifier("antiBotQuestion")
    @Autowired
    private Question antiBotQuestion;

    /**
     * Frond end calls checkAuth first and gets new csrf token
     * Then it is able to do other AuthService actions
     * For already connected user checkAuth returns
     * (single for session) current csrf token and other session options
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/api/checkAuth", method = RequestMethod.GET)
    public AuthStatus checkAuth(HttpServletRequest request, HttpServletResponse response) {
        AuthStatus result = new AuthStatus();
        HttpSessionCsrfTokenRepository httpTokenRepository = new HttpSessionCsrfTokenRepository();
        result.setAuth(false);
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                !(SecurityContextHolder.getContext().getAuthentication()
                        instanceof AnonymousAuthenticationToken)) {
            result.setAuth(true);
            //return current token if user is authenticated
            CsrfToken token = httpTokenRepository.loadToken(request);
            result.setTkn(token.getToken());
            result.setuName("" + httpSession.getAttribute(SessionParameters.USERNAME.name()));
            result.setuId((Long) httpSession.getAttribute(SessionParameters.USER_ID.name()));
        } else {
            //generate new token every time if user is not authenticated
            CsrfToken token = httpTokenRepository.generateToken(request);
            result.setTkn(token.getToken());
            result.setuName("");
            result.setuId(null);
            httpTokenRepository.saveToken(token, request, response);
        }
        return result;
    }

    /**
     * todo as improvement make possible to login by email or username
     *
     * @param loginData - contains email and password
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/api/login", method = RequestMethod.POST)
    public AuthStatus restLogin(@RequestBody LoginData loginData, HttpServletRequest request, HttpServletResponse response) {
        int code = HttpServletResponse.SC_OK;
        TypedQuery<UserEntity> userQuery =
                em.createQuery("select c from UserEntity c where LOWER(c.email) = :email or LOWER(c.username) = :username",
                        UserEntity.class);
        UserEntity userEntity = null;
        try {
            String inputEmail = loginData.getEmail().trim().toLowerCase();
            userEntity = userQuery
                    .setParameter("email", inputEmail)
                    .setParameter("username", inputEmail)
                    .getSingleResult();
        } catch (Exception ex) {
        }

        String message = "";
        if (userEntity == null) {
            code = HttpServletResponse.SC_BAD_REQUEST;
        } else {
            final UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userEntity.getEmail(), loginData.getPassword());
            try {
                final Authentication authentication = authenticationManager.authenticate(authRequest);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (BadCredentialsException ex) {
                code = HttpServletResponse.SC_BAD_REQUEST;
            } catch (Exception e) {
                LOG.error("Server error during login", e);
                code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                message = e.getMessage();
            }
        }
        AuthStatus result = this.checkAuth(request, response);
        result.setCd(code);
        result.seteMsg(message);
        return result;
    }

    /**
     * Frondend calls checkAuth and if user is authenticated calls logout.
     * Otherwise frontend goes to unauthenticated status withot calling logout.
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/api/logout", method = RequestMethod.POST)
    public AuthStatus restLogout(HttpServletRequest request, HttpServletResponse response) {
        AuthStatus auth = this.checkAuth(request, response);
        if (!auth.getAuth()) {
            return auth;
        }
        SecurityContextHolder.getContext().setAuthentication(null);
        httpSession.removeAttribute(SessionParameters.USERNAME.name());
        httpSession.removeAttribute(SessionParameters.USER_CREATED.name());
        httpSession.removeAttribute(SessionParameters.USER_ID.name());
        return this.checkAuth(request, response);
    }

    /**
     * USER REGISTRATION
     * User posts email, username, password and answer on antibot question
     *
     * @param regData
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/api/register", method = RequestMethod.POST)
    public RegistrationReply restRegister(@RequestBody RegistrationData regData) {
        RegistrationReply result = new RegistrationReply();
        //first check antibot answers
        try {
            Question.checkUserReply(regData.getGivenAnswers(),
                    (ArrayList<String>) httpSession.getAttribute(SessionParameters.QUESTION_ANSWERS.name()));
        } catch (BotCheckException e) {
            return this.prepareUnsuccessfulReplyWithError(result, regData, e, HttpServletResponse.SC_FORBIDDEN);
        }

        //email
        if (regData.getEmail().trim().length() == 0) {
            return this.prepareUnsuccessfulReplyWithError(result, regData, new Exception(bundle.getString("REG_NO_EMAIL")), HttpServletResponse.SC_FORBIDDEN);
        } else if (regData.getEmail().indexOf("@") == -1) {
            return this.prepareUnsuccessfulReplyWithError(
                    result,
                    regData,
                    new Exception("Email must contain @"),
                    HttpServletResponse.SC_BAD_REQUEST);
        }
        UserEntity newUser = new UserEntity();
        newUser.setEmail(regData.getEmail().trim().toLowerCase());
        if (ServiceUtil.isEmailExist(newUser.getEmail(), em)) {
            return this.prepareUnsuccessfulReplyWithError(result, regData, new Exception(bundle.getString("REG_EMAIL_EXISTS")), 422);
        }

        if (ReservedUsernames.RESERVED_USERNAMES.isUsernameReserved(regData.getUsername().trim().toLowerCase())
                && !ReservedUsernames.RESERVED_USERNAMES.checkEmail(regData.getUsername().trim().toLowerCase(), newUser.getEmail())) {
            return this.prepareUnsuccessfulReplyWithError(result, regData, new Exception(bundle.getString("USERNAME_RESERVED")), 422);
        }

        //password
        newUser.setTypedPassword(regData.getPassword());
        try {
            newUser.generatePasswordHashes();
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Error when generating password hash", e);
            return this.prepareUnsuccessfulReplyWithError(result, regData, e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        //username
        if (regData.getUsername().indexOf("@") != -1) {
            return this.prepareUnsuccessfulReplyWithError(
                    result,
                    regData,
                    new Exception("Username can not contain @"),
                    HttpServletResponse.SC_BAD_REQUEST);
        }

        ArrayList<String> checkUniqueUsername = null;
        if (regData.getUsername().trim().length() > 0) {
            checkUniqueUsername = ServiceUtil.checkUsernameUniqueNess(regData.getUsername().trim(), em);
            if (checkUniqueUsername.size() > 0) {
                return this.prepareUnsuccessfulReplyWithError(
                        result,
                        regData,
                        new Exception(MessageFormat.format(
                                bundle.getString((checkUniqueUsername.size() == 1) ? "USERNAME_EXISTS" : "USERNAME_EXISTING_LIST"),
                                String.join(", ", checkUniqueUsername))),
                        449);
            } else {
                newUser.setUsername(regData.getUsername().trim());
            }
        } else {
            return this.prepareUnsuccessfulReplyWithError(
                    result, regData, new Exception(bundle.getString("USERNAME_EMPTY")), 449);
        }

        //save new user
        try {
            em.persist(newUser);
        } catch (Exception e) {
            LOG.error("Error when saving new user", e);
            return this.prepareUnsuccessfulReplyWithError(result, regData, e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    /**
     * Generates anti-bot question with random replies
     *
     * @return
     */
    @RequestMapping(value = "/api/question", method = RequestMethod.GET)
    public AntiBotQuestion restRegistrationQuestion() {
        AntiBotQuestion result = new AntiBotQuestion();
        this.antiBotQuestion.createQuestion();
        result.setQuestionAnswers(this.antiBotQuestion.getQuestionAnswers());
        //save correct reply in session
        httpSession.setAttribute(SessionParameters.QUESTION_ANSWERS.name(), this.antiBotQuestion.getCorrectAnswers());
        return result;
    }

    /**
     * Cleans private data and sets next antibot question
     *
     * @param result    - RegistrationReply which already can be modified before
     * @param regData   - Data typed by user which is not needed to be sent back. It is cleaned
     * @param e         - Exception with error
     * @param errorCode
     * @return
     */
    private RegistrationReply prepareUnsuccessfulReplyWithError(RegistrationReply result,
                                                                RegistrationData regData,
                                                                Exception e,
                                                                int errorCode) {
        //clean given answers for antibot question, password and email
        regData.setGivenAnswers(null);
        regData.setPassword(null);
        regData.setEmail(null);
        result.setCode(errorCode);
        result.setErrorMsg(e.getLocalizedMessage());
        result.setRegistrationData(regData);

        //generate new antiBot question
        result.setNewQuestion(this.restRegistrationQuestion());
        return result;
    }


}
