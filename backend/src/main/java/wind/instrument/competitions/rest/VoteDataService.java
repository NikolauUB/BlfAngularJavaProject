package wind.instrument.competitions.rest;

import wind.instrument.competitions.data.CompetitionEntity;
import wind.instrument.competitions.data.CompetitionItemEntity;
import wind.instrument.competitions.data.CompetitionType;
import wind.instrument.competitions.rest.model.CompetitionData;
import wind.instrument.competitions.rest.model.CompetitionInfo;
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

    @RequestMapping("/api/votedata")
    public CompetitionInfo getActiveVoteData(@RequestParam("type") Integer type) {
        CompetitionInfo result = new CompetitionInfo();
        if(!CompetitionType.hasType(type)) {
            result.setCode(400);
            result.setErrorMsg("Type not found in request");
            return result;
        }

        CompetitionType competitionType =  CompetitionType.valueOf(type);
        TypedQuery<CompetitionEntity> competitionQuery =
                em.createQuery("select c from CompetitionEntity c where c.active = true and c.competitionType = :type",
                        CompetitionEntity.class);
        CompetitionEntity competition = null;
        try {
            competition =
                    competitionQuery.setParameter("type", competitionType.getValue()).getSingleResult();
        } catch (NoResultException ex) {
            LOG.debug("Error getting competition: ",ex);
        }
        if (competition == null) {
            result.setCode(404);
            result.setErrorMsg("Active competition is not found for selected type");
            return result;
        }
        result.setCompetitionData(new CompetitionData(competition.getCompetitionId(),
                competition.getCompetitionName(),
                competition.getCompetitionType().getValue(),
                competition.getCompetitionDesc(),
                competition.getCompetitionSampleVideo(),
                competition.getCompetitionStart(),
                competition.getCompetitionEnd()
                ));
        Collection<CompetitionItemEntity> competitionItemList = competition.getCompetitionItems();
        ArrayList<VoteData> voteDataList = new ArrayList<VoteData>();
        competitionItemList.forEach(item->{
            VoteData voteData = new VoteData(item.getCompetitionItemId(),
                    item.getOwner().getUsername(),
                    item.getCnItemDescription(),
                    item.getCnItemAuthor(),
                    item.getCnItemComposition(),
                    item.getCnItemVideo(),
                    item.getCnItemAudio());
            voteDataList.add(voteData);
        });
        result.setVoteData(voteDataList);
        return result;
    }

    @RequestMapping(value = "/api/vote", method = RequestMethod.POST)
    public void vote(@RequestBody List<VoteData> votingResult) {
        System.out.println("Result: " + votingResult);
    }

    private void  sendResponseError(int code, String text,  HttpServletResponse response) {
        try {
            response.sendError(code, text);
        } catch (Exception ex) {
            //do nothing
        }
    }


}
