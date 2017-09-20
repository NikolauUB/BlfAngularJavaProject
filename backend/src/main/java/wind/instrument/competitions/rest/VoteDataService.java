package wind.instrument.competitions.rest;

import wind.instrument.competitions.configuration.SessionParameters;
import wind.instrument.competitions.data.*;
import wind.instrument.competitions.rest.model.CompetitionData;
import wind.instrument.competitions.rest.model.CompetitionInfo;
import wind.instrument.competitions.rest.model.CompetitionItem;
import wind.instrument.competitions.rest.model.VoteData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@RestController
@Transactional
public class VoteDataService {

    private static Logger LOG = LoggerFactory.getLogger(VoteDataService.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private HttpSession httpSession;



    private boolean isAdmin(HttpServletResponse response) {
        UserEntity currentUser = em.find(UserEntity.class, httpSession.getAttribute(SessionParameters.USER_ID.name()));
        if (!AuthService.ADMIN_USERNAME.equals(currentUser.getUsername())) {
            try {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not administrator");
            } catch (Exception ex) { }
            return false;
        }
        return true;
    }
    @RequestMapping(value = "/api/loadCompetitionItem", method = RequestMethod.POST)
    public CompetitionItem loadCompetitionItem(@RequestBody CompetitionItem competitionItem, HttpServletResponse response) {
        if (this.isAdmin(response)) {
            TypedQuery<CompetitionItemEntity> competitionItemQuery =
                    em.createQuery("select c from CompetitionItemEntity c where c.userId = :userId and c.competitionId = :compId",
                            CompetitionItemEntity.class);
            CompetitionItemEntity competitionItemEntity = competitionItemQuery
                    .setParameter("userId", competitionItem.getUserId())
                    .setParameter("compId", competitionItem.getCompId())
                    .getSingleResult();

            competitionItem.setId(competitionItemEntity.getCompetitionItemId());
            competitionItem.setAudio(competitionItemEntity.getCnItemAudio());
            competitionItem.setVideo(competitionItemEntity.getCnItemVideo());
            competitionItem.setAuthor(competitionItemEntity.getCnItemAuthor());
            competitionItem.setComposition(competitionItemEntity.getCnItemComposition());
            competitionItem.setDesc(competitionItemEntity.getCnItemDescription());
            competitionItem.setInstrmnts(competitionItemEntity.getCnItemInstruments());
            competitionItem.setUserId(competitionItemEntity.getUserId());
            competitionItem.setCompId(competitionItemEntity.getCompetitionId());
        }
        return competitionItem;
    }



    @RequestMapping(value = "/api/saveCompetitionItem", method = RequestMethod.POST)
    public CompetitionItem saveCompetitionItem(@RequestBody CompetitionItem competitionItem, HttpServletResponse response) {
        if (this.isAdmin(response)) {
            CompetitionItemEntity competitionItemEntity = null;
            if (competitionItem.getId() != null) {
                competitionItemEntity = em.find(CompetitionItemEntity.class, competitionItem.getId());
            } else {
                competitionItemEntity = new CompetitionItemEntity();
            }
            competitionItemEntity.setCnItemAudio(competitionItem.getAudio());
            competitionItemEntity.setCnItemVideo(competitionItem.getVideo());
            competitionItemEntity.setCnItemAuthor(competitionItem.getAuthor());
            competitionItemEntity.setCnItemComposition(competitionItem.getComposition());
            competitionItemEntity.setCnItemDescription(competitionItem.getDesc());
            competitionItemEntity.setCnItemInstruments(competitionItem.getInstrmnts());
            competitionItemEntity.setUserId(competitionItem.getUserId());
            competitionItemEntity.setCompetitionId(competitionItem.getCompId());
            em.persist(competitionItemEntity);
            competitionItem.setId(competitionItemEntity.getCompetitionItemId());
        }
        return competitionItem;
    }


    @RequestMapping("/api/votedata")
    public CompetitionInfo getActiveVoteData(@RequestParam("type") Integer type, HttpServletResponse response) {
        CompetitionInfo result = new CompetitionInfo();

        UserEntity currentUser = null;
        if(httpSession.getAttribute(SessionParameters.USER_ID.name()) != null) {
            currentUser = em.find(UserEntity.class, httpSession.getAttribute(SessionParameters.USER_ID.name()));
        }

        if(!CompetitionType.hasType(type)) {
            this.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, "Type not found in request", response);
            return result;
        }

        CompetitionType competitionType =  CompetitionType.valueOf(type);
        //todo select in period of activity
        TypedQuery<CompetitionEntity> competitionQuery =
                em.createQuery("select c from CompetitionEntity c where c.active = true and c.competitionType = :type",
                        CompetitionEntity.class);
        CompetitionEntity competition = null;
        try {
            competition =
                    competitionQuery.setParameter("type", competitionType.getValue()).getSingleResult();
        } catch (NoResultException ex) {
            LOG.debug("Error getting competition: ",ex);
            this.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error getting competition", response);
        }
        if (competition == null) {
            this.sendResponseError(HttpServletResponse.SC_NOT_FOUND, "Active competition is not found for selected type", response);
            return result;
        }
        result.setCompetitionData(new CompetitionData(competition.getCompetitionId(),
                "",
                competition.getCompetitionType().getValue(),
                "",
                "",
                competition.getCompetitionStart(),
                competition.getCompetitionEnd()
                ));
        Collection<CompetitionItemEntity> competitionItemList = competition.getCompetitionItems();

        final Map<Long, Integer> votingOrderMap = new HashMap<Long, Integer>();
        if (currentUser != null) {
            List<CompetitionVotingEntity> votingList = this.getUserVotings(competition.getCompetitionId(), currentUser.getUserId(), response);
            votingList.forEach(votingItem -> {
                votingOrderMap.put(votingItem.getCompetitionItemId(), votingItem.getVotingOrder());
            });
        }
        ArrayList<VoteData> voteDataList = new ArrayList<VoteData>();
        competitionItemList.forEach(item->{
            ArrayList<Long> userIds = new ArrayList<Long>();
            ArrayList<String> usernames = new ArrayList<String>();
            userIds.add(item.getUserId());
            usernames.add(item.getOwner().getUsername());
            item.getCompetitionItemUsers().forEach(itemUsers -> {
                userIds.add(itemUsers.getUserId());
                usernames.add(itemUsers.getUser().getUsername());
            });

            VoteData voteData = new VoteData(item.getCompetitionItemId(),
                    userIds,
                    usernames,
                    item.getCnItemDescription(),
                    item.getCnItemInstruments(),
                    item.getCnItemAuthor(),
                    item.getCnItemComposition(),
                    item.getCnItemVideo(),
                    item.getCnItemAudio());
            if (votingOrderMap.containsKey(item.getCompetitionItemId())) {
                voteData.setOrder(votingOrderMap.get(item.getCompetitionItemId()));
            }
            voteDataList.add(voteData);
        });
        if (votingOrderMap.size() > 0) {
            result.setVoted(true);
        }
        result.setVoteData(voteDataList);
        return result;
    }

