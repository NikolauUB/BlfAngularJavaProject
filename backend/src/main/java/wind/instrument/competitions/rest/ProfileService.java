package wind.instrument.competitions.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import wind.instrument.competitions.configuration.SessionParameters;
import wind.instrument.competitions.data.UserEntity;
import wind.instrument.competitions.middle.ReservedUsernames;
import wind.instrument.competitions.middle.Utils;
import wind.instrument.competitions.rest.model.EmailData;
import wind.instrument.competitions.rest.model.PasswordData;
import wind.instrument.competitions.rest.model.ProfileChangedReply;
import wind.instrument.competitions.rest.model.UserData;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.ResourceBundle;

@RestController
@Transactional
public class ProfileService {
    private static Logger LOG = LoggerFactory.getLogger(AuthService.class);
    /**
     * Russian messages
     */
    private static ResourceBundle bundle = ResourceBundle.getBundle("Messages");

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private HttpSession httpSession;

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
        return this.getUserProfile((Long) httpSession.getAttribute(SessionParameters.USER_ID.name()));
    }

    @RequestMapping(value = "/api/getUserDetails", method = RequestMethod.GET)
    public UserData getUserDetails(@RequestParam("uid") Long userId, HttpServletResponse response) {
        return this.getUserProfile(userId);
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

    @RequestMapping(value = "/api/deleteprofileimage", method = RequestMethod.DELETE)
    public ProfileChangedReply restDeleteProfileImage(HttpServletRequest request, HttpServletResponse response) {
        ProfileChangedReply result = new ProfileChangedReply();
        UserEntity currentUser = em.find(UserEntity.class, httpSession.getAttribute(SessionParameters.USER_ID.name()));

        if (currentUser == null) {
            ServiceUtil.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
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
            ServiceUtil.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getLocalizedMessage(),
                    response);
        }
        return result;
    }

    /**
     * Change profile username and image can be replaced
     *
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
            ServiceUtil.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    bundle.getString("SESSION_ISSUE"),
                    response);
        }
        if (userData.getUsername().indexOf("@") != -1) {
            return this.prepareProfileReplyWithError(result,
                    userData,
                    new Exception("Username can not contain @"),
                    HttpServletResponse.SC_BAD_REQUEST);
        }

        if (!currentUser.getUsername().trim().toLowerCase().equals(userData.getUsername().trim().toLowerCase())
                && ReservedUsernames.RESERVED_USERNAMES.isUsernameReserved(userData.getUsername().trim().toLowerCase())
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
            ArrayList<String> existingUsernames = ServiceUtil.checkUsernameUniqueNess(userData.getUsername().trim(), em);
            if (existingUsernames.size() > 0) {
                return this.prepareProfileReplyWithError(result,
                        userData,
                        new Exception(MessageFormat.format(
                                bundle.getString((existingUsernames.size() == 1) ? "USERNAME_EXISTS" : "USERNAME_EXISTING_LIST"),
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
                ServiceUtil.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        e.getLocalizedMessage(),
                        response);
            }
        }
        userData.setSaveImage(null);
        result.setUserData(userData);
        return result;
    }

    /**
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


    /*******************
     * Password Section
     ******************/
    @RequestMapping(value = "/api/changepasswordtid", method = RequestMethod.PUT)
    public int restChangePasswordByToken(@RequestBody PasswordData passwordData, HttpServletResponse response) {
        ServiceUtil.sendResponseError(HttpServletResponse.SC_NOT_IMPLEMENTED,
                "Сменя пароля по ссылке из электронной почты еще не реализована",
                response);
        return HttpServletResponse.SC_OK;
    }

    @RequestMapping(value = "/api/changepassword", method = RequestMethod.PUT)
    public int restChangePassword(@RequestBody PasswordData passwordData, HttpServletResponse response) {
        //change password
        if (passwordData.getToken() == null) {
            UserEntity currentUser = em.find(UserEntity.class, httpSession.getAttribute(SessionParameters.USER_ID.name()));
            if (currentUser == null) {
                ServiceUtil.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        bundle.getString("SESSION_ISSUE"),
                        response);
            }
            //check old password
            if (passwordData.getOldPassword() == null || passwordData.getOldPassword().trim().length() == 0) {
                ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST,
                        bundle.getString("NO_OLD_PASSWORD"),
                        response);
            }
            //check new password
            if (passwordData.getNewPassword() == null || passwordData.getNewPassword().trim().length() == 0) {
                ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST,
                        bundle.getString("NO_NEW_PASSWORD"),
                        response);
            }

            String encryptedPass = null;
            try {
                encryptedPass = Utils.encryptPassword(passwordData.getOldPassword(), currentUser.getPasswordSalt());
            } catch (NoSuchAlgorithmException e) {
                LOG.error("Error when generating old password hash in restSaveProfile", e);
                ServiceUtil.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        e.getLocalizedMessage(),
                        response);
            }

            if (encryptedPass != null && encryptedPass.equals(currentUser.getPasswordHash())) {
                currentUser.setTypedPassword(passwordData.getNewPassword());
                try {
                    currentUser.generatePasswordHashes();
                } catch (NoSuchAlgorithmException e) {
                    LOG.error("Error when generating password hash in restSaveProfile", e);
                    ServiceUtil.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            e.getLocalizedMessage(),
                            response);
                }
                //save new password
                try {
                    em.persist(currentUser);
                } catch (Exception e) {
                    LOG.error("Error when saving new user password", e);
                    ServiceUtil.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            e.getLocalizedMessage(),
                            response);
                }
            } else {
                ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST,
                        bundle.getString("OLD_PASSWORD_WRONG"),
                        response);
            }

        }
        return HttpServletResponse.SC_OK;
    }


    /*******************
     * Email Section
     ******************/
    @RequestMapping(value = "/api/changeemail", method = RequestMethod.PUT)
    public int restChangeEmail(@RequestBody EmailData emailData, HttpServletResponse response) {
        UserEntity currentUser = ServiceUtil.findCurrentUser(em, httpSession);
        if (currentUser == null) {
            ServiceUtil.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    bundle.getString("SESSION_ISSUE"),
                    response);
        }
        if (emailData.getNewEmail() != null && emailData.getNewEmail().indexOf("@") == -1) {
            ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST,
                    "Email must contain @",
                    response);
            return HttpServletResponse.SC_BAD_REQUEST;
        }
        //change email
        if (emailData.getNewEmail() != null && emailData.getNewEmail().trim().length() > 0) {
            if (emailData.getOldEmail() != null && emailData.getOldEmail().trim().length() > 0) {
                if (currentUser.getEmail().toLowerCase().equals(emailData.getOldEmail().trim().toLowerCase())) {
                    if (ServiceUtil.isEmailExist(emailData.getNewEmail().trim(), em)) {
                        ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST,
                                bundle.getString("REG_EMAIL_EXISTS"),
                                response);
                    } else {
                        currentUser.setEmail(emailData.getNewEmail().trim().toLowerCase());
                        //save new email
                        try {
                            em.persist(currentUser);
                        } catch (Exception e) {
                            LOG.error("Error when saving new user email", e);
                            ServiceUtil.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                    e.getLocalizedMessage(),
                                    response);
                        }
                    }
                } else {
                    ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST,
                            bundle.getString("OLD_EMAIL_WRONG"),
                            response);
                }
            } else {
                ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST,
                        bundle.getString("OLD_EMAIL_EMPTY"),
                        response);
            }
        } else {
            ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST,
                    bundle.getString("NEW_EMAIL_EMPTY"),
                    response);
        }
        return HttpServletResponse.SC_OK;
    }


}
