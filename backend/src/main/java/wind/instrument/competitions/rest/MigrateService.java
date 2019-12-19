package wind.instrument.competitions.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wind.instrument.competitions.configuration.SessionParameters;
import wind.instrument.competitions.data.CompetitionEntity;
import wind.instrument.competitions.data.CompetitionType;
import wind.instrument.competitions.middle.AdminInfo;

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

    @Autowired
    private ApplicationContext context;

    @RequestMapping("/stopServer")
    public String stopServer(HttpServletResponse res) {
        if (!AdminInfo.ADMIN_USERNAME.equals("" + httpSession.getAttribute(SessionParameters.USERNAME.name()))) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "Access Error!";
        }
        int exitCode = SpringApplication.exit(context, () -> 0);
        System.exit(exitCode);
        return "Server shutdown ...";
    }

    //for testing only
    @RequestMapping("/api/migrate")
    public String migrate(HttpServletRequest req, HttpServletResponse res) {
        if (!AdminInfo.ADMIN_USERNAME.equals("" + httpSession.getAttribute(SessionParameters.USERNAME.name()))) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "Access Error!";
        }

        Calendar cal = Calendar.getInstance();    
	cal.set(2020, 0, 20, 23, 59);
        CompetitionEntity competitionEntityConcert = new CompetitionEntity();
        competitionEntityConcert.setCompetitionType(CompetitionType.CONCERT);
        competitionEntityConcert.setCompetitionName("Онлайн-Концерт");
        competitionEntityConcert.setCompetitionDesc(this.getConcertDescription());
        competitionEntityConcert.setCompetitionSampleVideo("https://www.youtube.com/embed/YVxCXtjFXWI");
        competitionEntityConcert.setCompetitionStart(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        competitionEntityConcert.setCompetitionEnd(cal.getTime());
        competitionEntityConcert.setActive(false);
        competitionEntityConcert.setFuture(true);
        em.persist(competitionEntityConcert);

    
        cal.set(2020, 2, 2, 23, 59);
        CompetitionEntity competitionEntityComp = new CompetitionEntity();
        competitionEntityComp.setCompetitionType(CompetitionType.COMPOSITION);
        competitionEntityComp.setCompetitionName("Конкурс композиторов");
        competitionEntityComp.setCompetitionDesc(this.getCompositionDescription());
        competitionEntityComp.setCompetitionSampleVideo("https://www.youtube.com/embed/3CViCvab1KM");
        competitionEntityComp.setCompetitionStart(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        competitionEntityComp.setCompetitionEnd(cal.getTime());
        competitionEntityComp.setActive(false);
        competitionEntityComp.setFuture(true);
        em.persist(competitionEntityComp);
	
        /*
        cal.set(2019, 9, 14, 23, 59);
        CompetitionEntity competitionEntity = new CompetitionEntity();
        competitionEntity.setCompetitionType(CompetitionType.PRESCRIBED_BAROQUE);
        competitionEntity.setCompetitionName("Обязательная классическая программа");
        competitionEntity.setCompetitionDesc(this.getClassicDescription());
        competitionEntity.setCompetitionSampleVideo("https://www.youtube.com/embed/YWgzPL1xF0Q");
        competitionEntity.setCompetitionStart(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        competitionEntity.setCompetitionEnd(cal.getTime());
        competitionEntity.setActive(false);
        competitionEntity.setFuture(true);
	em.persist(competitionEntity);
	*/
	    /*
        CompetitionEntity competitionEntityJazz = new CompetitionEntity();
        cal.set(2019, 9, 14, 23, 59);
        competitionEntityJazz.setCompetitionType(CompetitionType.PRESCRIBED_JAZZ);
        competitionEntityJazz.setCompetitionName("Обязательная народная программа");
        competitionEntityJazz.setCompetitionDesc(this.getJazzDescription());
        competitionEntityJazz.setCompetitionSampleVideo("https://www.youtube.com/embed/J0C5YI_wfsU");
        competitionEntityJazz.setCompetitionStart(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        competitionEntityJazz.setCompetitionEnd(cal.getTime());
        competitionEntityJazz.setActive(false);
        competitionEntityJazz.setFuture(true);
        em.persist(competitionEntityJazz);
*/
/*	    
        CompetitionEntity competitionEntityFree = new CompetitionEntity();
        cal.set(2019, 9, 28, 23, 59);
        competitionEntityFree.setCompetitionType(CompetitionType.FREE);
        competitionEntityFree.setCompetitionName("Свободная программа");
        competitionEntityFree.setCompetitionDesc("Любое произведение по Вашему выбору с аккомпанементом или без.");
        competitionEntityFree.setCompetitionSampleVideo("https://www.youtube.com/embed/MMs1z9KoCxI");
	competitionEntityFree.setCompetitionStart(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        competitionEntityFree.setCompetitionEnd(cal.getTime());
        competitionEntityFree.setActive(false);
        competitionEntityFree.setFuture(true);
        em.persist(competitionEntityFree);
*/	
        /*
        Query query = em.createQuery(
                "update CompetitionEntity ce set ce.future = 'true' where ce.active = 'true' and ce.competitionType in (" +
                        CompetitionType.CONCERT.getValue() + ", " + CompetitionType.COMPOSITION.getValue() + ")");
        query.executeUpdate(); */
        return "done";

    }

    private String getCompositionDescription() {
        return "<p>В прошлом конкурсе композиторов победил <a href=\"https://www.youtube.com/watch?v=3CViCvab1KM\" target=\"blank\">Daerhon</a> с композицией \"Свободная Минутка\". Поздравляем!</p>" +
		"<p>Задание на следующий раз: </p>" +
		"<p><b>СИ</b>, <b>МИ</b>, <b>СИ</b>, <b>ДО</b>, <b>ЛЯ</b>, <b>МИ</b>, <b>ЛЯ</b>.</p>" +
                "<p>Последовательность не является темой - это лишь некоторый фрагмент темы, которую еще предстоит придумать. " +
                "Возможно, как угодно, менять длительность заданных нот и делать акценты в произвольном месте.</p>" +
                "<p>Голосование в композиторском конкурсе подобно голосованию на фестивалях.</p>" +
                "<p>Удачи!</p>";
    }

    private String getConcertDescription() {
        return "<p>Это не конкурс - голосование отсутствует.</p> <p> Тем не менее, лавровые листочки будут выдаваться автоматически каждому участнику из расчета количество номеров в концерте умножить на два.</p>" +
                "<p>Вы можете записать любые произведения в любом количестве, только, в случае нескольких произведений, их следует объединить в одно видео и в одну аудиозапись. </p>" +
                "<p>Горячо приветствуются ансамбли!</p>" +
                "<p>Удачи!</p>";
    }

    private String getClassicDescription() {
        return "<p><strong>Композитор</strong>: затерян в истории</p>" +
                "<p><strong>Произведение</strong>: Salarello, 17 век</p>" +
                "<p><strong>Ноты: <a href=\"https://musescore.com/user/8094196/scores/5396944\" target=\"_blank\">Ноты с Musescore.com сложные</a></p>" +
		"<p><strong>Ноты упрощенного варианта: <a href=\"http://dudari.ru/assets/score/Saltarello_odnogolosie_simple.pdf\" target=\"_blank\">Упрощенный вариант</a></p>" +
                "<p>Произведение следует играть без аккомпанемента</p>" +
                "<p>Удачи!</p>";
    }


    private String getJazzDescription() {
        return "<p><strong>Композитор</strong>: Народная китайская музыка</p>" +
                "<p><strong>Произведение</strong>: 茉莉花 ( Muo Li Hua )／ The Jasmine Flower </p>" +
                "<p><strong>Пример исполнения</strong>:<a href=\"https://www.youtube.com/watch?v=J0C5YI_wfsU\" target=\"_blank\">&nbsp;флейта дизи</a></p>" +
                "<p><strong>Ноты</strong>: <a href=\"https://www.flutetunes.com/tunes.php?id=713\" target=\"_blank\">На сайте flutetunes</a>, <a href=\"http://dudari.ru/assets/score/mo-li-hua.pdf\" target=\"_blank\">Или</a> " +
                "</p>" +
                "<p>Произведение следует играть без аккомпанемента</p>" +
                "<p>Удачи!</p>";
    }
}

