package wind.instrument.competitions.rest;

import wind.instrument.competitions.configuration.SessionParameters;
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
import javax.servlet.http.HttpServletRequest;
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

    //for testing only
    @RequestMapping("/migrate")
    public String migrate(HttpServletRequest req, HttpServletResponse res) {
        if(!AuthService.ADMIN_USERNAME.equals("" + httpSession.getAttribute(SessionParameters.USERNAME.name()))) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "Access Error!";
        }
        Calendar cal = Calendar.getInstance();
        cal.set(2017, 10, 16);
        CompetitionEntity competitionEntity = new CompetitionEntity();
        competitionEntity.setCompetitionType(CompetitionType.PRESCRIBED_BAROQUE);
        competitionEntity.setCompetitionName("Обязательная классическая программа");
        competitionEntity.setCompetitionDesc(this.getClassicDescription());
        competitionEntity.setCompetitionSampleVideo("https://www.youtube.com/embed/rxunt-uyDPc");
        competitionEntity.setCompetitionStart(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        competitionEntity.setCompetitionEnd(cal.getTime());
        competitionEntity.setActive(true);
        Query query = em.createQuery("update CompetitionEntity ce set ce.active = 'false' where ce.competitionType = 0");
        query.executeUpdate();
        em.persist(competitionEntity);

        CompetitionEntity competitionEntityJazz = new CompetitionEntity();
        cal.set(2017, 10, 16);
        competitionEntityJazz.setCompetitionType(CompetitionType.PRESCRIBED_JAZZ);
        competitionEntityJazz.setCompetitionName("Обязательная джазовая программа");
        competitionEntityJazz.setCompetitionDesc(this.getJazzDescription());
        competitionEntityJazz.setCompetitionSampleVideo("https://www.youtube.com/embed/kHtwF-gpluc");
        competitionEntityJazz.setCompetitionStart(cal.getTime());

        cal.add(Calendar.MONTH, 1);
        competitionEntityJazz.setCompetitionEnd(cal.getTime());
        competitionEntityJazz.setActive(true);
        Query queryJazz = em.createQuery("update CompetitionEntity ce set ce.active = 'false' where ce.competitionType = 1");
        queryJazz.executeUpdate();
        em.persist(competitionEntityJazz);

        CompetitionEntity competitionEntityFree = new CompetitionEntity();
        cal.set(2017, 10, 30);
        competitionEntityFree.setCompetitionType(CompetitionType.FREE);
        competitionEntityFree.setCompetitionName("Свободная программа");
        competitionEntityFree.setCompetitionDesc("Любое произведение по Вашему выбору.");
        competitionEntityFree.setCompetitionStart(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        competitionEntityFree.setCompetitionEnd(cal.getTime());
        competitionEntityFree.setActive(true);
        Query queryFree = em.createQuery("update CompetitionEntity ce set ce.active = 'false' where ce.competitionType = 2");
        queryFree.executeUpdate();
        em.persist(competitionEntityFree);


/*
        Collection<CompetitionItemEntity> items = new ArrayList<CompetitionItemEntity>();
        CompetitionItemEntity competitionTypeEntity = new CompetitionItemEntity();
        competitionTypeEntity.setCompetitionId(competitionEntity.getCompetitionId());
        competitionTypeEntity.setUserId(1l);
        competitionTypeEntity.setCnItemAuthor("Bach");
        competitionTypeEntity.setCnItemAudio("https://cloud.mail.ru/public/73YC/MoMWRRjza");
        competitionTypeEntity.setCnItemVideo("https://youtu.be/Xv9bmbVw7XU");
        competitionTypeEntity.setCnItemInstruments("Soprano recorder");
        competitionTypeEntity.setCnItemComposition("Bandeneria");
        competitionTypeEntity.setCnItemDescription("Bach on soprano recorder");
        em.persist(competitionTypeEntity);
        competitionTypeEntity = new CompetitionItemEntity();
        competitionTypeEntity.setCompetitionId(competitionEntity.getCompetitionId());
        competitionTypeEntity.setUserId(2l);
        competitionTypeEntity.setCnItemAuthor("Bach");
        competitionTypeEntity.setCnItemAudio("https://cloud.mail.ru/public/73YC/MoMWRRjza");
        competitionTypeEntity.setCnItemVideo("https://youtu.be/Xv9bmbVw7XU");
        competitionTypeEntity.setCnItemInstruments("Tenor recorder");
        competitionTypeEntity.setCnItemComposition("Bandeneria");
        competitionTypeEntity.setCnItemDescription("Bach on tenor recorder");
        em.persist(competitionTypeEntity);
        competitionTypeEntity = new CompetitionItemEntity();
        competitionTypeEntity.setCompetitionId(competitionEntity.getCompetitionId());
        competitionTypeEntity.setUserId(3l);
        competitionTypeEntity.setCnItemAuthor("Bach");
        competitionTypeEntity.setCnItemAudio("https://cloud.mail.ru/public/73YC/MoMWRRjza");
        competitionTypeEntity.setCnItemVideo("https://youtu.be/Xv9bmbVw7XU");
        competitionTypeEntity.setCnItemInstruments("Alto recorder");
        competitionTypeEntity.setCnItemComposition("Bandeneria");
        competitionTypeEntity.setCnItemDescription("Bach on alto recorder");
        em.persist(competitionTypeEntity);
        */
        return "done";
    }

    private String getClassicDescription() {
        return "<div>" +
        "Jean Daniel Braun,  \"Pièces sans Basse pour la Flute Traversiere\" " +
        "Largo и Double  из сюйты соло для флейты-траверс<br>" +
        "<a href=\"https://www.youtube.com/watch?v=rxunt-uyDPc\" target=\"_blank\">https://www.youtube.com/watch?v=rxunt-uyDPc</a><br>"+
        "<div>" +
        "<iframe width=\"420\" height=\"315\" src=\"https://www.youtube.com/embed/rxunt-uyDPc\" " +
        "allowfullscreen=\"true\" webkitallowfullscreen=\"true\" mozallowfullscreen=\"true\">" +
        "</iframe>" +
        "</div>" +
        "Ноты в оригинальной тональности подходят для сопрано, тенора и для гобоя - смотрите на 11 странице Largo и Double (в записи с гобоисткой они звучат в первые 2 минуты): [url]https://cloud.mail.ru/public/AMNE/DQFvcYmD8[/url]<br>" +
        "Сделал ноты только с нужными частями: " +
        " - Сопрано, тенор, грандбас, гобой, флейта траверс и оркестровая: <a href=\"https://cloud.mail.ru/public/FBm7/JWKFshctx\">ми-минор(тональность оригинала)</a>, <a href=\"https://cloud.mail.ru/public/HU6f/jT4c1pfeX\">ре-минор</a><br>" +
        " - Сопранино, альт, бас, кларнет (вроде ля-минор удобная тональность для кларнета): <a href=\"https://cloud.mail.ru/public/B7th/hUB4ATSLz\">ля-минор</a>, <a href=\"https://cloud.mail.ru/public/A4BJ/pD4C8wck1\">соль-минор</a><br>" +
        "</div>";
    }


    private String getJazzDescription() {
        return "<div>"+
                "Scott Joplin \"The Entertainer\"<br>"+
                "Ноты: <a href=\"https://cloud.mail.ru/public/52rG/TDexuQCrF\" target=\"_blank\">https://cloud.mail.ru/public/52rG/TDexuQCrF</a><br>"+
                "Минусовки в разных темпах (под авторством участника форума blf.ru Husim, о работах которого очень вовремя вспомнила ЛюдМила)<br>"+
                "Teмп 52: <a href=\"https://cloud.mail.ru/public/HqdY/PqLfo8JNV\" target=\"_blank\">https://cloud.mail.ru/public/HqdY/PqLfo8JNV</a><br>"+
                "Темп 64: <a href=\"https://cloud.mail.ru/public/JEUp/nuxv6jj9x\" target=\"_blank\">https://cloud.mail.ru/public/JEUp/nuxv6jj9x</a><br>"+
                "Темп 76: <a href=\"https://cloud.mail.ru/public/6V9S/eb3FbZiDe\" target=\"_blank\">https://cloud.mail.ru/public/6V9S/eb3FbZiDe</a><br>"+
                "Темп 88: <a href=\"https://cloud.mail.ru/public/CKus/shbP5NdNs\" target=\"_blank\">https://cloud.mail.ru/public/CKus/shbP5NdNs</a><br>"+
                "Темп 100: <a href=\"https://cloud.mail.ru/public/MqHe/m6j5jsPZK\" target=\"_blank\">https://cloud.mail.ru/public/MqHe/m6j5jsPZK</a><br>"+
                "<br>"+
                "Есть также плюс под эту минусовку: <a href=\"https://cloud.mail.ru/public/2xYE/1emrzc1eX\" target=\"_blank\">https://cloud.mail.ru/public/2xYE/1emrzc1eX</a><br>"+
                "И вот гитарный вариант:<br>"+
                "<a href=\"https://www.youtube.com/watch?v=kHtwF-gpluc\" target=\"_blank\">https://www.youtube.com/watch?v=kHtwF-gpluc</a><br>" +
                "<div>" +
                "<iframe width=\"420\" height=\"315\" src=\"https://www.youtube.com/embed/kHtwF-gpluc\" " +
                "allowfullscreen=\"true\" webkitallowfullscreen=\"true\" mozallowfullscreen=\"true\">" +
                "</iframe>" +
                "</div>" +
                "</div>";
    }

}
