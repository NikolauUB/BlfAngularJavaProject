package wind.instrument.competitions.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import wind.instrument.competitions.data.*;
import wind.instrument.competitions.middle.AdminInfo;
import wind.instrument.competitions.rest.model.CompetitionData;
import wind.instrument.competitions.rest.model.CompetitionInfo;
import wind.instrument.competitions.rest.model.CompetitionItem;
import wind.instrument.competitions.rest.model.VoteData;
import wind.instrument.competitions.rest.model.votestatistic.VoteStatistic;
import wind.instrument.competitions.rest.model.votestatistic.VoterRecord;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Transactional
public class VoteDataService {

    private static Logger LOG = LoggerFactory.getLogger(VoteDataService.class);
    /**
     * Russian messages
     */
    private static ResourceBundle bundle = ResourceBundle.getBundle("Messages");

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private HttpSession httpSession;


    @RequestMapping(value = "/api/loadCompetitionItem", method = RequestMethod.POST)
    public CompetitionItem loadCompetitionItem(@RequestBody CompetitionItem competitionItem, HttpServletResponse response) {
        if (this.isAdmin(response)) {
            TypedQuery<CompetitionItemEntity> competitionItemQuery =
                    em.createQuery("select c from CompetitionItemEntity c where c.userId = :userId and c.competitionId = :compId",
                            CompetitionItemEntity.class);
            CompetitionItemEntity competitionItemEntity = null;
            try {
                competitionItemEntity = competitionItemQuery
                        .setParameter("userId", competitionItem.getUserId())
                        .setParameter("compId", competitionItem.getCompId())
                        .getSingleResult();
            } catch (NoResultException ex) {
                ServiceUtil.sendResponseError(HttpServletResponse.SC_NOT_FOUND, "Vote Item doesn't exist", response);
                return competitionItem;
            }

            competitionItem.setId(competitionItemEntity.getCompetitionItemId());
            competitionItem.setAudio(competitionItemEntity.getCnItemAudio());
            competitionItem.setEmbedAudio(competitionItemEntity.getCnItemEmbedAudio());
            competitionItem.setVideo(competitionItemEntity.getCnItemVideo());
            competitionItem.setEmbedVideo(competitionItemEntity.getCnItemEmbedVideo());
            competitionItem.setAuthor(competitionItemEntity.getCnItemAuthor());
            competitionItem.setComposition(competitionItemEntity.getCnItemComposition());
            competitionItem.setDesc(competitionItemEntity.getCnItemDescription());
            competitionItem.setInstrmnts(competitionItemEntity.getCnItemInstruments());
            competitionItem.setUserId(competitionItemEntity.getUserId());
            competitionItem.setCompId(competitionItemEntity.getCompetitionId());
            //additional members
            Collection<CompetitionItemUsers> itemUsersList = competitionItemEntity.getCompetitionItemUsers();
            StringBuilder ids = new StringBuilder();
            itemUsersList.forEach((item) -> {
                if (ids.length() > 0) {
                    ids.append(",");
                }
                ids.append(item.getUserId().toString());
            });
            if (ids.length() > 0) {
                competitionItem.setAdUsers(ids.toString());
            }
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
            competitionItemEntity.setCnItemEmbedAudio(competitionItem.getEmbedAudio());
            competitionItemEntity.setCnItemVideo(competitionItem.getVideo());
            competitionItemEntity.setCnItemEmbedVideo(competitionItem.getEmbedVideo());
            competitionItemEntity.setCnItemAuthor(competitionItem.getAuthor());
            competitionItemEntity.setCnItemComposition(competitionItem.getComposition());
            competitionItemEntity.setCnItemDescription(competitionItem.getDesc());
            competitionItemEntity.setCnItemInstruments(competitionItem.getInstrmnts());
            competitionItemEntity.setUserId(competitionItem.getUserId());
            competitionItemEntity.setCompetitionId(competitionItem.getCompId());
            em.persist(competitionItemEntity);
            competitionItem.setId(competitionItemEntity.getCompetitionItemId());
            //additional users
            //delete old
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<CompetitionItemUsers> criteria = criteriaBuilder.createQuery(CompetitionItemUsers.class);
            Root<CompetitionItemUsers> from = criteria.from(CompetitionItemUsers.class);
            criteria.select(from);

            ParameterExpression<Long> competitionItemIdParam = criteriaBuilder.parameter(Long.class);
            criteria.where(criteriaBuilder.equal(from.get("competitionItemId"), competitionItemIdParam));
            TypedQuery<CompetitionItemUsers> query = em.createQuery(criteria);
            query.setParameter(competitionItemIdParam, competitionItemEntity.getCompetitionItemId());
            List<CompetitionItemUsers> listItemUsers = query.getResultList();
            for (CompetitionItemUsers itemUser : listItemUsers) {
                em.remove(itemUser);
            }
            if (competitionItem.getAdUsers() != null && competitionItem.getAdUsers().length() > 0) {
                String[] ids = competitionItem.getAdUsers().split(",");
                for (String id : ids) {
                    CompetitionItemUsers itemU = new CompetitionItemUsers();
                    itemU.setCompetitionItemId(competitionItemEntity.getCompetitionItemId());
                    itemU.setUserId(Long.parseLong(id));
                    em.persist(itemU);
                }
            }


        }
        return competitionItem;
    }

