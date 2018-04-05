package wind.instrument.competitions.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;
import wind.instrument.competitions.data.MessageEntity;
import wind.instrument.competitions.data.UserEntity;
import wind.instrument.competitions.middle.AdminInfo;
import wind.instrument.competitions.middle.Utils;
import wind.instrument.competitions.rest.model.discussion.DiscussionItem;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceUtil {

    private static Logger LOG = LoggerFactory.getLogger(CompetitionService.class);
    private final static String VK_USER_EXPIRE_PATTERN = "(expire=)[0-9]+";
    private final static String VK_USER_ID_PATTERN = "(mid=)[0-9]+";
    private final static String VK_SECRET_PATTERN = "(secret=)[0-9a-fA-F]+";
    private final static String VK_SID_PATTERN = "(sid=)[0-9a-fA-F]+";
    private final static String VK_SIG_PATTERN = "(sig=)[0-9a-fA-F]+";

    public static void sendResponseError(int code, String text, HttpServletResponse response) {
        try {
            response.sendError(code, text);
        } catch (Exception ex) {
            LOG.error("Something wrong sending error responses", ex);
        }
    }

    protected static UserEntity findCurrentUser(EntityManager em, HttpSession httpSession) {
        if (httpSession.getAttribute("USER_ID") != null) {
            return em.find(UserEntity.class, httpSession.getAttribute("USER_ID"));
        } else {
            return null;
        }
    }

    protected static boolean isAdmin(EntityManager em, HttpSession httpSession) {
       UserEntity user =  ServiceUtil.findCurrentUser(em, httpSession);
       return (user != null) && AdminInfo.ADMIN_USERNAME.equals(user.getUsername());
    }

    protected static boolean isAdmin(UserEntity user) {
        return (user != null) && AdminInfo.ADMIN_USERNAME.equals(user.getUsername());
    }

    /**
     * Checks whether username already exists and if yes return list of names started like the username as hint
     *
     * @param username
     * @return
     */
    protected static ArrayList<String> checkUsernameUniqueNess(String username, EntityManager em) {
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
     *
     * @param email
     * @return
     */
    protected static boolean isEmailExist(String email, EntityManager em) {
        TypedQuery<UserEntity> userQuery =
                em.createQuery("select u from UserEntity u where LOWER(u.email) = :email", UserEntity.class);
        return userQuery.setParameter("email", email.toLowerCase()).getResultList().size() > 0;
    }

    protected static ArrayList<DiscussionItem> fillInDiscussionItems(List<MessageEntity> messages, Long competitionId)  {
        ArrayList<DiscussionItem> itemList = new ArrayList<DiscussionItem>();
        for (MessageEntity item : messages) {
            DiscussionItem dItem = new DiscussionItem();
            dItem.setCompetitionId(competitionId);
            dItem.setAuthorId(item.getUserId());
            dItem.setUpdateDate(item.getUpdated());
            dItem.setCreationDate(item.getCreated());
            dItem.setMsgId(item.getMsgId());
            dItem.setMsgText(item.getMsgBody());
            dItem.setMsgThreadId(item.getThemeId());
            dItem.setParentMsgId(item.getParentMsgId());
            itemList.add(dItem);
        }
        return itemList;
    }


    /************************
     * VK autherization
     *************************/
    public static String getVKAttribute(String cookieLine, String regexp) {
        Pattern pattern = Pattern.compile(regexp);
        Matcher m = pattern.matcher(cookieLine);
        if (m.find()) {
            return m.group();
        }
        return "";
    }

    public static boolean checkVKAuth(String expire, String userId, String secret, String sid, String sig) {
        if (sig == null || sig.indexOf("=") == -1) {
            return false;
        }
        try {
            byte[] keyBytes = String.format("%s%s%s%s", expire, userId, secret, sid).getBytes("UTF-8");
            String value = sig.split("=")[1];
            String hash = DigestUtils.md5DigestAsHex(keyBytes);
            return hash.equals(value);
        } catch (UnsupportedEncodingException e) {
            LOG.error("Cannot check MD5 (Encoding Issue): ", e);
            return false;
        }
    }

    public static boolean checkUserVKHash(String userId, String hash) {
        if (userId == null || userId.indexOf("=") == -1) {
            return false;
        }
        try {
            String value = userId.split("=")[1];
            byte[] keyBytes = String.format("%s%s%s", Utils.VK_APP_ID, value, AdminInfo.VK_KEY).getBytes("UTF-8");
            String userHash = DigestUtils.md5DigestAsHex(keyBytes);
            return userHash.equals(hash);
        } catch (UnsupportedEncodingException e) {
            LOG.error("Cannot check MD5 (Encoding Issue): ", e);
            return false;
        }
    }

    public static boolean isCookieExpired(String expired) {
        if (expired == null || expired.indexOf("=") == -1) {
            return false;
        }
        Date expDate = new Date();
        expDate.setTime(Integer.parseInt(expired.split("=")[1])*1000l);
        return expDate.before(new Date());
    }

    public static boolean isVKUserCorrect(String vkApp, long uid, String hash, HttpServletResponse response) {
        String vkExpire = ServiceUtil.getVKAttribute(vkApp, VK_USER_EXPIRE_PATTERN);
        String vkUserId = ServiceUtil.getVKAttribute(vkApp, VK_USER_ID_PATTERN);
        String secret = ServiceUtil.getVKAttribute(vkApp, VK_SECRET_PATTERN);
        String sid = ServiceUtil.getVKAttribute(vkApp, VK_SID_PATTERN);
        String sig = ServiceUtil.getVKAttribute(vkApp, VK_SIG_PATTERN);

        //todo exception about expired
        //if (ServiceUtil.isCookieExpired(vkExpire)) {
        //    ServiceUtil.sendResponseError(HttpServletResponse.SC_FORBIDDEN, "VK connection is expired!", response);
        //    return false;
        //}

        //check userId
        if (Long.parseLong(vkUserId.split("=")[1]) != uid) {
            ServiceUtil.sendResponseError(HttpServletResponse.SC_FORBIDDEN, "VK user ids are not equals!", response);
            return false;
        }

        if (!ServiceUtil.checkVKAuth(vkExpire, vkUserId, secret, sid, sig)) {
            ServiceUtil.sendResponseError(HttpServletResponse.SC_FORBIDDEN, "VK user are not connected to VK API!", response);
            return false;
        }

        if (!ServiceUtil.checkUserVKHash(vkUserId, hash)) {
            ServiceUtil.sendResponseError(HttpServletResponse.SC_FORBIDDEN, "VK user hash is not valid!", response);
            return false;
        }

        return true;

    }


}
