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
import wind.instrument.competitions.rest.model.VotingThread;
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
public class OpinionService {
    private static Logger LOG = LoggerFactory.getLogger(DiscussService.class);
    /**
     * Russian messages
     */
    private static ResourceBundle bundle = ResourceBundle.getBundle("Messages");

    private static int PAGE_SIZE = 10;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private HttpSession httpSession;

    /**
     *
     * @param competitionId
     * @param msgStartAfterTime if = -1 get first page without check on updates
     * @param controlDate
     * @param response
     * @return
     */
    @RequestMapping(value = "/api/getVotingOpinions", method = RequestMethod.GET)
    public VotingThread getVotingOpinions(@RequestParam("cId") Long competitionId, @RequestParam(value= "saId", required = false) Long msgStartAfterTime,
                                          @RequestParam(value="cd", required = false) Long controlDate, HttpServletResponse response) {
        VotingThread result = new VotingThread();
        //if controlDate not null and msgStartAfterTime = null do  check for updates
        if (controlDate != null && msgStartAfterTime == null) {
            if (!this.themeHasChanges(competitionId, controlDate)) {
                result.setC(false);
                return result;
            }
        }

        ThemeEntity opinionTheme = this.getVotingThemeByCompetitionId(competitionId);
        boolean isFirstPage =  (msgStartAfterTime == null || msgStartAfterTime.longValue() == -1l);
        Date msgStartAfterDateTime = (msgStartAfterTime != null && msgStartAfterTime.longValue() != -1l) ? new Date(msgStartAfterTime.longValue()) : null;

        //find messages in the theme
        if (opinionTheme != null) {
            result.setId(opinionTheme.getId());
            TypedQuery<MessageEntity> msgQuery = ( isFirstPage )
                    ? em.createQuery("select m from MessageEntity m where  m.themeId = :threadId order by m.created desc",
                            MessageEntity.class)
                    : em.createQuery("select m from MessageEntity m where m.themeId = :threadId and m.created <= :msgStartAfterTime order by m.created desc",
                            MessageEntity.class);
            TypedQuery<Long> allMsgCountQuery = em.createQuery("select count(*) from  MessageEntity m where m.themeId = :threadId", Long.class);
            TypedQuery<Long> youngerMsgCountQuery = em.createQuery("select count(*) from  MessageEntity m where m.themeId = :threadId and m.created < :youngestTime", Long.class);
            try {
                List<MessageEntity> msgList = (isFirstPage)
                    ? msgQuery
                        .setParameter("threadId", opinionTheme.getId())
                        .setMaxResults(PAGE_SIZE)
                        .getResultList()
                    : msgQuery
                        .setParameter("threadId", opinionTheme.getId())
                        .setParameter("msgStartAfterTime", msgStartAfterDateTime)
                        .setMaxResults(PAGE_SIZE)
                        .getResultList();
                Long allMsgCount = allMsgCountQuery.setParameter("threadId", opinionTheme.getId()).getSingleResult();
                if (allMsgCount != null && allMsgCount.longValue() > 0l) {
                    result.setAc(allMsgCount);
                }
                if (!isFirstPage) {
                    Date youngestDateTime = (msgList.size() > 0) ? msgList.get(msgList.size() - 1).getCreated() : msgStartAfterDateTime;
                    Long youngerMsgCount = youngerMsgCountQuery
                            .setParameter("threadId", opinionTheme.getId())
                            .setParameter("youngestTime", youngestDateTime)
                            .getSingleResult();
                    if (youngerMsgCount != null) {
                        result.setYc(youngerMsgCount);
                    }
                }
                result.setOi(ServiceUtil.fillInDiscussionItems(msgList, competitionId));
            } catch (Exception ex) {
                LOG.error("Some error getting VotingOpinions: ",ex);
            }
        }
        return result;
    }