    @RequestMapping(value = "/api/removeCompetitionItem", method = RequestMethod.DELETE)
    public void removeCompetitionItem(@RequestParam("iid") Long itemId, HttpServletResponse response) {
        if (this.isAdmin(response)) {
            CompetitionItemEntity competitionItemEntity = em.find(CompetitionItemEntity.class, itemId);
            if (competitionItemEntity == null) {
                ServiceUtil.sendResponseError(HttpServletResponse.SC_NOT_FOUND, "Now item with id " + itemId + " exists!", response);
                return;
            }
            try {
                em.remove(competitionItemEntity);
            } catch (Exception ex) {
                LOG.error("Error deleting competion item: ", ex);
                ServiceUtil.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, bundle.getString("SERVER_ERROR"), response);
            }
        }
    }


    @RequestMapping("/api/votedata")
    public CompetitionInfo getActiveVoteData(@RequestParam("type") Integer type, @RequestParam(name="cid", required = false) Long compId, HttpServletResponse response) {
        CompetitionInfo result = new CompetitionInfo();
        UserEntity currentUser = ServiceUtil.findCurrentUser(em, httpSession);

        if (type.intValue() != -1 && !CompetitionType.hasType(type) ) {
            ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, "Type not found in request", response);
            return result;
        }

        CompetitionType competitionType = CompetitionType.valueOf(type);
        TypedQuery<CompetitionEntity> competitionQuery = null;
        if (type.intValue() > -1) {
            competitionQuery =
                    em.createQuery("select c from CompetitionEntity c where c.active = true and c.competitionType = :type",
                            CompetitionEntity.class);
        } else {
            competitionQuery =
                    em.createQuery("select c from CompetitionEntity c where c.competitionId = :compId",
                            CompetitionEntity.class);

        }
        CompetitionEntity competition = null;
        try {
            if (type.intValue() > -1) {
                competition =
                        competitionQuery.setParameter("type", competitionType.getValue()).getSingleResult();
            } else {
                competition =
                        competitionQuery.setParameter("compId", compId).getSingleResult();
            }
        } catch (NoResultException ex) {
            LOG.error("Error getting competition: ", ex);
            ServiceUtil.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error getting competition", response);
        }
        if (competition == null) {
            ServiceUtil.sendResponseError(HttpServletResponse.SC_NOT_FOUND, "Active competition is not found for selected type or id", response);
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
        if (!ServiceUtil.isAdmin(currentUser)) {
            competitionItemList =
                    competitionItemList.stream().filter(item -> (
                            (item.getActive() != null && item.getActive())
                                    || (currentUser != null && item.getUserId().equals(currentUser.getUserId()))
                        )).collect(Collectors.toList());
        }
        final Map<Long, Integer> votingOrderMap = new HashMap<Long, Integer>();
        if (currentUser != null) {
            List<CompetitionVotingEntity> votingList = this.getUserVotings(competition.getCompetitionId(), currentUser.getUserId(), response);
            votingList.forEach(votingItem -> {
                votingOrderMap.put(votingItem.getCompetitionItemId(), votingItem.getVotingOrder());
            });
        }
        ArrayList<VoteData> voteDataList = new ArrayList<VoteData>();
        competitionItemList.forEach(item -> {
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
                    item.getCnItemEmbedVideo(),
                    item.getCnItemAudio(),
                    item.getCnItemEmbedAudio());
            if (votingOrderMap.containsKey(item.getCompetitionItemId())) {
                voteData.setOrder(votingOrderMap.get(item.getCompetitionItemId()));
            }
            voteDataList.add(voteData);
        });
        if (votingOrderMap.size() > 0) {
            result.setVoted(true);
            if (currentUser != null) {
                result.setUserId(currentUser.getUserId());
            }
        }
        result.setVoteData(voteDataList);
        return result;
    }

    @RequestMapping(value = "/api/vote", method = RequestMethod.POST)
    public void vote(@RequestBody List<VoteData> votingResult, HttpServletResponse response) {
        UserEntity currentUser = ServiceUtil.findCurrentUser(em, httpSession);
        boolean isChecked = false;
        Long firstCompId = null;
        for (VoteData vote : votingResult) {
            CompetitionItemEntity cItem = em.find(CompetitionItemEntity.class, vote.getId());
            if (cItem == null) {
                ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, "Competition Item is not found", response);
                return;
            } else if (firstCompId != null && !cItem.getCompetitionId().equals(firstCompId)) {
                ServiceUtil.sendResponseError(HttpServletResponse.SC_BAD_REQUEST, "Competition Items should belong to one competition", response);
                return;
            }

            if (!isChecked) {
                if (this.getUserVotings(cItem.getCompetitionId(), currentUser.getUserId(), response).size() > 0) {
                    ServiceUtil.sendResponseError(HttpServletResponse.SC_CONFLICT, "You already voted!", response);
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
        UserEntity currentUser = ServiceUtil.findCurrentUser(em, httpSession);
        if (compId != null) {
            Query deleteVoteQuery =
                    em.createQuery("delete from CompetitionVotingEntity c where c.competitionId = :compId and c.userId = :uid");
            deleteVoteQuery.setParameter("compId", compId).setParameter("uid", currentUser.getUserId()).executeUpdate();
        }
    }

    private boolean isAdmin(HttpServletResponse response) {
        UserEntity currentUser = ServiceUtil.findCurrentUser(em, httpSession);
        if (!AdminInfo.ADMIN_USERNAME.equals(currentUser.getUsername())) {
            try {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not administrator");
            } catch (Exception ex) {
            }
            return false;
        }
        return true;
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
            ServiceUtil.sendResponseError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Something wrong in vote when check previous votes", response);
            return new ArrayList<>();
        }
    }

}
