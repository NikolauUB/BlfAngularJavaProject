package wind.instrument.competitions.service;



import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import wind.instrument.competitions.data.CompetitionEntity;
import wind.instrument.competitions.data.CompetitionVotingSummary;
import wind.instrument.competitions.rest.model.votestatistic.UserCompetition;
import wind.instrument.competitions.rest.model.votestatistic.UserStatisticHistory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(propagation =  Propagation.SUPPORTS, readOnly = true)
public class StatisticCacheService {


    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PersistentCacheManager cacheManager;

    public List<UserStatisticHistory> getAllStatistic() {
        Cache cache = cacheManager.getCache("allStatistic", String.class, ArrayList.class);
        if (cache != null) {
            return (List<UserStatisticHistory>) cache.get("all");
        }
        return null;
    }

    public List<UserStatisticHistory> putAllStatistic() {
        Cache cache = cacheManager.getCache("allStatistic", String.class, ArrayList.class);
        List<UserStatisticHistory> result = new ArrayList<>();

        TypedQuery<Object[]> userSummary =
                em.createQuery("select c, u.username from CompetitionVotingSummary c, UserEntity u WHERE c.userId = u.userId order by c.leafSummary desc, u.username", Object[].class);
        TypedQuery<CompetitionEntity> userCompetions =
                em.createQuery("select c from CompetitionEntity c where " +
                        "exists (select i from CompetitionItemEntity i " +
                        "LEFT JOIN i.competitionItemUsers u " +
                        "where i.competitionId = c.competitionId " +
                        "and (i.userId = :userId or u.userId = :userId)) order by c.created DESC" , CompetitionEntity.class);
        List<Object[]> summaries = userSummary.getResultList();
        for (Object[] summaryItemArr : summaries) {
            CompetitionVotingSummary summaryItem = (CompetitionVotingSummary) summaryItemArr[0];
            String username =(String) summaryItemArr[1];
            UserStatisticHistory userStatistic = new UserStatisticHistory();
            userStatistic.setUserId(summaryItem.getUserId());
            userStatistic.setUsername(username);
            userStatistic.setBroomType(summaryItem.getBroomType().getValue());
            userStatistic.setLeaves(summaryItem.getLeafSummary());
            List<CompetitionEntity>  compList =
                    userCompetions.setParameter("userId", summaryItem.getUserId()).getResultList();
            List<UserCompetition> userComps = new ArrayList<UserCompetition>();
            for(CompetitionEntity competition : compList) {
                userComps.add(new UserCompetition(competition.getCompetitionId(),
                        competition.getCompetitionName(),
                        competition.getCompetitionStart()));
            }
            userStatistic.setCompIds(userComps);
            result.add(userStatistic);
        }
        cache.put("all", result);
        return (List<UserStatisticHistory>) cache.get("all");
    }

    public void cleanCache() {
        Cache cache = cacheManager.getCache("allStatistic", String.class, ArrayList.class);
        cache.remove("all");
    }
}
