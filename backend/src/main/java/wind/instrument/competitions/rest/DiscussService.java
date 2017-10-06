package wind.instrument.competitions.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import wind.instrument.competitions.data.MessageEntity;
import wind.instrument.competitions.data.ThemeEntity;
import wind.instrument.competitions.data.ThemeType;
import wind.instrument.competitions.data.UserEntity;
import wind.instrument.competitions.middle.AdminInfo;
import wind.instrument.competitions.rest.model.PartakeThread;
import wind.instrument.competitions.rest.model.discussion.DiscussionItem;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

@RestController
@Transactional
public class DiscussService {
    private static Logger LOG = LoggerFactory.getLogger(DiscussService.class);
    /**
     * Russian messages
     */
    private static ResourceBundle bundle = ResourceBundle.getBundle("Messages");

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private HttpSession httpSession;

    /**
     * Discussion user's thread and all threads for admin for competition
     *
     * @param competitionId - discussed competition id
     * @param response
     * @return
     */
    @RequestMapping(value = "/api/getPartakeDiscuss", method = RequestMethod.GET)
    public PartakeThread getPartakeDiscussion(@RequestParam("cId") Long competitionId, HttpServletResponse response) {
        PartakeThread result = new PartakeThread();
        UserEntity currentUser = ServiceUtil.findCurrentUser(em, httpSession);

        //simple user see own requests and admins replies
        //find theme first
        TypedQuery<ThemeEntity> themeQuery =
                em.createQuery("select t from ThemeEntity t where t.userId = :userId and t.themeType = :type and t.competitionId = :cId",
                        ThemeEntity.class);
        ThemeEntity usersPartakeTheme = null;
        try {
            usersPartakeTheme = themeQuery.setParameter("userId", currentUser.getUserId())
                    .setParameter("type", ThemeType.COMPETITION_REQUEST.getValue())
                    .setParameter("cId", competitionId)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }

        //find messages in the theme
        if (usersPartakeTheme != null) {
            result.setThreadId(usersPartakeTheme.getId());
            result.setThUpdated(usersPartakeTheme.getUpdated());
            TypedQuery<MessageEntity> msgQuery =
                    em.createQuery("select m from MessageEntity m where  m.themeId = :threadId order by m.created",
                            MessageEntity.class);
            try {
                List<MessageEntity> msgList = msgQuery.setParameter("threadId", usersPartakeTheme.getId()).getResultList();
                result.setDiscussionItems(ServiceUtil.fillInDiscussionItems(msgList, competitionId));
            } catch (NoResultException ex) {
            }
        }
        return result;
    }

    /**
     * Admin see all requests
     *
     * @param competitionId
     * @param result
     * @return
     */
    @RequestMapping(value = "/api/getPartakeDiscussForAdmin", method = RequestMethod.GET)
    public PartakeThread getPartakeForAdmin(@RequestParam("cId") Long competitionId, @RequestParam("tId") Long themeId) {
        PartakeThread result = new PartakeThread();
        result.setThreadId(themeId);
        UserEntity currentUser = ServiceUtil.findCurrentUser(em, httpSession);
        if (!AdminInfo.ADMIN_USERNAME.equals(currentUser.getUsername())) {
            return result;
        }
        TypedQuery<MessageEntity> msgQuery =
                em.createQuery("select m from MessageEntity m where  m.themeId = :threadId order by m.created",
                        MessageEntity.class);
        List<MessageEntity> partakeTheme = null;
        try {
            partakeTheme = msgQuery
                    .setParameter("threadId", themeId)
                    .getResultList();
        } catch (NoResultException ex) {
        }
        result.setDiscussionItems(ServiceUtil.fillInDiscussionItems(partakeTheme, competitionId));
        return result;
    }