    private List<CompetitionVotingEntity> getUserVotings(Long compId, Long userId, HttpServletResponse response) {
        TypedQuery<CompetitionVotingEntity> voteQuery =
                em.createQuery("select c from CompetitionVotingEntity c where c.competitionId = :compId and c.userId = :uid",
                        CompetitionVotingEntity.class);
        try {
            return voteQuery.setParameter("compId", compId)
                    .setParameter("uid", userId)
                    .getResultList();
        } catch (Exception ex) {
            LOG.error("Something wrong in vote when check previous votes", ex);
            this.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Something wrong in vote when check previous votes", response);
            return new ArrayList<CompetitionVotingEntity>();
        }
    }

    @RequestMapping(value = "/api/vote", method = RequestMethod.POST)
    public void vote(@RequestBody List<VoteData> votingResult, HttpServletResponse response) {
        UserEntity currentUser = em.find(UserEntity.class, httpSession.getAttribute(SessionParameters.USER_ID.name()));
        boolean isChecked = false;
        Long firstCompId = null;
        for(VoteData vote : votingResult){
            CompetitionItemEntity cItem = em.find(CompetitionItemEntity.class, vote.getId());
            if (cItem == null) {
                this.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, "Competition Item is not found", response);
                return;
            } else if (firstCompId != null && !cItem.getCompetitionId().equals(firstCompId)) {
                this.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, "Competition Items should belong to one competition", response);
                return;
            }

            if (!isChecked) {
                if (this.getUserVotings(cItem.getCompetitionId(), currentUser.getUserId(), response).size() > 0) {
                    this.sendResponseError(HttpServletResponse.SC_CONFLICT, "You already voted!", response);
                    return;
                }
                isChecked = true;
                firstCompId = cItem.getCompetitionId();
            }
            CompetitionVotingEntity votingEntity = new CompetitionVotingEntity();
            votingEntity.setCompetitionId(cItem.getCompetitionId());
            votingEntity.setCompetitionItemId(vote.getId());
            votingEntity.setUserId(currentUser.getUserId());
            votingEntity.setVotingOrder(vote.getOrder());
            em.persist(votingEntity);
        }
    }

    @RequestMapping(value = "/api/deleteVote", method = RequestMethod.DELETE)
    public void deleteVote(@RequestParam("cid") Long compId, HttpServletResponse response) {
        UserEntity currentUser = em.find(UserEntity.class, httpSession.getAttribute(SessionParameters.USER_ID.name()));
        if (compId != null ) {
            Query deleteVoteQuery =
                    em.createQuery("delete from CompetitionVotingEntity c where c.competitionId = :compId and c.userId = :uid");
            deleteVoteQuery.setParameter("compId", compId).setParameter("uid", currentUser.getUserId()).executeUpdate();
        }
    }



    private void  sendResponseError(int code, String text,  HttpServletResponse response) {
        try {
            response.sendError(code, text);
        } catch (Exception ex) {
            LOG.error("Something wrong sending error responses", ex);
        }
    }


}
