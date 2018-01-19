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
import java.util.stream.Collectors;

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

    private final static String COMPETITION_LIST = "COMP_LIST";
    private final static String COMPETITION_FUTURE_LIST = "FUTURE_LIST";
    private final static String DESCRIPTION_FOR_TYPE = "DESC_";
    private final static String DESCRIPTION_FOR_FUTURE_TYPE = "DESC_F_";
    private final static String COMPETITION_MEMBERS_PREFIX = "MBRS_";
    private final static String COMPETITION_MEMBERS_COUNT = "MBCNT_";
    private final static String VOTING_ITEMS_COUNT = "VCNT_";
    private final static String VOTING_PREFIX = "V_";

    private boolean compDescChanged = false;
    private boolean compDescFutureChanged = false;
    private int themeCounter = 0;
    private int votingItemCounter = 0;
    private final static int ACTIVE_FEST_DISCUSSION_CODE = 0;

    /**
     * @param previousTime - set it -1 so that just to init time point
     * @return
     */
    @RequestMapping(value = "/api/getChangedKeywords", method = RequestMethod.GET)
    public ChangesKeywords getChangedKeywords(@RequestParam("ld") Long previousTime, HttpServletResponse response) {
        ChangesKeywords result = new ChangesKeywords();
        result.setKeywords(new ArrayList<String>());

        long currentTime = System.currentTimeMillis();
        if (previousTime > 0) {
            TypedQuery<CompetitionEntity> activeCompetitionQuery =
                    em.createQuery("select c from CompetitionEntity c where c.active = true or c.future = true",
                            CompetitionEntity.class);
            TypedQuery<ThemeEntity> partakeThemeQuery =
                    em.createQuery("select t from ThemeEntity t where t.competitionId = :compId and t.themeType = 1 " +
                                        "and exists (select m from MessageEntity m where m.themeId = t.id)",
                            ThemeEntity.class);
            try {
                List<CompetitionEntity> competitionList = activeCompetitionQuery.getResultList();
                this.compDescChanged = false;
                this.compDescFutureChanged = false;
                this.themeCounter = 0;
                this.votingItemCounter = 0;
                competitionList.forEach((item) -> {
                    if (item.getUpdated().getTime() > previousTime.longValue()) {
                        if (item.getFuture()) {
                            result.getKeywords().add(DESCRIPTION_FOR_FUTURE_TYPE + item.getCompetitionType().getValue());
                            this.compDescFutureChanged = true;
                        }
                        if (item.getActive()) {
                            result.getKeywords().add(DESCRIPTION_FOR_TYPE + item.getCompetitionType().getValue());
                            this.compDescChanged = true;
                        }
                    }
                    //check partake themes
                    if (item.getFuture()) {
                        Collection<ThemeEntity> themeEntities =
                                partakeThemeQuery.setParameter("compId", item.getCompetitionId()).getResultList();

                        this.themeCounter += (themeEntities != null) ? themeEntities.size() : 0;
                        if (themeEntities != null && themeEntities.stream().anyMatch(
                                (theme) -> theme.getCreated().getTime() > previousTime.longValue())) {
                            result.getKeywords().add(COMPETITION_MEMBERS_PREFIX + item.getCompetitionType().getValue());

                        }
                    }

                    //check competition items
                    if (item.getActive()) {
                        Collection<CompetitionItemEntity> competitionItems = item.getCompetitionItems();
                        if (!ServiceUtil.isAdmin(em, httpSession)) {
                            competitionItems =
                                    competitionItems.stream().filter(itm -> (itm.getActive() != null && itm.getActive()))
                                            .collect(Collectors.toList());
                        }
                        this.votingItemCounter += (competitionItems != null) ? competitionItems.size() : 0;
                        if (competitionItems != null && competitionItems.stream().anyMatch(
                                (compItem) -> compItem.getUpdated().getTime() > previousTime.longValue())) {
                            result.getKeywords().add(VOTING_PREFIX + item.getCompetitionType().getValue());
                        }
                    }

                });

                if (this.compDescChanged) {
                    result.getKeywords().add(COMPETITION_LIST);
                }
                if (this.compDescFutureChanged) {
                    result.getKeywords().add(COMPETITION_FUTURE_LIST);
                    result.getKeywords().add(COMPETITION_MEMBERS_PREFIX + "0");
                    result.getKeywords().add(COMPETITION_MEMBERS_PREFIX + "1");
                    result.getKeywords().add(COMPETITION_MEMBERS_PREFIX + "2");
                    result.getKeywords().add(COMPETITION_MEMBERS_PREFIX + "3");
                    result.getKeywords().add(COMPETITION_MEMBERS_PREFIX + "4");
                }
                //control common member's count
                result.getKeywords().add(COMPETITION_MEMBERS_COUNT + this.themeCounter);
                //control common voting items count
                result.getKeywords().add(VOTING_ITEMS_COUNT + this.votingItemCounter);
            } catch (Exception ex) {
                LOG.error("Error in getChangedKeywords: ", ex);
            }
        }
        result.setTime(currentTime);
        return result;
    }

    @RequestMapping(value = "/api/getActiveThreadUpdates", method = RequestMethod.GET)
    public HashMap<Long, Long> getActiveThreadUpdates(@RequestParam(value = "tld") Long threadControlTime, HttpServletResponse response) {
        HashMap<Long, Long> result = new HashMap<Long, Long>();
        TypedQuery<Object[]> newMessagesQuery =
                em.createQuery("select c.competitionId, count(*), c.competitionType from MessageEntity m, CompetitionThemeEntity ct, CompetitionEntity c " +
                                "where m.themeId = ct.themeId and ct.competitionId = c.competitionId and c.active = true and m.created > :time " +
                                "group by c.competitionId, c.competitionType",
                        Object[].class);

        try {
            Date threadControlDate = new Date(threadControlTime.longValue());
            List<Object[]> countList = newMessagesQuery.setParameter("time", threadControlDate).getResultList();
            for(Object[] idAndCount : countList) {
                Integer type = (Integer)idAndCount[2];
                if (type.equals(CompetitionType.PRESCRIBED_BAROQUE.getValue()) ||
                        type.equals(CompetitionType.PRESCRIBED_JAZZ.getValue()) ||
                        type.equals(CompetitionType.FREE.getValue())) {
                    result.put(Long.valueOf(ACTIVE_FEST_DISCUSSION_CODE), (Long)idAndCount[1]);
                } else {
                    result.put((Long)idAndCount[0], (Long)idAndCount[1]);
                }

            }
        } catch (NoResultException ex) {
            LOG.error("Can not get result for count new msgs for active competitions: ", ex);
            ServiceUtil.sendResponseError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage(), response);
            return result;
        }
        return result;

    }

    @RequestMapping(value = "/api/getThreadUpdates", method = RequestMethod.GET)
    public ThreadChanges getThreadUpdates(@RequestParam(value = "tld") Long threadControlTime,
                                          @RequestParam("thid") Long threadId, HttpServletResponse response) {
        ThreadChanges result = new ThreadChanges();
        result.setThChanged(false);

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
                    .getResultList().size() > 0);
            result.setThChanged(changed || (themeQuery.setParameter("thId", threadId)
                    .setParameter("ctlTime", threadControlDate)
                    .getResultList().size() > 0));

        } catch (NoResultException ex) {
            LOG.error("Can not get result for thread: ", ex);
            ServiceUtil.sendResponseError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage(), response);
            return result;
        }

        return result;

    }

    @RequestMapping(value = "/api/getUsersUpdates", method = RequestMethod.GET)
    public UsersChanges getUsersUpdates(@RequestParam(value = "uld", required = true) Long userControlTime,
                                        HttpServletResponse response) {
        UsersChanges result = new UsersChanges();
        ArrayList<Long> userIds = new ArrayList<Long>();
        result.setUserIds(userIds);

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
        } catch (NoResultException ex) {
            LOG.error("Can not get result for users updates: ", ex);
            ServiceUtil.sendResponseError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage(), response);
            return result;
        }
        return result;
    }

}
