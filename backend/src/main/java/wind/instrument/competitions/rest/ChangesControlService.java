package wind.instrument.competitions.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wind.instrument.competitions.data.CompetitionEntity;
import wind.instrument.competitions.data.MessageEntity;
import wind.instrument.competitions.data.ThemeEntity;
import wind.instrument.competitions.data.UserEntity;
import wind.instrument.competitions.rest.model.CompetitionData;
import wind.instrument.competitions.rest.model.changes.ChangesKeywords;
import wind.instrument.competitions.rest.model.changes.ThreadChanges;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

@RestController
@Transactional
public class ChangesControlService {
    private static Logger LOG = LoggerFactory.getLogger(ChangesControlService.class);
    /**
     * Russian messages
     */
    private static ResourceBundle bundle = ResourceBundle.getBundle("Messages");

    @PersistenceContext
    private EntityManager em;

    public static String COMPETITION_LIST = "COMP_LIST";
    public static String DESCRIPTION_FOR_TYPE = "DESC_";
    public static String COMPETITION_MEMBERS_PREFIX = "MBRS_";

    private boolean compDescChanged = false;
    private boolean memberListChanged = false;

    /**
     * @param previousTime - set it -1 so that just to init time point
     * @return
     */
    @RequestMapping(value = "/api/getChangedKeywords", method = RequestMethod.GET)
    public ChangesKeywords getChangedKeywords(@RequestParam("ld") Long previousTime,  HttpServletResponse response) {
        ChangesKeywords result = new ChangesKeywords();
        if(previousTime == null) {
            this.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, "previousTime is not set", response);
            return result;
        }
        result.setKeywords(new ArrayList<String>());

        long currentTime = System.currentTimeMillis();
        if (previousTime > 0 ) {
            TypedQuery<CompetitionEntity> activeCompetitionQuery =
                    em.createQuery("select c from CompetitionEntity c where c.active = true",
                            CompetitionEntity.class);
            try {
                List<CompetitionEntity> competitionList = activeCompetitionQuery.getResultList();
                this.compDescChanged = false;
                this.memberListChanged = false;
                competitionList.forEach((item) -> {
                    if (item.getUpdated().getTime() > previousTime.longValue()) {
                        result.getKeywords().add(DESCRIPTION_FOR_TYPE + item.getCompetitionType().getValue());
                        this.compDescChanged = true;
                    }
                    item.getThemesByMembers().forEach((theme) -> {
                        if (theme.getCreated().getTime() > previousTime.longValue()) {
                            this.memberListChanged = true;
                        }
                    });
                    if (this.memberListChanged ) {
                        result.getKeywords().add(COMPETITION_MEMBERS_PREFIX + item.getCompetitionType().getValue());

                    }
                });

                if (this.compDescChanged) {
                    result.getKeywords().add(COMPETITION_LIST);
                }

            } catch (NoResultException ex) {
                //do nothing
            }
        }
        result.setTime(currentTime);
        return result;
    }

    @RequestMapping(value = "/api/getThreadUpdates", method = RequestMethod.GET)
    public ThreadChanges getThreadUpdates(@RequestParam(value ="uld", required = false) Long userControlTime,
                                            @RequestParam(value ="tld", required = false) Long threadControlTime,
                                            @RequestParam("thid") Long threadId,  HttpServletResponse response) {
        ThreadChanges result = new ThreadChanges();
        result.setThChanged(false);
        ArrayList<Long> msgIds = new ArrayList<Long>();
        ArrayList<Long> userIds = new ArrayList<Long>();
        result.setMsgIds(msgIds);
        result.setUserIds(userIds);
        if(threadId == null) {
            this.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, "threadId is not set", response);
            return result;
        }
        TypedQuery<MessageEntity> msgQuery =
                em.createQuery("select m from MessageEntity m where m.themeId = :thId and m.updated > :ctlTime",
                        MessageEntity.class);
        TypedQuery<UserEntity> usersQuery =
                em.createQuery("select u from UserEntity u where u.updated > :ctlTime and exists " +
                                "(select m from MessageEntity m where m.userId = u.userId and m.themeId = :thId)",
                        UserEntity.class);
        TypedQuery<ThemeEntity> themeQuery =
                        em.createQuery("select t from ThemeEntity t where t.updated > :ctlTime and t.id = :thId",
                                ThemeEntity.class);
        try {

            if (threadControlTime != null)  {
                Date threadControlDate = new Date(threadControlTime.longValue());
                msgQuery.setParameter("thId", threadId)
                    .setParameter("ctlTime", threadControlDate)
                    .getResultList()
                    .forEach((msg) -> {
                        msgIds.add(msg.getMsgId());
                    });
                result.setThChanged(themeQuery.setParameter("thId", threadId)
                    .setParameter("ctlTime", threadControlDate)
                    .getResultList().size() > 0);

            }
            if (userControlTime != null)  {
                Date userControlDate = new Date(userControlTime.longValue());
                usersQuery.setParameter("thId", threadId)
                    .setParameter("ctlTime", userControlDate)
                    .getResultList()
                    .forEach((usr) -> {
                        userIds.add(usr.getUserId());
                    });
            }
        } catch(NoResultException ex) {
            LOG.error("Can not get result for thread: ", ex);
            this.sendResponseError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage(), response);
            return result;
        }

        return result;

    }

    private void sendResponseError(int code, String text,  HttpServletResponse response) {
        try {
            response.sendError(code, text);
        } catch (Exception ex) {
            LOG.error("Something wrong sending error responses", ex);
        }
    }

}