    @RequestMapping(value = "/api/deletePartake", method = RequestMethod.DELETE)
    public void deletePartakeMessage(@RequestParam("iid") Long itemId, HttpServletResponse response) {
        UserEntity currentUser = ServiceUtil.findCurrentUser(em, httpSession);
        MessageEntity messageEntity = null;
        try {
            messageEntity = em.find(MessageEntity.class, itemId);
        } catch (NoResultException ex) {
            //do nothing
        }
        if (messageEntity == null) {
            //todo translate
            ServiceUtil.sendResponseError(HttpServletResponse.SC_NOT_FOUND, "Message doesn't exist!", response);
        }
        if (!messageEntity.getUserId().equals(currentUser.getUserId())) {
            //todo translate
            ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, "Message belongs to other user! It cannot be deleted!", response);
        } else {
            try {
                ThemeEntity themeEntity = messageEntity.getTheme();
                themeEntity.setUpdated(new Date());
                if (messageEntity.getParentMsgId() == null) {
                    em.remove(messageEntity);
                    if (themeEntity.getUserId() == currentUser.getUserId()) {
                        em.remove(themeEntity);
                    } else {
                        em.persist(themeEntity);
                    }
                } else {
                    em.remove(messageEntity);
                    em.persist(themeEntity);
                }
            } catch (Exception ex) {
                LOG.error("Error deleting partake message: ", ex);
                ServiceUtil.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, bundle.getString("SERVER_ERROR"), response);
            }
        }
    }


    @RequestMapping(value = "/api/deleteTheme", method = RequestMethod.DELETE)
    public void deleteTheme(@RequestParam("thid") Long itemId, HttpServletResponse response) {
        UserEntity currentUser = ServiceUtil.findCurrentUser(em, httpSession);
        ThemeEntity themeEntity = em.find(ThemeEntity.class, itemId);
        if (themeEntity == null) {
            ServiceUtil.sendResponseError(HttpServletResponse.SC_NOT_FOUND, "Item Id is not found!", response);
        }

        if (!themeEntity.getUserId().equals(currentUser.getUserId())) {
            //todo translate
            ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, "Message belongs to other user! It cannot be deleted!", response);
        } else {
            try {
                themeEntity.getMessages().forEach((message) -> {
                    if (message.getParentMsgId() != null) {
                        em.remove(message);
                    }
                });
                themeEntity.getMessages().forEach((rootMessage) -> {
                    em.remove(rootMessage);
                });
                em.remove(themeEntity);
            } catch (Exception ex) {
                LOG.error("Error deleting partake message: ", ex);
                ServiceUtil.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, bundle.getString("SERVER_ERROR"), response);
            }
        }
    }

    @RequestMapping(value = "/api/submitPartake", method = RequestMethod.POST)
    public DiscussionItem submitPartake(@RequestBody DiscussionItem discussionItem, HttpServletResponse response) {

        if (discussionItem.getMsgText() == null || discussionItem.getMsgText().trim().length() == 0) {
            ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, bundle.getString("EMPTY_MSG_BODY"), response);
            return discussionItem;
        }
        if (discussionItem.getCompetitionId() == null) {
            ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, "Competition id is not set!", response);
            return discussionItem;
        }
        UserEntity currentUser = ServiceUtil.findCurrentUser(em, httpSession);
        if (AdminInfo.ADMIN_USERNAME.equals(currentUser.getUsername()) && discussionItem.getParentMsgId() != null) {
            return persistMessage(discussionItem, null, currentUser, response);
        }

        TypedQuery<ThemeEntity> themeQuery =
                em.createQuery("select t from ThemeEntity t where t.userId = :userId and t.themeType = :type and t.competitionId = :cId",
                        ThemeEntity.class);
        try {
            ThemeEntity usersPartakeTheme = null;
            try {
                usersPartakeTheme = themeQuery.setParameter("userId", currentUser.getUserId())
                        .setParameter("cId", discussionItem.getCompetitionId())
                        .setParameter("type", ThemeType.COMPETITION_REQUEST.getValue()).getSingleResult();
            } catch (NoResultException ex) {
                //do nothing
            }
            if (usersPartakeTheme == null) {
                usersPartakeTheme = new ThemeEntity();
                usersPartakeTheme.setUserId(currentUser.getUserId());
                usersPartakeTheme.setCompetitionId(discussionItem.getCompetitionId());
                usersPartakeTheme.setName("Partake theme for competition " + discussionItem.getCompetitionId() +
                        " by " + currentUser.getUsername() + ":" + currentUser.getEmail());
                usersPartakeTheme.setThemeType(ThemeType.COMPETITION_REQUEST);
                em.persist(usersPartakeTheme);
            }
            if (discussionItem.getMsgThreadId() != null && !discussionItem.getMsgThreadId().equals(usersPartakeTheme.getId())) {
                LOG.error("Bad request from client. Tries to save data for theme " + discussionItem.getMsgThreadId());
                ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, "Bad request. The thread doesn't belongs to you!", response);
                return discussionItem;
            }
            return persistMessage(discussionItem, usersPartakeTheme, currentUser, response);
        } catch (Exception ex) {
            LOG.error("Error saving partake message: ", ex);
            ServiceUtil.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, bundle.getString("SERVER_ERROR"), response);
            return discussionItem;
        }

    }


    private DiscussionItem persistMessage(DiscussionItem discussionItem,
                                          ThemeEntity usersPartakeTheme, //null for admin
                                          UserEntity currentUser,
                                          HttpServletResponse response) {
        MessageEntity message = new MessageEntity();
        if (discussionItem.getMsgId() != null) {
            message = em.find(MessageEntity.class, discussionItem.getMsgId());
            if (usersPartakeTheme != null && !message.getThemeId().equals(usersPartakeTheme.getId())) {
                LOG.error("ERROR: Message " + message.getMsgId() + " doesn't belong to theme " + usersPartakeTheme.getId());
                ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, "\"Bad request! Message \" + message.getMsgId() + \" doesn't belong to theme \" + usersPartakeTheme.getId()", response);
                return discussionItem;
            }
            if (!message.getUserId().equals(currentUser.getUserId())) {
                LOG.error("ERROR: Message " + message.getMsgId() + " doesn't belong to user " + currentUser.getUserId());
                ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, "Bad request! Message doesn't belongs to you", response);
                return discussionItem;
            }
        } else {
            message.setUserId(currentUser.getUserId());
            message.setThemeId((usersPartakeTheme != null) ? usersPartakeTheme.getId() : discussionItem.getMsgThreadId());
            message.setParentMsgId(discussionItem.getParentMsgId());
        }
        message.setMsgBody(discussionItem.getMsgText());
        em.persist(message);

        //change updated for Theme
        ThemeEntity theme = em.find(ThemeEntity.class, message.getThemeId());
        theme.setUpdated(message.getUpdated());
        em.persist(theme);

        //return back
        discussionItem.setMsgId(message.getMsgId());
        discussionItem.setAuthorId(message.getUserId());
        discussionItem.setCreationDate(message.getCreated());
        discussionItem.setUpdateDate(message.getUpdated());
        discussionItem.setMsgThreadId(message.getThemeId());
        return discussionItem;
    }


}
