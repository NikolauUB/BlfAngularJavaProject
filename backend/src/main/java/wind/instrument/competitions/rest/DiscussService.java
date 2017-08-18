package wind.instrument.competitions.rest;

import wind.instrument.competitions.data.*;
import wind.instrument.competitions.rest.model.ActiveCompetitions;
import wind.instrument.competitions.rest.model.CompetitionData;
import wind.instrument.competitions.rest.model.PartakeThread;
import wind.instrument.competitions.rest.model.discussion.DiscussionItem;
import wind.instrument.competitions.rest.model.status.StatusOfDiscussionItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@Transactional
public class DiscussService {
    private static Logger LOG = LoggerFactory.getLogger(DiscussService.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private HttpSession httpSession;

    @RequestMapping(value = "/api/getActiveCompetitions", method = RequestMethod.GET)
    public ActiveCompetitions getActiveCompetitions() {
        ActiveCompetitions result = new ActiveCompetitions();
        result.setCode(200);
        TypedQuery<CompetitionEntity> activeCometQuery =
                em.createQuery("select c from CompetitionEntity c where c.active = true",
                        CompetitionEntity.class);
        try {
            List<CompetitionEntity>  competList = activeCometQuery.getResultList();
            ArrayList<CompetitionData> list = new ArrayList<CompetitionData>();
            competList.forEach((item)->{
                list.add(new CompetitionData(
                        item.getCompetitionId(),
                        item.getCompetitionName(),
                        item.getCompetitionType().getValue(),
                        item.getCompetitionDesc(),
                        item.getCompetitionStart(),
                        item.getCompetitionEnd()));
            });
            result.setActiveList(list);
        } catch (NoResultException ex) {
            result.setCode(404);
            result.setErrorMsg("No active competitions are not available");
        }
        return result;
    }

    //todo it in future
    /*@RequestMapping(value = "/api/getPartakeChanges", method = RequestMethod.GET)
    public PartakeThreadChanges getPartakeChanges(@RequestParam("time") Long time, @RequestParam("threadId") Long threadId) {
        PartakeThreadChanges result = new PartakeThreadChanges();
        result.setCode(200);
        if (time == null) {
            result.setCode(400);
            result.setErrorMsg("Time is not set!");
            return result;
        }

        if (threadId == null) {
            result.setCode(400);
            result.setErrorMsg("Thread id is not set!");
            return result;
        }

        TypedQuery<MessageEntity> messagesQuery =
                em.createQuery("select m from MessageEntity m where m.themeId = :themeId and m.updated > :time",
                        MessageEntity.class);
        try {
            List<MessageEntity> messList = messagesQuery.setParameter("themeId", threadId).setParameter("time", new Date(time)).getResultList();
            for (MessageEntity mess: messList) {
                if (mess.getHidden()) {
                    result.getDeletedIds().add(mess.getMsgId());
                } else {
                    result.getChangedIds().add(mess.getMsgId());
                }
            }
            return result;
        } catch (NoResultException ex) {
            LOG.debug("Error getting partake changes: ",ex);
            result.setCode(500);
            result.setErrorMsg("Server error getting partake changes!");
            return result;
        }
    }*/

    @RequestMapping(value = "/api/getPartakeDiscuss", method = RequestMethod.GET)
    public PartakeThread getPartakeDiscussion(@RequestParam("cId") Long competitionId) {
        PartakeThread result = new PartakeThread();
        result.setCode(200);
        if (httpSession.getAttribute("USER_ID") == null) {
            result.setCode(403);
            result.setErrorMsg("Login first please");
            return result;
        }
        if (competitionId == null) {
            result.setCode(400);
            result.setErrorMsg("Competition id is not set!");
            return result;
        }
        UserEntity currentUser = em.find(UserEntity.class, httpSession.getAttribute("USER_ID"));
        if (AuthService.ADMIN_USERNAME.equals(currentUser.getUsername())) {
            return getAllPartakesForAdmin(competitionId, result);
        }

        TypedQuery<ThemeEntity> themeQuery =
                em.createQuery("select t from ThemeEntity t where t.userId = :userId and t.themeType = :type and t.competitionId = :cId",
                        ThemeEntity.class);
        ThemeEntity usersPartakeTheme = null;
        try {
            usersPartakeTheme = themeQuery.setParameter("userId", currentUser.getUserId())
                    .setParameter("type", ThemeType.COMPETITION_REQUEST.getValue())
                    .setParameter("cId", competitionId)
                    .getSingleResult();
        } catch (NoResultException ex) { }
        if (usersPartakeTheme != null) {
            TypedQuery<MessageEntity> msgQuery =
                    em.createQuery("select m from MessageEntity m where  m.themeId = :threadId",
                            MessageEntity.class);
            try {
                List<MessageEntity> msgList = msgQuery.setParameter("threadId", usersPartakeTheme.getId()).getResultList();
                this.fillInDiscussionItems(msgList, competitionId, result);
            } catch (NoResultException ex) { }
        }
        return result;
    }

    private PartakeThread getAllPartakesForAdmin(Long competitionId, PartakeThread result) {
        TypedQuery<MessageEntity> msgQuery =
                em.createQuery("select m from MessageEntity m," +
                                " ThemeEntity t where  m.themeId = t.id and t.themeType = :type and t.competitionId = :cId",
                        MessageEntity.class);
        List<MessageEntity> allPartakeTheme = null;
        try {
            allPartakeTheme = msgQuery
                    .setParameter("type", ThemeType.COMPETITION_REQUEST.getValue())
                    .setParameter("cId", competitionId)
                    .getResultList();
        } catch (NoResultException ex) { }
        this.fillInDiscussionItems(allPartakeTheme, competitionId, result);
        return result;
    }

    private void fillInDiscussionItems(List<MessageEntity> messages, Long competitionId, PartakeThread result) {
        ArrayList<DiscussionItem> discussionItemList = new ArrayList<DiscussionItem>();
        for (MessageEntity item : messages) {
            DiscussionItem discussionItem = new DiscussionItem();
            discussionItem.setCompetitionId(competitionId);
            discussionItem.setAuthorId(item.getUserId());
            discussionItem.setUpdateDate(item.getUpdated());
            discussionItem.setCreationDate(item.getCreated());
            discussionItem.setMsgId(item.getMsgId());
            discussionItem.setMsgText(item.getMsgBody());
            discussionItem.setMsgThreadId(item.getThemeId());
            discussionItem.setParentMsgId(item.getParentMsgId());
            discussionItemList.add(discussionItem);
        }
        result.setDiscussionItems(discussionItemList);
    }

    @RequestMapping(value = "/api/deletePartake", method = RequestMethod.DELETE)
    public StatusOfDiscussionItem deletePartakeMessage(@RequestParam("iid") Long itemId) {
        StatusOfDiscussionItem result = new StatusOfDiscussionItem();
        result.setCode(200);
        if (httpSession.getAttribute("USER_ID") == null) {
            result.setCode(403);
            result.setErrorMsg("Login first please");
            return result;
        }
        if (itemId == null) {
            result.setCode(400);
            result.setErrorMsg("Item Id is not set!");
            return result;
        }
        UserEntity currentUser = em.find(UserEntity.class, httpSession.getAttribute("USER_ID"));
        MessageEntity messageEntity = null;
        try {
            messageEntity = em.find(MessageEntity.class, itemId);
        } catch (NoResultException ex) {
            //do nothing
        }
        if (messageEntity == null) {
            result.setCode(404);
            result.setErrorMsg("Message doesn't exist!");
            return result;
        }
        if (messageEntity.getUserId() != currentUser.getUserId()) {
            result.setCode(403);
            result.setErrorMsg("Message belongs to other user! It cannot be deleted!");
            return result;
        } else {
            try {
                if(messageEntity.getParentMsgId() == null) {
                    ThemeEntity themeEntity = em.find(ThemeEntity.class, messageEntity.getThemeId());
                    em.remove(messageEntity);
                    if(themeEntity.getUserId() == currentUser.getUserId()) {
                        em.remove(themeEntity);
                    }
                } else {
                    em.remove(messageEntity);
                }

            } catch(Exception ex) {
                LOG.debug("Error deleting partake message: ", ex);
                result.setCode(500);
                result.setErrorMsg("Server error when deleting message");
            }
        }

        return result;
    }


    @RequestMapping(value = "/api/submitPartake", method = RequestMethod.POST)
    public StatusOfDiscussionItem submitPartake(@RequestBody DiscussionItem discussionItem) {
        StatusOfDiscussionItem result = new StatusOfDiscussionItem();
        result.setCode(200);
        if (httpSession.getAttribute("USER_ID") == null) {
            result.setCode(403);
            result.setErrorMsg("Login first please");
            return result;
        }
        if(discussionItem.getMsgText() == null || discussionItem.getMsgText().length() == 0) {
            result.setCode(400);
            result.setErrorMsg("Text can not be empty!");
            return result;
        }
        if (discussionItem.getCompetitionId() == null) {
            result.setCode(400);
            result.setErrorMsg("Competition id is not set!");
            return result;
        }
        UserEntity currentUser = em.find(UserEntity.class, httpSession.getAttribute("USER_ID"));
        if (AuthService.ADMIN_USERNAME.equals(currentUser.getUsername())) {
            return persistMessage(discussionItem, null, currentUser, result);
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
            if (discussionItem.getMsgThreadId() != null && discussionItem.getMsgThreadId() != usersPartakeTheme.getId()) {
                LOG.info("Bad request from client. Tries to save data for theme " + discussionItem.getMsgThreadId());
                result.setCode(400);
                result.setErrorMsg("Bad request. The thread doesn't belongs to you!");
                return result;
            }
            return persistMessage(discussionItem, usersPartakeTheme, currentUser, result);
        } catch (Exception ex) {
            LOG.debug("Error saving partake message: ",ex);
            result.setCode(500);
            result.setErrorMsg("Server error saving partake changes!");
            return result;
        }

    }

    private StatusOfDiscussionItem persistMessage(DiscussionItem discussionItem,
                                ThemeEntity usersPartakeTheme, //null for admin
                                UserEntity currentUser,
                                StatusOfDiscussionItem result) {
        MessageEntity message = new MessageEntity();
        if(discussionItem.getMsgId() != null) {
            message = em.find(MessageEntity.class, discussionItem.getMsgId());
            if(usersPartakeTheme != null && message.getThemeId() != usersPartakeTheme.getId()) {
                LOG.debug("ERROR: Message " + message.getMsgId() + " doesn't belong to theme " + usersPartakeTheme.getId());
                result.setCode(403);
                result.setErrorMsg("Bad request Message " + message.getMsgId() + " doesn't belong to theme " + usersPartakeTheme.getId());
                return result;
            }
            if(message.getUserId() != currentUser.getUserId()) {
                LOG.debug("ERROR: Message " + message.getMsgId() + " doesn't belong to user " + currentUser.getUserId());
                result.setCode(403);
                result.setErrorMsg("Bad request Message doesn't belongs you");
                return result;
            }
        } else {
            message.setUserId(currentUser.getUserId());
            message.setThemeId((usersPartakeTheme != null) ? usersPartakeTheme.getId() : discussionItem.getMsgThreadId());
            message.setParentMsgId(discussionItem.getParentMsgId());
        }
        message.setMsgBody(discussionItem.getMsgText());
        em.persist(message);
        //return back
        discussionItem.setMsgId(message.getMsgId());
        discussionItem.setAuthorId(message.getUserId());
        discussionItem.setCreationDate(message.getCreated());
        discussionItem.setUpdateDate(message.getUpdated());
        discussionItem.setMsgThreadId(message.getThemeId());
        result.setItem(discussionItem);
        return result;
    }
}
