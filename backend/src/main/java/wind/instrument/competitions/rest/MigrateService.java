package wind.instrument.competitions.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wind.instrument.competitions.configuration.SessionParameters;
import wind.instrument.competitions.data.CompetitionEntity;
import wind.instrument.competitions.data.CompetitionType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Calendar;

@RestController
@Transactional
public class MigrateService {
    private static Logger LOG = LoggerFactory.getLogger(MigrateService.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private HttpSession httpSession;

    //for testing only
    @RequestMapping("/migrate")
    public String migrate(HttpServletRequest req, HttpServletResponse res) {
        if (!AuthService.ADMIN_USERNAME.equals("" + httpSession.getAttribute(SessionParameters.USERNAME.name()))) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "Access Error!";
        }
        Calendar cal = Calendar.getInstance();
        cal.set(2017, 10, 16, 23, 59);
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
        cal.set(2017, 10, 16, 23, 59);
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
        cal.set(2017, 10, 30, 23, 59);
        competitionEntityFree.setCompetitionType(CompetitionType.FREE);
        competitionEntityFree.setCompetitionName("Свободная программа");
        competitionEntityFree.setCompetitionDesc("Любое произведение по Вашему выбору. Желательно только, чтобы длительность звучания записи не превышала 3-х минут");
        competitionEntityFree.setCompetitionSampleVideo("https://www.youtube.com/embed/KPskrs5lePM");
        competitionEntityFree.setCompetitionStart(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        competitionEntityFree.setCompetitionEnd(cal.getTime());
        competitionEntityFree.setActive(true);
        Query queryFree = em.createQuery("update CompetitionEntity ce set ce.active = 'false' where ce.competitionType = 2");
        queryFree.executeUpdate();
        em.persist(competitionEntityFree);
        return "done";
    }

    private String getClassicDescription() {
        return "<p><strong>Композитор</strong>: Жан Дэниел Браун( 1728 -1740, Jean Daniel Braun)</p>" +
                "<p><strong>Произведение</strong>: &nbsp;&quot;Pi&egrave;ces sans Basse pour la Flute Traversiere&quot; Largo и Double из сюйты соло для флейты-траверс</p>" +
                "<p><strong>Пример исполнения</strong>: <a href=\"https://www.youtube.com/watch?v=rxunt-uyDPc\" target=\"_blank\">https://www.youtube.com/watch?v=rxunt-uyDPc</a> (Largo и Double в течении первых 2-х минут)</p>" +
                "<p><strong>Ноты: <a href=\"https://cloud.mail.ru/public/AMNE/DQFvcYmD8\">сюита целком</a>&nbsp;</strong><em>(нужный фрагмент находится на 11-той странице - </em>Largo и Double<em>)</em></p>" +
                "<p>Ноты только с нужными частями:</p>" +
                "<ul>" +
                "<li>Сопрано, тенор, грандбас, гобой, флейта траверс и оркестровая: <a href=\"https://cloud.mail.ru/public/FBm7/JWKFshctx\">ми-минор(тональность оригинала)</a>, <a href=\"https://cloud.mail.ru/public/HU6f/jT4c1pfeX\">ре-минор</a></li>" +
                "<li>Сопранино, альт, бас, кларнет &nbsp;<a href=\"https://cloud.mail.ru/public/B7th/hUB4ATSLz\">ля-минор</a>, <a href=\"https://cloud.mail.ru/public/A4BJ/pD4C8wck1\">соль-минор</a></li>" +
                "</ul>" +
                "<p data-empty=\"true\">Программа состоит из 2 частей медленной и быстрой. Если быстрая часть не получается, Вы можете ограничиться записью только медленной части.</p>" +
                "<p>Удачи!</p>";
    }


    private String getJazzDescription() {
        return "<p><strong>Композитор</strong>: Скотт Джо́плин (1868 - 1917, Scott Joplin)</p>" +
                "<p><strong>Произведение</strong>: &quot;The Entertainer&quot;</p>" +
                "<p><strong>Пример исполнения</strong>:<a href=\"https://www.youtube.com/watch?v=kHtwF-gpluc\" target=\"_blank\">&nbsp;https://www.youtube.com/watch?v=kHtwF-gpluc</a> (гитара, но мы сможем и на духовом инструменте)</p>" +
                "<p><strong>Ноты</strong>: <a href=\"https://cloud.mail.ru/public/52rG/TDexuQCrF\" target=\"_blank\">https://cloud.mail.ru/public/52rG/TDexuQCrF</a></p>" +
                "<p>Есть минусовки в разных темпах:&nbsp;</p>" +
                "<ul>" +
                "<li>Teмп 52: <a href=\"https://cloud.mail.ru/public/HqdY/PqLfo8JNV\" target=\"_blank\">https://cloud.mail.ru/public/HqdY/PqLfo8JNV</a></li>" +
                "<li>Темп 64: <a href=\"https://cloud.mail.ru/public/JEUp/nuxv6jj9x\" target=\"_blank\">https://cloud.mail.ru/public/JEUp/nuxv6jj9x</a></li>" +
                "<li>Темп 76: <a href=\"https://cloud.mail.ru/public/6V9S/eb3FbZiDe\" target=\"_blank\">https://cloud.mail.ru/public/6V9S/eb3FbZiDe</a></li>" +
                "<li>Темп 88: <a href=\"https://cloud.mail.ru/public/CKus/shbP5NdNs\" target=\"_blank\">https://cloud.mail.ru/public/CKus/shbP5NdNs</a></li>" +
                "<li>Темп 100: <a href=\"https://cloud.mail.ru/public/MqHe/m6j5jsPZK\" target=\"_blank\">https://cloud.mail.ru/public/MqHe/m6j5jsPZK</a></li>" +
                "</ul>" +
                "<p>Есть также плюс в одном из темпов: <a href=\"https://cloud.mail.ru/public/2xYE/1emrzc1eX\" target=\"_blank\">https://cloud.mail.ru/public/2xYE/1emrzc1eX</a></p>" +
                "<p>Удачи!</p>";
    }
}

