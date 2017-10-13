package wind.instrument.competitions.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wind.instrument.competitions.data.MessageEntity;
import wind.instrument.competitions.data.UserEntity;
import wind.instrument.competitions.middle.AdminInfo;
import wind.instrument.competitions.rest.model.discussion.DiscussionItem;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

public class ServiceUtil {

    private static Logger LOG = LoggerFactory.getLogger(CompetitionService.class);

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

}
