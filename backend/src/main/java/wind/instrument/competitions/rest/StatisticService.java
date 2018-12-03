package wind.instrument.competitions.rest;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wind.instrument.competitions.data.*;
import wind.instrument.competitions.rest.model.votestatistic.UserCompetition;
import wind.instrument.competitions.rest.model.votestatistic.UserStatisticHistory;
import wind.instrument.competitions.rest.model.votestatistic.VoteStatistic;
import wind.instrument.competitions.rest.model.votestatistic.VoterRecord;
import wind.instrument.competitions.service.StatisticCacheService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;


@RestController
@Transactional(propagation =  Propagation.SUPPORTS, readOnly = true)
@CacheConfig(cacheNames = "allStatistic")
public class StatisticService {

    private static Logger LOG = LoggerFactory.getLogger(VoteDataService.class);
    /**
     * Russian messages
     */
    private static ResourceBundle bundle = ResourceBundle.getBundle("Messages");

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private HttpSession httpSession;


    @Autowired
    private StatisticCacheService statisticService;

    @RequestMapping(value = "/api/allstatistic", method = RequestMethod.GET)
    public List<UserStatisticHistory> getAllStatistic() {
        List<UserStatisticHistory> result = statisticService.getAllStatistic();
        if (result == null) {
            result = statisticService.putAllStatistic();
        }
        return result;
    }

    @RequestMapping(value = "/api/updatestatistic", method = RequestMethod.GET)
    @Transactional(propagation =  Propagation.REQUIRED, readOnly = false)
    public String updateStatistic(HttpServletResponse response) throws Exception{
        if (!ServiceUtil.isAdmin(em, httpSession)) {
            try {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not administrator");
            } catch (Exception ex) {}
            return "Error";
        }
        Map<Long,Integer> readyToSave = this.calculateUsersStatistic(response);
        Iterator<Long> userIds = readyToSave.keySet().iterator();
        while (userIds.hasNext()) {
            Long userId = userIds.next();
            CompetitionVotingSummary userSummary = em.find(CompetitionVotingSummary.class, userId);
            BroomType previousBroom = BroomType.NONE;
            if (userSummary == null) {
                userSummary = new CompetitionVotingSummary();
            } else {
                previousBroom = userSummary.getBroomType();
            }
            userSummary.setUserId(userId);
            Integer summ = readyToSave.get(userId);
            if (summ.intValue() >= BroomType.FORTH_BROOM.getValue()
                    && previousBroom.getValue() < BroomType.FORTH_BROOM.getValue()) {
                userSummary.setBroomType(BroomType.FORTH_BROOM);
                userSummary.setLeafSummary(summ - BroomType.FORTH_BROOM.getValue());
            } else if (summ.intValue() >= BroomType.THIRD_BROOM.getValue()
                    && previousBroom.getValue() < BroomType.THIRD_BROOM.getValue()) {
                userSummary.setBroomType(BroomType.THIRD_BROOM);
                userSummary.setLeafSummary(summ - BroomType.THIRD_BROOM.getValue());
            } else if (summ.intValue() >= BroomType.SECOND_BROOM.getValue()
                    && previousBroom.getValue() < BroomType.SECOND_BROOM.getValue()) {
                userSummary.setBroomType(BroomType.SECOND_BROOM);
                userSummary.setLeafSummary(summ - BroomType.SECOND_BROOM.getValue());
            } else if (summ.intValue() >= BroomType.FIRST_BROOM.getValue()
                    && previousBroom.getValue() < BroomType.FIRST_BROOM.getValue()) {
                userSummary.setBroomType(BroomType.FIRST_BROOM);
                userSummary.setLeafSummary(summ - BroomType.FIRST_BROOM.getValue());
            } else {
                userSummary.setBroomType(previousBroom);
                userSummary.setLeafSummary(summ - previousBroom.getValue());
            }
            em.persist(userSummary);
        }
        //clean cash
        statisticService.cleanCache();
        return "Statistic is updated successfully";
    }





