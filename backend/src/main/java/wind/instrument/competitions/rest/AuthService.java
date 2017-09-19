package wind.instrument.competitions.rest;

import org.springframework.web.bind.annotation.*;
import wind.instrument.competitions.configuration.SessionParameters;
import wind.instrument.competitions.data.UserEntity;
import wind.instrument.competitions.middle.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.transaction.annotation.Transactional;
import wind.instrument.competitions.rest.model.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.*;

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
        HttpSessionCsrfTokenRepository httpTokenRepository =  new HttpSessionCsrfTokenRepository();
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
            result.setuId((Long)httpSession.getAttribute(SessionParameters.USER_ID.name()));
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
        } catch (Exception ex) {}

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
        //todo check that user is authenticated to be on safe side
        SecurityContextHolder.getContext().setAuthentication(null);
        httpSession.removeAttribute(SessionParameters.USERNAME.name());
        httpSession.removeAttribute(SessionParameters.USER_CREATED.name());
        httpSession.removeAttribute(SessionParameters.USER_ID.name());
        return this.checkAuth(request, response);
    }

    /**
     * User profile data
     * avatars all jpeg are stored in the database as bytes encoded into base64 for transportation
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/api/profile", method = RequestMethod.GET)
    public UserData getProfile() {
        return this.getUserProfile((Long)httpSession.getAttribute(SessionParameters.USER_ID.name()));
    }

    private UserData getUserProfile(Long userId) {
        UserData result = new UserData();
        UserEntity user = em.find(UserEntity.class, userId);
        result.setUsername(user.getUsername());
        result.setUpdated(user.getUpdated());
        result.setCreated(user.getCreated());
        if (user.getImage() != null && user.getImage().length > 0) {
            result.setPreviewImage("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(user.getImage()));
        }
        return result;
    }


    @RequestMapping(value = "/api/getUserDetails", method = RequestMethod.GET)
    public UserData getUserDetails(@RequestParam("uid") Long userId, HttpServletResponse response) {
        if (userId == null) {
            this.sendResponseError(HttpServletResponse.SC_BAD_REQUEST,
                    "User id is not set",
                    response);
        }
        return this.getUserProfile(userId);
    }

    @RequestMapping(value = "/api/changepasswordtid", method = RequestMethod.PUT)
    public int restChangePasswordByToken(@RequestBody PasswordData passwordData, HttpServletResponse response) {
        //SendEmailToUser sendEmail = new SendEmailToUser();
        //sendEmail.sendLinkForResetPassword(passwordData.getNewPassword());

        this.sendResponseError(HttpServletResponse.SC_NOT_IMPLEMENTED,
                "Сменя пароля по ссылке из электронной почты еще не реализована",
                response);
        return  HttpServletResponse.SC_OK;
    }

    @RequestMapping(value = "/api/changepassword", method = RequestMethod.PUT)
    public int restChangePassword(@RequestBody PasswordData passwordData, HttpServletResponse response) {
        //change password
        if (passwordData.getToken() == null) {
            UserEntity currentUser = em.find(UserEntity.class, httpSession.getAttribute(SessionParameters.USER_ID.name()));
            if (currentUser == null) {
                this.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        bundle.getString("SESSION_ISSUE"),
                        response);
            }
            //check old password
            if (passwordData.getOldPassword() == null || passwordData.getOldPassword().trim().length() == 0) {
                this.sendResponseError(HttpServletResponse.SC_BAD_REQUEST,
                        bundle.getString("NO_OLD_PASSWORD"),
                        response);
            }
            //check new password
            if (passwordData.getNewPassword() == null || passwordData.getNewPassword().trim().length() == 0) {
                this.sendResponseError(HttpServletResponse.SC_BAD_REQUEST,
                        bundle.getString("NO_NEW_PASSWORD"),
                        response);
            }

            String encryptedPass = null;
            try {
                encryptedPass = Utils.encryptPassword(passwordData.getOldPassword(),currentUser.getPasswordSalt());
            } catch (NoSuchAlgorithmException e) {
                LOG.error("Error when generating old password hash in restSaveProfile", e);
                this.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        e.getLocalizedMessage(),
                        response);
            }

            if (encryptedPass != null && encryptedPass.equals(currentUser.getPasswordHash())) {
                currentUser.setTypedPassword(passwordData.getNewPassword());
                try {
                    currentUser.generatePasswordHashes();
                } catch (NoSuchAlgorithmException e) {
                    LOG.error("Error when generating password hash in restSaveProfile", e);
                    this.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            e.getLocalizedMessage(),
                            response);
                }
                //save new password
                try {
                    em.persist(currentUser);
                } catch (Exception e) {
                    LOG.error("Error when saving new user password", e);
                    this.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            e.getLocalizedMessage(),
                            response);
                }
            } else {
                this.sendResponseError(HttpServletResponse.SC_BAD_REQUEST,
                        bundle.getString("OLD_PASSWORD_WRONG"),
                        response);
            }

        }
        return HttpServletResponse.SC_OK;
    }

    @RequestMapping(value = "/api/changeemail", method = RequestMethod.PUT)
    public int restChangeEmail(@RequestBody EmailData emailData, HttpServletResponse response) {
        UserEntity currentUser = em.find(UserEntity.class, httpSession.getAttribute(SessionParameters.USER_ID.name()));
        if (currentUser == null) {
            this.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    bundle.getString("SESSION_ISSUE"),
                    response);
        }
        if(emailData.getNewEmail() != null && emailData.getNewEmail().indexOf("@") == -1) {
            this.sendResponseError(HttpServletResponse.SC_BAD_REQUEST,
                    "Email must contain @",
                    response);
            return HttpServletResponse.SC_BAD_REQUEST;
        }
        //change email
        if (emailData.getNewEmail() != null && emailData.getNewEmail().trim().length() > 0) {
            if (emailData.getOldEmail() != null && emailData.getOldEmail().trim().length() > 0) {
                if(currentUser.getEmail().toLowerCase().equals(emailData.getOldEmail().trim().toLowerCase())) {
                    if(isEmailExist(emailData.getNewEmail().trim())) {
                        this.sendResponseError(HttpServletResponse.SC_BAD_REQUEST,
                                bundle.getString("REG_EMAIL_EXISTS"),
                                response);
                    } else {
                        currentUser.setEmail(emailData.getNewEmail().trim().toLowerCase());
                        //save new email
                        try {
                            em.persist(currentUser);
                        } catch (Exception e) {
                            LOG.error("Error when saving new user email", e);
                            this.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                    e.getLocalizedMessage(),
                                    response);
                        }
                    }
                } else {
                    this.sendResponseError(HttpServletResponse.SC_BAD_REQUEST,
                            bundle.getString("OLD_EMAIL_WRONG"),
                            response);
                }
            } else {
                this.sendResponseError(HttpServletResponse.SC_BAD_REQUEST,
                        bundle.getString("OLD_EMAIL_EMPTY"),
                        response);
            }
        } else {
            this.sendResponseError(HttpServletResponse.SC_BAD_REQUEST,
                    bundle.getString("NEW_EMAIL_EMPTY"),
                    response);
        }
        return HttpServletResponse.SC_OK;
    }
    @RequestMapping(value = "/api/deleteprofileimage", method = RequestMethod.DELETE)
    public ProfileChangedReply restDeleteProfileImage(HttpServletRequest request, HttpServletResponse response) {
        ProfileChangedReply result = new ProfileChangedReply();
        UserEntity currentUser = em.find(UserEntity.class, httpSession.getAttribute(SessionParameters.USER_ID.name()));

        if (currentUser == null) {
            this.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                    bundle.getString("SESSION_ISSUE"),
                                    response);
        }
        currentUser.setImage(null);
        try {
            em.persist(currentUser);
            UserData userDate = new UserData();
            userDate.setUsername(currentUser.getUsername());
            result.setUserData(userDate);
        } catch (Exception e) {
            LOG.error("Error when deleting user image", e);
            this.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getLocalizedMessage(),
                    response);
        }
        return result;
    }


    /**
     * Change profile username and image can be replaced
     * @param userData
     * @param response
     * @return
     */
    @RequestMapping(value = "/api/saveprofile", method = RequestMethod.PUT)
    public ProfileChangedReply restSaveProfile(@RequestBody UserData userData, HttpServletResponse response) {
        ProfileChangedReply result = new ProfileChangedReply();
        UserEntity currentUser = em.find(UserEntity.class, httpSession.getAttribute(SessionParameters.USER_ID.name()));
        boolean anyChanges = false;
        if (currentUser == null) {
            this.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    bundle.getString("SESSION_ISSUE"),
                    response);
        }
        if (userData.getUsername().indexOf("@") != -1) {
            return this.prepareProfileReplyWithError(result,
                    userData,
                    new Exception("Username can not contain @"),
                    HttpServletResponse.SC_BAD_REQUEST);
        }

        if (ReservedUsernames.RESERVED_USERNAMES.isUsernameReserved(userData.getUsername().trim().toLowerCase())
                && !ReservedUsernames.RESERVED_USERNAMES.checkEmail(userData.getUsername().trim().toLowerCase(), currentUser.getEmail())) {
            return this.prepareProfileReplyWithError(result,
                    userData,
                    new Exception(bundle.getString("USERNAME_RESERVED")),
                    HttpServletResponse.SC_BAD_REQUEST);
        }

        //username
        if (userData.getUsername() != null
                && userData.getUsername().trim().length() > 0
                && !userData.getUsername().trim().toLowerCase().equals(currentUser.getUsername().toLowerCase())) {
            ArrayList<String> existingUsernames = this.checkUsernameUniqueNess(userData.getUsername().trim());
            if (existingUsernames.size() > 0) {
                return this.prepareProfileReplyWithError(result,
                        userData,
                        new Exception(MessageFormat.format(
                                bundle.getString((existingUsernames.size() == 1)?"USERNAME_EXISTS":"USERNAME_EXISTING_LIST"),
                                String.join(", ", existingUsernames))),
                        HttpServletResponse.SC_BAD_REQUEST);
            }
            currentUser.setUsername(userData.getUsername().trim());
            anyChanges = true;
        }



        //image
        if (userData.getSaveImage() != null && userData.getSaveImage().length() > 0) {
            currentUser.setImage(Base64.getDecoder().decode(userData.getSaveImage()));
            anyChanges = true;
        }

        //save if changes are available
        if (anyChanges) {
            try {
                em.persist(currentUser);
                //set new user in the session
                httpSession.setAttribute(SessionParameters.USERNAME.name(), currentUser.getUsername());
            } catch (Exception e) {
                LOG.error("Error when saving user profile", e);
                this.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        e.getLocalizedMessage(),
                        response);
            }
        }
        userData.setSaveImage(null);
        result.setUserData(userData);
        return result;
    }

    /**
     *
     * @param result
     * @param userData
     * @param e
     * @param errorCode
     * @return
     */
    private ProfileChangedReply prepareProfileReplyWithError(ProfileChangedReply result,
                                                       UserData userData,
                                                       Exception e,
                                                       int errorCode) {
        userData.setSaveImage(null);
        result.setUserData(userData);
        result.setCode(errorCode);
        result.setErrorMsg(e.getLocalizedMessage());
        return result;
    }

    /**
     * Generates anti-bot question with random replies
     * @return
     */
    @RequestMapping(value = "/api/question", method = RequestMethod.GET)
    public AntiBotQuestion restRegistrationQuestion() {
        AntiBotQuestion result = new AntiBotQuestion();
        Question question = new Question();
        result.setQuestionAnswers(question.getQuestionAnswers());
        //save correct reply in session
        httpSession.setAttribute(SessionParameters.QUESTION_ANSWERS.name(), question.getCorrectAnswers());
        return result;
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
    public RegistrationReply restRegister(@RequestBody RegistrationData regData,
                                          HttpServletRequest request, HttpServletResponse response) {
        RegistrationReply result = new RegistrationReply();
        //first check antibot answers
        try {
            Question.checkUserReply( regData.getGivenAnswers(),
                    (ArrayList<String>)httpSession.getAttribute(SessionParameters.QUESTION_ANSWERS.name()));
        } catch (BotCheckException e) {
            return this.prepareUnsuccessfulReplyWithError(result, regData, e, HttpServletResponse.SC_FORBIDDEN);
        }

        //email
        if (regData.getEmail().trim().length() == 0) {
            return this.prepareUnsuccessfulReplyWithError(result, regData, new Exception(bundle.getString("REG_NO_EMAIL")), HttpServletResponse.SC_FORBIDDEN);
        } else if ( regData.getEmail().indexOf("@") == -1) {
            return this.prepareUnsuccessfulReplyWithError(
                    result,
                    regData,
                    new Exception("Email must contain @"),
                    HttpServletResponse.SC_BAD_REQUEST);
        }
        UserEntity newUser = new UserEntity();
        newUser.setEmail(regData.getEmail().trim().toLowerCase());
        if (this.isEmailExist(newUser.getEmail())) {
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
        if(regData.getUsername().trim().length() > 0) {
            checkUniqueUsername=this.checkUsernameUniqueNess(regData.getUsername().trim());
            if(checkUniqueUsername.size() > 0) {
                return this.prepareUnsuccessfulReplyWithError(
                        result,
                        regData,
                        new Exception(MessageFormat.format(
                                bundle.getString((checkUniqueUsername.size() == 1)?"USERNAME_EXISTS":"USERNAME_EXISTING_LIST"),
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
     * Checks whether username already exists and if yes return list of names started like the username as hint
     * @param username
     * @return
     */
    private ArrayList<String> checkUsernameUniqueNess(String username) {
        ArrayList<String> result = new ArrayList<String>();
        TypedQuery<UserEntity> userQuery =
                em.createQuery("select u from UserEntity u where LOWER(u.username) like :usernameLike and exists " +
                        "(select un from UserEntity un where LOWER(un.username) like :username)", UserEntity.class);
        List<UserEntity> usersWithSimilarNames = userQuery.setParameter("usernameLike", username.toLowerCase() + "%")
                .setParameter("username", username.toLowerCase())
                .getResultList();
        usersWithSimilarNames.forEach(userEntity -> result.add(userEntity.getUsername()));
        return result;
    }

    /**
     * Checks email existance
     * @param email
     * @return
     */
    private boolean isEmailExist(String email) {
        TypedQuery<UserEntity> userQuery =
                em.createQuery("select u from UserEntity u where LOWER(u.email) = :email", UserEntity.class);
        return userQuery.setParameter("email", email.toLowerCase()).getResultList().size() > 0;
    }

    /**
     * Cleans private data and sets next antibot question
     *
     * @param result - RegistrationReply which already can be modified before
     * @param regData - Data typed by user which is not needed to be sent back. It is cleaned
     * @param e - Exception with error
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

    private void  sendResponseError(int code, String text,  HttpServletResponse response) {
        try {
            response.sendError(code, text);
        } catch (Exception ex) {
            //do nothing
        }
    }




}
