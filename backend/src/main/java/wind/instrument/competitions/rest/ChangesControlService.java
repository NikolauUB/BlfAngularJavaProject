package wind.instrument.competitions.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wind.instrument.competitions.data.*;
import wind.instrument.competitions.rest.model.CompetitionData;
import wind.instrument.competitions.rest.model.changes.ChangesKeywords;
import wind.instrument.competitions.rest.model.changes.ThreadChanges;
import wind.instrument.competitions.rest.model.changes.UsersChanges;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

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

    @Autowired
    private HttpSession httpSession;

    public static String COMPETITION_LIST = "COMP_LIST";
    public static String DESCRIPTION_FOR_TYPE = "DESC_";
    public static String COMPETITION_MEMBERS_PREFIX = "MBRS_";
    public static String COMPETITION_MEMBERS_COUNT = "MBCNT_";
    public static String VOTING_ITEMS_COUNT = "VCNT_";
    public static String VOTING_PREFIX = "V_";

    private boolean compDescChanged = false;
    private int themeCounter = 0;
    private int votingItemCounter = 0;

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
                this.themeCounter = 0;
                this.votingItemCounter = 0;
                competitionList.forEach((item) -> {
                    if (item.getUpdated().getTime() > previousTime.longValue()) {
                        result.getKeywords().add(DESCRIPTION_FOR_TYPE + item.getCompetitionType().getValue());
                        this.compDescChanged = true;
                    }
                    //check partake themes
                    Collection<ThemeEntity> themeEntities = item.getThemesByMembers();
                    this.themeCounter+= (themeEntities != null) ? themeEntities.size() : 0;
                    if ( themeEntities != null && themeEntities.stream().anyMatch(
                            (theme) -> theme.getCreated().getTime() > previousTime.longValue()) ) {
                        result.getKeywords().add(COMPETITION_MEMBERS_PREFIX + item.getCompetitionType().getValue());

                    }

                    //check competition items
                    Collection<CompetitionItemEntity> competitionItems = item.getCompetitionItems();
                    this.votingItemCounter+= (competitionItems != null) ? competitionItems.size() : 0;
                    if( competitionItems != null && competitionItems.stream().anyMatch(
                            (compItem) -> compItem.getUpdated().getTime() > previousTime.longValue()) ) {
                        result.getKeywords().add(VOTING_PREFIX + item.getCompetitionType().getValue());
                    }

                });

                if (this.compDescChanged) {
                    result.getKeywords().add(COMPETITION_LIST);
                }
                //control common member's count
                result.getKeywords().add(COMPETITION_MEMBERS_COUNT + this.themeCounter);
                //control common voting items count
                result.getKeywords().add(VOTING_ITEMS_COUNT + this.votingItemCounter);
            } catch (NoResultException ex) {
                //do nothing
            }
        }
        result.setTime(currentTime);
        return result;
    }

    @RequestMapping(value = "/api/getThreadUpdates", method = RequestMethod.GET)
    public ThreadChanges getThreadUpdates(@RequestParam(value ="tld") Long threadControlTime,
                                            @RequestParam("thid") Long threadId,  HttpServletResponse response) {
        ThreadChanges result = new ThreadChanges();
        result.setThChanged(false);
        if (threadId == null) {
            this.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, "threadId is not set", response);
            return result;
        }

        TypedQuery<MessageEntity> msgQuery =
                em.createQuery("select m from MessageEntity m " +
                                "where m.themeId = :thId and m.userId != :userId and m.updated > :ctlTime",
                        MessageEntity.class);
        TypedQuery<ThemeEntity> themeQuery =
                        em.createQuery("select t from ThemeEntity t where t.updated > :ctlTime and t.id = :thId",
                                ThemeEntity.class);
        try {
            Date threadControlDate = new Date(threadControlTime.longValue());
            boolean changed = (msgQuery.setParameter("thId", threadId)
                    .setParameter("userId", httpSession.getAttribute("USER_ID"))
                    .setParameter("ctlTime", threadControlDate)
                    .getResultList().size() > 0) ;
            result.setThChanged( changed || (themeQuery.setParameter("thId", threadId)
                    .setParameter("ctlTime", threadControlDate)
                    .getResultList().size() > 0));

        } catch(NoResultException ex) {
            LOG.error("Can not get result for thread: ", ex);
            this.sendResponseError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage(), response);
            return result;
        }

        return result;

    }

    @RequestMapping(value = "/api/getUsersUpdates", method = RequestMethod.GET)
    public UsersChanges getUsersUpdates(@RequestParam(value ="uld", required = true) Long userControlTime,
                                            HttpServletResponse response) {
        UsersChanges result = new UsersChanges();
        ArrayList<Long> userIds = new ArrayList<Long>();
        result.setUserIds(userIds);
        if (userControlTime == null) {
            this.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, "userControlTime is not set", response);
            return result;
        }
        TypedQuery<UserEntity> usersQuery =
                em.createQuery("select u from UserEntity u where (u.updated > :ctlTime and exists " +
                                "(select t from ThemeEntity t, CompetitionEntity c " +
                                        "where t.competitionId = c.competitionId " +
                                        "and t.userId = u.userId " +
                                        "and c.active = true)) " +
                                "or (u.updated > :ctlTime  and exists " +
                                "(select ciu from CompetitionItemUsers ciu, CompetitionItemEntity cis, " +
                                        "CompetitionEntity cs " +
                                        "where ciu.competitionItemId = cis.competitionItemId " +
                                        "and cis.competitionId = cs.competitionId " +
                                        "and ciu.userId = u.userId " +
                                        "and cs.active = true))"
                                , UserEntity.class);
        try {
            Date userControlDate = new Date(userControlTime.longValue());
            usersQuery.setParameter("ctlTime", userControlDate)
                    .getResultList()
                    .forEach((usr) -> {
                        userIds.add(usr.getUserId());
                    });
        } catch(NoResultException ex) {
            LOG.error("Can not get result for users updates: ", ex);
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
