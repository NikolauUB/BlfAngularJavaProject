package wind.instrument.competitions.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wind.instrument.competitions.data.CompetitionEntity;
import wind.instrument.competitions.rest.model.CompetitionData;
import wind.instrument.competitions.rest.model.changes.ChangesKeywords;

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
    public ChangesKeywords getChangedKeywords(@RequestParam("ld") Long previousTime) {
        ChangesKeywords result = new ChangesKeywords();
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

}