    @RequestMapping(value = "/api/saveOpinion", method = RequestMethod.POST)
    public DiscussionItem saveOpinion(@RequestBody DiscussionItem discussionItem, HttpServletResponse response) {

        if (discussionItem.getMsgText() == null || discussionItem.getMsgText().trim().length() == 0) {
            ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, bundle.getString("EMPTY_MSG_BODY"), response);
            return discussionItem;
        }
        if (discussionItem.getCompetitionId() == null) {
            ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, "Competition id is not set!", response);
            return discussionItem;
        }
        UserEntity currentUser = em.find(UserEntity.class, httpSession.getAttribute("USER_ID"));

        try {
            ThemeEntity opinionTheme = this.getTheme(discussionItem, currentUser, response);
            return persistMessage(discussionItem, opinionTheme, currentUser, response);
        } catch (Exception ex) {
            LOG.error("Error saving voting message: ",ex);
            ServiceUtil.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, bundle.getString("SERVER_ERROR"), response);
            return discussionItem;
        }

    }

    @RequestMapping(value = "/api/deleteOpinion", method = RequestMethod.DELETE)
    public void deleteOpinion(@RequestParam("iid") Long msgId, HttpServletResponse response) {
        UserEntity currentUser = ServiceUtil.findCurrentUser(em, httpSession);
        MessageEntity message = em.find(MessageEntity.class, msgId);
        if (!message.getUserId().equals(currentUser.getUserId())) {
            ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, "Message not belongs to you", response);
            return;
        }
        ThemeEntity themeEntity = em.find(ThemeEntity.class, message.getThemeId());
        em.remove(message);
        themeEntity.setUpdated(new Date());
        em.persist(themeEntity);
    }


    private ThemeEntity getTheme(DiscussionItem discussionItem, UserEntity currentUser, HttpServletResponse response) {
        ThemeEntity opinionTheme = null;
        try {
            opinionTheme = this.getVotingThemeByCompetitionId(discussionItem.getCompetitionId());
            if (opinionTheme == null) {
                opinionTheme = new ThemeEntity();
                opinionTheme.setUserId(currentUser.getUserId());
                opinionTheme.setCompetitionId(discussionItem.getCompetitionId());
                opinionTheme.setName("Voting theme for competition " + discussionItem.getCompetitionId());
                opinionTheme.setThemeType(ThemeType.COMPETITION_VOTING);
                em.persist(opinionTheme);
            }
        } catch (Exception ex) {
            LOG.error("Error getting or saving voting theme: ",ex);
            ServiceUtil.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, bundle.getString("SERVER_ERROR"), response);
            return null;
        }
        return opinionTheme;
    }

    private ThemeEntity getVotingThemeByCompetitionId(Long id) {
        TypedQuery<ThemeEntity> themeQuery =
                em.createQuery("select t from ThemeEntity t where t.themeType = :type and t.competitionId = :cId",
                        ThemeEntity.class);
        try {
            return themeQuery.setParameter("cId", id)
                    .setParameter("type", ThemeType.COMPETITION_VOTING.getValue()).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }


    private DiscussionItem persistMessage(DiscussionItem discussionItem,
                                          ThemeEntity opinionTheme,
                                          UserEntity currentUser,
                                          HttpServletResponse response) {
        MessageEntity message = new MessageEntity();
        if (discussionItem.getMsgId() != null) {
            message = em.find(MessageEntity.class, discussionItem.getMsgId());
            if(opinionTheme != null && !message.getThemeId().equals(opinionTheme.getId())) {
                LOG.error("ERROR: Message " + message.getMsgId() + " doesn't belong to theme " + opinionTheme.getId());
                ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, "\"Bad request! Message \" + message.getMsgId() + \" doesn't belong to theme \" + opinionTheme.getId()", response);
                return discussionItem;
            }
            if(!message.getUserId().equals(currentUser.getUserId())) {
                LOG.error("ERROR: Message " + message.getMsgId() + " doesn't belong to user " + currentUser.getUserId());
                ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, "Bad request! Message doesn't belongs to you", response);
                return discussionItem;
            }
        } else {
            message.setUserId(currentUser.getUserId());
            message.setThemeId(opinionTheme.getId());
            message.setParentMsgId(discussionItem.getParentMsgId());
        }
        message.setMsgBody(discussionItem.getMsgText());
        em.persist(message);

        //change updated for Theme
        //ThemeEntity theme = em.find(ThemeEntity.class, message.getThemeId());
        opinionTheme.setUpdated(message.getUpdated());
        em.persist(opinionTheme);

        //return back
        discussionItem.setMsgId(message.getMsgId());
        discussionItem.setAuthorId(message.getUserId());
        discussionItem.setCreationDate(message.getCreated());
        discussionItem.setUpdateDate(message.getUpdated());
        discussionItem.setMsgThreadId(message.getThemeId());
        return discussionItem;
    }

    private boolean themeHasChanges(Long id, Long controlDate) {
        TypedQuery<Long> themeQuery =
                em.createQuery("select count(*) from ThemeEntity t where t.themeType = :type and t.competitionId = :cId and t.updated > :cDate",
                        Long.class);
        try {
            Date threadControlDate = new Date(controlDate.longValue());
            Long count = themeQuery.setParameter("cId", id)
                    .setParameter("type", ThemeType.COMPETITION_VOTING.getValue())
                    .setParameter("cDate", threadControlDate)
                    .getSingleResult();
            return (count != null && count > 0);
        } catch (NoResultException ex) {
            LOG.debug("Some error getting themeHasChanges: ",ex);
            return true;
        }
    }

}
