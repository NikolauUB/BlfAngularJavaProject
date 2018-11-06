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
	    
	cal.set(2018, 0, 14, 23, 59);
        CompetitionEntity competitionEntityConcert = new CompetitionEntity();
        competitionEntityConcert.setCompetitionType(CompetitionType.CONCERT);
        competitionEntityConcert.setCompetitionName("Онлайн-Концерт");
        competitionEntityConcert.setCompetitionDesc(this.getConcertDescription());
        competitionEntityConcert.setCompetitionSampleVideo("https://www.youtube.com/embed/ZdUWPA_AX6o");
        competitionEntityConcert.setCompetitionStart(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        competitionEntityConcert.setCompetitionEnd(cal.getTime());
        competitionEntityConcert.setActive(false);
        competitionEntityConcert.setFuture(true);
        em.persist(competitionEntityConcert);

    
        cal.set(2019, 2, 4, 23, 59);
        CompetitionEntity competitionEntityComp = new CompetitionEntity();
        competitionEntityComp.setCompetitionType(CompetitionType.COMPOSITION);
        competitionEntityComp.setCompetitionName("Конкурс композиторов");
        competitionEntityComp.setCompetitionDesc(this.getCompositionDescription());
        competitionEntityComp.setCompetitionSampleVideo("https://www.youtube.com/embed/1_lWUeZPJYk");
        competitionEntityComp.setCompetitionStart(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        competitionEntityComp.setCompetitionEnd(cal.getTime());
        competitionEntityComp.setActive(false);
        competitionEntityComp.setFuture(true);
        em.persist(competitionEntityComp);

        
//        cal.set(2018, 9, 15, 23, 59);
//        CompetitionEntity competitionEntity = new CompetitionEntity();
//        competitionEntity.setCompetitionType(CompetitionType.PRESCRIBED_BAROQUE);
//        competitionEntity.setCompetitionName("Обязательная классическая программа");
//        competitionEntity.setCompetitionDesc(this.getClassicDescription());
//        competitionEntity.setCompetitionSampleVideo("http://dudari.ru/assets/score/BachAria.pdf");
//        competitionEntity.setCompetitionStart(cal.getTime());
//        cal.add(Calendar.MONTH, 1);
//        competitionEntity.setCompetitionEnd(cal.getTime());
//        competitionEntity.setActive(false);
//        competitionEntity.setFuture(true);
//        Query query = em.createQuery("update CompetitionEntity ce set ce.future = 'false' where ce.competitionType = 0");
//        query.executeUpdate();
//        em.persist(competitionEntity);

//        CompetitionEntity competitionEntityJazz = new CompetitionEntity();
//        cal.set(2018, 9, 15, 23, 59);
//        competitionEntityJazz.setCompetitionType(CompetitionType.PRESCRIBED_JAZZ);
//        competitionEntityJazz.setCompetitionName("Обязательная джазовая программа");
//        competitionEntityJazz.setCompetitionDesc(this.getJazzDescription());
//        competitionEntityJazz.setCompetitionSampleVideo("https://www.youtube.com/embed/5uzsKr1qNe8");
//        competitionEntityJazz.setCompetitionStart(cal.getTime());

//        cal.add(Calendar.MONTH, 1);
//        competitionEntityJazz.setCompetitionEnd(cal.getTime());
//        competitionEntityJazz.setActive(false);
//        competitionEntityJazz.setFuture(true);
//        Query queryJazz = em.createQuery("update CompetitionEntity ce set ce.future = 'false' where ce.competitionType = 1");
//        queryJazz.executeUpdate();
//        em.persist(competitionEntityJazz);

//        CompetitionEntity competitionEntityFree = new CompetitionEntity();
//        cal.set(2018, 9, 29, 23, 59);
//        competitionEntityFree.setCompetitionType(CompetitionType.FREE);
//        competitionEntityFree.setCompetitionName("Свободная программа");
//        competitionEntityFree.setCompetitionDesc("Любое произведение по Вашему выбору с аккомпанементом или без.");
//        competitionEntityFree.setCompetitionSampleVideo("https://www.youtube.com/embed/_FKFwX2Wsq0");
//        competitionEntityFree.setCompetitionStart(cal.getTime());
//        cal.add(Calendar.MONTH, 1);
//        competitionEntityFree.setCompetitionEnd(cal.getTime());
//        competitionEntityFree.setActive(false);
//        competitionEntityFree.setFuture(true);
//        Query queryFree = em.createQuery("update CompetitionEntity ce set ce.future = 'false' where ce.competitionType = 2");
//        queryFree.executeUpdate();
//        em.persist(competitionEntityFree);
        /*
        Query query = em.createQuery(
                "update CompetitionEntity ce set ce.future = 'true' where ce.active = 'true' and ce.competitionType in (" +
                        CompetitionType.CONCERT.getValue() + ", " + CompetitionType.COMPOSITION.getValue() + ")");
        query.executeUpdate(); */
        return "done";

    }

    private String getCompositionDescription() {
        return "<p>Обычно в композиторском конкурсе задание придумывает тот, чьё сочинение набрало больше голосов в предыдущем конкурсе. " +
                "Однако, этот раз необычный. " +
                "Победителем <a target=\"blank\" href=\"http://dudari.ru/showByid/5600\">предущего конкурса</a> стал " +
                "<a target=\"blank\" href=\"https://www.youtube.com/watch?v=1_lWUeZPJYk\">Максим Хорош</a> с сочинением " +
                "<a target=\"blank\" href=\"https://app.box.com/s/1x7mbvjhcrofdy3sef4hmwc335isrqf7\">\"Соло дождя\"</a> (справа), " +
                "и пораздумав он подарил своё право придумывать задание на следующий раз другому участнику " +
                "<a target=\"blank\" href=\"https://www.youtube.com/watch?v=YST08L09IkA\">Татьяне Ивановне</a>" +
                "с созвучным сочинением <a target=\"blank\" href=\"http://dudari.ru/assets/score/TativaAboveARiver.pdf\">\"Над рекой\"</a>.</p>" +
                "<p>Задание в композиторском конкурсе - это обязательная последовательность нот, которая должна встретиться в сочинении " +
                "хотя бы один раз.</p>" +
				"<p>Татьяна Ивановна придумала следующее задание.</p>" +
				"<p>к<b>Ля</b>ксой <b>Си</b>-<b>Ре</b>невой лег на кон<b>Соль</b></br>" +
                "кот, наблюдая в окно за <b>Си</b>ницей,</br>" +
                "прежде, чем прыгнуть, решил затаиться,</br>" +
                "прыгнул за птичкой, но в<b>Ля</b>пался в <b>Соль</b></p>" +
                "<p>Жду Ваши сочинения, включающие заданную последовательность нот: </br>" +
                "<b><font size=\"3\">ЛЯ, СИ, РЕ, СОЛЬ, СИ, ЛЯ, СОЛЬ</font></b></p>" +
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
        return "<p><strong>Композитор</strong>: Иоганн Себастьян Бах</p>" +
                "<p><strong>Произведение</strong>: Ария</p>" +
                "<p><strong>Ноты: <a href=\"http://dudari.ru/assets/score/BachAria.pdf\" target=\"_blank\">Для сопрано</a>, &nbsp;<a href=\"http://dudari.ru/assets/score/BachAriaAlt.pdf\" target=\"_blank\">Для альта</a></p>" +
                "<p>Произведение следует играть без аккомпанемента</p>" +
                "<p>Удачи!</p>";
    }


    private String getJazzDescription() {
        return "<p><strong>Композитор</strong>: George Shearing</p>" +
                "<p><strong>Произведение</strong>: &quot;Lullaby of Birdland&quot;</p>" +
                "<p><strong>Пример исполнения</strong>:<a href=\"https://www.youtube.com/watch?v=5uzsKr1qNe8\" target=\"_blank\">&nbsp;Кларнет</a></p>" +
                "<p><strong>Ноты</strong>: <a href=\"http://dudari.ru/assets/score/GShearingLullabyofBirdlandAmiSoprano.pdf\" target=\"_blank\">для сопрано в ля миноре</a>, " +
                "<a href=\"http://dudari.ru/assets/score/GShearingLullabyofBirdlandGmiSoprano.pdf\" target=\"_blank\">для сопрано в соль миноре</a>, " +
                "<a href=\"http://dudari.ru/assets/score/GShearingLullabyofBirdlandCmiAlt.pdf\" target=\"_blank\">для альта в до миноре</a>, " +
                "<a href=\"http://dudari.ru/assets/score/GShearingLullabyofBirdlandDmiAlt.pdf\" target=\"_blank\">для альта в ре миноре</a>" +
                "</p>" +
                "<p>Минусовки:&nbsp;</p>" +
                "<ul>" +
                "<li><a href=\"http://dudari.ru/assets/minus/LullabyOfBirdlandGShearingAmi.mp3\" target=\"_blank\">Cопрано (ля минор)</a></li>" +
                "<li><a href=\"http://dudari.ru/assets/minus/LullabyOfBirdlandGeorgeShearingGmi.mp3\" target=\"_blank\">Cопрано (соль минор)</a></li>" +
                "<li><a href=\"http://dudari.ru/assets/minus/LullabyOfBirdlandGShearingСmi.mp3\" target=\"_blank\">Альт (до минор)</a></li>" +
                "<li><a href=\"http://dudari.ru/assets/minus/LullabyOfBirdlandGShearingDmi.mp3\" target=\"_blank\">Альт (ре минор)</a></li>" +
                "</ul>" +
                "<p>Удачи!</p>";
    }
}

