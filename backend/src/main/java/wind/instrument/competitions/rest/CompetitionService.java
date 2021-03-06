package wind.instrument.competitions.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wind.instrument.competitions.data.CompetitionEntity;
import wind.instrument.competitions.data.ThemeType;
import wind.instrument.competitions.data.UserEntity;
import wind.instrument.competitions.rest.model.ActiveCompetitionType;
import wind.instrument.competitions.rest.model.ActiveCompetitions;
import wind.instrument.competitions.rest.model.CompetitionData;
import wind.instrument.competitions.rest.model.CompetitionMember;

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
public class CompetitionService {
    private static Logger LOG = LoggerFactory.getLogger(CompetitionService.class);
    /**
     * Russian messages
     */
    private static ResourceBundle bundle = ResourceBundle.getBundle("Messages");

    @PersistenceContext
    private EntityManager em;

    @RequestMapping(value = "/api/getActiveCompetitions", method = RequestMethod.GET)
    public ActiveCompetitions getActiveCompetitions(HttpServletResponse response) {
        ActiveCompetitions result = new ActiveCompetitions();
        TypedQuery<CompetitionEntity> activeCometQuery =
                em.createQuery("select c from CompetitionEntity c where c.active = true",
                        CompetitionEntity.class);
        try {
            List<CompetitionEntity> competList = activeCometQuery.getResultList();
            ArrayList<ActiveCompetitionType> list = new ArrayList<ActiveCompetitionType>();
            competList.forEach((item) -> {
                ActiveCompetitionType type = new ActiveCompetitionType();
                type.setId(item.getCompetitionId());
                type.setType(item.getCompetitionType().getValue());
                type.setStart(item.getCompetitionStart());
                type.setEnd(item.getCompetitionEnd());
                list.add(type);
            });
            result.setTypes(list);
        } catch (NoResultException ex) {
            ServiceUtil.sendResponseError(HttpServletResponse.SC_NOT_FOUND, bundle.getString("ACTIVE_COMPETIONS_IS_NOT_FOUND"), response);
        }
        return result;
    }

    @RequestMapping(value = "/api/getFutureCompetitions", method = RequestMethod.GET)
    public ActiveCompetitions getFutureCompetitions(HttpServletResponse response) {
        ActiveCompetitions result = new ActiveCompetitions();
        TypedQuery<CompetitionEntity> activeCometQuery =
                em.createQuery("select c from CompetitionEntity c where c.future = true",
                        CompetitionEntity.class);
        try {
            List<CompetitionEntity> competList = activeCometQuery.getResultList();
            ArrayList<ActiveCompetitionType> list = new ArrayList<ActiveCompetitionType>();
            competList.forEach((item) -> {
                ActiveCompetitionType type = new ActiveCompetitionType();
                type.setId(item.getCompetitionId());
                type.setType(item.getCompetitionType().getValue());
                type.setStart(item.getCompetitionStart());
                type.setEnd(item.getCompetitionEnd());
                list.add(type);
            });
            result.setTypes(list);
        } catch (NoResultException ex) {
            ServiceUtil.sendResponseError(HttpServletResponse.SC_NOT_FOUND, bundle.getString("ACTIVE_COMPETIONS_IS_NOT_FOUND"), response);
        }
        return result;
    }


    @RequestMapping(value = "/api/getActiveCompetitionData", method = RequestMethod.GET)
    public CompetitionData getActiveCompetitionData(@RequestParam("tp") Integer competitionType,
                                                    HttpServletResponse response) {
        CompetitionData result = null;
        TypedQuery<CompetitionEntity> activeCometQuery =
                em.createQuery("select c from CompetitionEntity c where c.active = true and c.competitionType=:type",
                        CompetitionEntity.class);
        try {
            CompetitionEntity competitionEntity = activeCometQuery
                    .setParameter("type", competitionType)
                    .getSingleResult();

            result = new CompetitionData(
                    competitionEntity.getCompetitionId(),
                    competitionEntity.getCompetitionName(),
                    competitionEntity.getCompetitionType().getValue(),
                    competitionEntity.getCompetitionDesc(),
                    competitionEntity.getCompetitionSampleVideo(),
                    competitionEntity.getCompetitionStart(),
                    competitionEntity.getCompetitionEnd());
            return result;
        } catch (NoResultException ex) {
            ServiceUtil.sendResponseError(HttpServletResponse.SC_NOT_FOUND, bundle.getString("ACTIVE_COMPETIONS_IS_NOT_FOUND"), response);
        }
        return result;
    }

    @RequestMapping(value = "/api/getFutureCompetitionData", method = RequestMethod.GET)
    public CompetitionData getFutureCompetitionData(@RequestParam("tp") Integer competitionType,
                                                    HttpServletResponse response) {
        CompetitionData result = null;
        TypedQuery<CompetitionEntity> activeCometQuery =
                em.createQuery("select c from CompetitionEntity c where c.future = true and c.competitionType=:type",
                        CompetitionEntity.class);
        try {
            CompetitionEntity competitionEntity = activeCometQuery
                    .setParameter("type", competitionType)
                    .getSingleResult();

            result = new CompetitionData(
                    competitionEntity.getCompetitionId(),
                    competitionEntity.getCompetitionName(),
                    competitionEntity.getCompetitionType().getValue(),
                    competitionEntity.getCompetitionDesc(),
                    competitionEntity.getCompetitionSampleVideo(),
                    competitionEntity.getCompetitionStart(),
                    competitionEntity.getCompetitionEnd());
            return result;
        } catch (NoResultException ex) {
            ServiceUtil.sendResponseError(HttpServletResponse.SC_NOT_FOUND, bundle.getString("ACTIVE_COMPETIONS_IS_NOT_FOUND"), response);
        }
        return result;
    }

    /**
     * Checks every time when user sees|removes partake request
     *
     * @param response
     * @return
     */
    @RequestMapping(value = "/api/getCompetitionMembers", method = RequestMethod.GET)
    public ArrayList<CompetitionMember> getCompetitionMembers(HttpServletResponse response) {
        ArrayList<CompetitionMember> result = new ArrayList<CompetitionMember>();
        TypedQuery<CompetitionEntity> activeCometQuery =
                em.createQuery("select c from CompetitionEntity c where c.future = true",
                        CompetitionEntity.class);
        try {
            List<CompetitionEntity> competitionList = activeCometQuery.getResultList();
            competitionList.forEach((item) -> {
                item.getThemesByMembers().forEach((theme) -> {
                    if (theme.getThemeType().equals(ThemeType.COMPETITION_REQUEST) && theme.getMessages().size() > 0) {
                        CompetitionMember competitionMember = new CompetitionMember();
                        UserEntity member = theme.getOwner();
                        competitionMember.setmId(member.getUserId());
                        competitionMember.setmUsername(member.getUsername());
                        competitionMember.setCompType(item.getCompetitionType().getValue());
                        competitionMember.setThreadId(theme.getId());
                        result.add(competitionMember);
                    }
                });
            });
        } catch (NoResultException ex) {
            ServiceUtil.sendResponseError(HttpServletResponse.SC_NOT_FOUND, bundle.getString("ACTIVE_COMPETIONS_IS_NOT_FOUND"), response);
        }
        return result;
    }

}