    private Map<Long,Integer> calculateUsersStatistic(HttpServletResponse response) throws Exception {
        Map<Long,HashSet<Long>> competitionItems = new LinkedHashMap<>();
        Map<Long,HashSet<Long>> userCompItemsMap  = new HashMap<>();
        Map<Long,Integer> compItemsSummaryMap  = new HashMap<>();
        Map<Long,Integer> userSummaryMap  = new HashMap<>();

        TypedQuery<CompetitionItemEntity> activeCompetitionQuery =
                em.createQuery("select i from CompetitionItemEntity i inner join i.competition c where c.competitionEnd < now() order by c.created",
                        CompetitionItemEntity.class);
        List<CompetitionItemEntity> allItems = activeCompetitionQuery.getResultList();
        allItems.forEach(item -> {
            HashSet<Long> users= new  HashSet<>();
            users.add(item.getUserId());
            for (CompetitionItemUsers itemUser : item.getCompetitionItemUsers()) {
                users.add(itemUser.getUserId());
            }
            userCompItemsMap.put(item.getCompetitionItemId(), users);

            if(!competitionItems.containsKey(item.getCompetitionId())) {
                HashSet<Long> uniqueItems = new HashSet<>();
                uniqueItems.add(item.getCompetitionItemId());
                competitionItems.put(item.getCompetitionId(), uniqueItems);
            } else {
                competitionItems.get(item.getCompetitionId()).add(item.getCompetitionItemId());
            }
        });

        Iterator<Long> keys = competitionItems.keySet().iterator();
        while(keys.hasNext()) {
            Long compId = keys.next();
            VoteStatistic statistic = this.getVoteStatistic(compId, response);
            HashSet<Long>  controlItemHash = competitionItems.get(compId);
            List<Long> items = statistic.getAllVoteItemIdList();
            for (Long itemId : items) {
                if (!controlItemHash.contains(itemId)) {
                    throw new Exception("something wrong in statistic calculation for competitionId: " + compId
                            + " itemId " + itemId + "is not found."
                            + " ControlItemHash.size|items.size() - "
                            + controlItemHash.size() + "|" + items.size());
                }
            }
            List<VoterRecord> voterRecords = statistic.getVoters();
            for (VoterRecord voterRecord : voterRecords) {
                HashMap<Long, Integer>  placeMap = voterRecord.getVoterPlaceMap();
                boolean allPlacesSelected = placeMap.size() == items.size();
                for (Long itemId : items) {
                    Integer leaves = 2;
                    if (!allPlacesSelected && placeMap.size() > 0) {
                        leaves = placeMap.containsKey(itemId) ? 5 - placeMap.get(itemId) : 1;
                    } else if (placeMap.size() == 0) {
                        leaves = 2 * items.size();
                    }
                    compItemsSummaryMap.put(itemId,
                            compItemsSummaryMap.containsKey(itemId) ? compItemsSummaryMap.get(itemId) + leaves : leaves);
                }

            }
        }

        Iterator<Long> compItemIds = compItemsSummaryMap.keySet().iterator();
        while (compItemIds.hasNext()) {
            Long compItemId = compItemIds.next();
            if (userCompItemsMap.containsKey(compItemId)) {
                Iterator<Long> userIds = userCompItemsMap.get(compItemId).iterator();
                while (userIds.hasNext()) {
                    Long userId = userIds.next();
                    userSummaryMap.put(userId,
                            userSummaryMap.containsKey(userId)
                                    ? userSummaryMap.get(userId) + compItemsSummaryMap.get(compItemId)
                                    : compItemsSummaryMap.get(compItemId));
                }
            }
        }

        return userSummaryMap;
    }


    @RequestMapping(value = "/api/votestatistic", method = RequestMethod.GET)
    public VoteStatistic getVoteStatistic(@RequestParam("cid") Long compId, HttpServletResponse response) {
        VoteStatistic result = new VoteStatistic();
        TypedQuery<Long> voteItemQuery =
                em.createQuery("select c.id from CompetitionItemEntity c where c.competitionId = :compId and c.active = true order by c.created",
                        Long.class);
        try {
            //all items ids
            result.setAllVoteItemIdList(voteItemQuery.setParameter("compId", compId).getResultList());
            List<CompetitionVotingEntity> votingList= this.getAllVotingsForCompetition(compId, response);
            ListIterator<CompetitionVotingEntity> votingIterator = votingList.listIterator();
            Long currentUserId = null;
            VoterRecord voterRecord = null;
            int countWinners = 0;
            int currentPlace = 3;
            while (votingIterator.hasNext()) {
                CompetitionVotingEntity voting = votingIterator.next();
                if (currentUserId == null || !currentUserId.equals(voting.getUserId())) {
                    currentUserId = voting.getUserId();
                    if (voterRecord != null) {
                        currentPlace = 3;
                    }
                    voterRecord = new VoterRecord();
                    result.getVoters().add(voterRecord);
                    voterRecord.setVoterId(currentUserId);
                }
                //raw voting result
                voterRecord.getVoterRawMap().put(voting.getCompetitionItemId(), voting.getVotingOrder());
                for (int i = currentPlace; i > 0 && countWinners == 0; i--) {
                    int count = voting.getVotingOrder().intValue()/i;
                    if ( count > 0) {
                        countWinners = count;
                        break;
                    }
                    currentPlace--;
                }

                if (countWinners > 0) {
                    voterRecord.getVoterPlaceMap().put(voting.getCompetitionItemId(), currentPlace);
                    countWinners--;
                    if (countWinners == 0) {
                        currentPlace--;
                    }
                }
            }
            return result;

        } catch (Exception ex) {
            LOG.error("Something wrong in getting voting statistic", ex);
            ServiceUtil.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Something wrong in getting voting statistic", response);
            return result;
        }


    }

    private List<CompetitionVotingEntity>  getAllVotingsForCompetition(Long compId, HttpServletResponse response) {
        TypedQuery<CompetitionVotingEntity> voteQuery =
                em.createQuery("select c from CompetitionVotingEntity c where c.competitionId = :compId order by c.userId, c.votingOrder DESC, c.created",
                        CompetitionVotingEntity.class);
        try {
            return voteQuery.setParameter("compId", compId).getResultList();
        } catch (Exception ex) {
            LOG.error("Something wrong in getting voting list", ex);
            ServiceUtil.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Something wrong in getting voting list", response);
            return new ArrayList<>();
        }

    }


}
