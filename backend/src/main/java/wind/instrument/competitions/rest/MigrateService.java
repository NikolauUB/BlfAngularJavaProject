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
    @RequestMapping("/migrate")
    public String migrate(HttpServletRequest req, HttpServletResponse res) {
        if (!AdminInfo.ADMIN_USERNAME.equals("" + httpSession.getAttribute(SessionParameters.USERNAME.name()))) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "Access Error!";
        }
        /*
        Calendar cal = Calendar.getInstance();
        cal.set(2018, 2, 1, 23, 59);
        CompetitionEntity competitionEntity = new CompetitionEntity();
        competitionEntity.setCompetitionType(CompetitionType.COMPOSITION);
        competitionEntity.setCompetitionName("Конкурс композиторов");
        competitionEntity.setCompetitionDesc(this.getCompositionDescription());
        competitionEntity.setCompetitionSampleVideo("https://www.youtube.com/embed/ux7u5MnsfSQ");
        competitionEntity.setCompetitionStart(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        competitionEntity.setCompetitionEnd(cal.getTime());
        competitionEntity.setActive(true);
        Query query = em.createQuery("update CompetitionEntity ce set ce.active = 'false' where ce.competitionType = 3");
        query.executeUpdate();
        em.persist(competitionEntity);
        */
        Calendar cal = Calendar.getInstance();
        cal.set(2018, 3, 16, 23, 59);
        CompetitionEntity competitionEntity = new CompetitionEntity();
        competitionEntity.setCompetitionType(CompetitionType.PRESCRIBED_BAROQUE);
        competitionEntity.setCompetitionName("Обязательная классическая программа");
        competitionEntity.setCompetitionDesc(this.getClassicDescription());
        competitionEntity.setCompetitionSampleVideo("http://dudari.ru/assets/score/PreludeFMajor.pdf");
        competitionEntity.setCompetitionStart(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        competitionEntity.setCompetitionEnd(cal.getTime());
        competitionEntity.setActive(false);
        competitionEntity.setFuture(true);
        //Query query = em.createQuery("update CompetitionEntity ce set ce.active = 'false' where ce.competitionType = 4");
        //query.executeUpdate();
        em.persist(competitionEntity);

        CompetitionEntity competitionEntityJazz = new CompetitionEntity();
        cal.set(2018, 3, 16, 23, 59);
        competitionEntityJazz.setCompetitionType(CompetitionType.PRESCRIBED_JAZZ);
        competitionEntityJazz.setCompetitionName("Обязательная джазовая программа");
        competitionEntityJazz.setCompetitionDesc(this.getJazzDescription());
        competitionEntityJazz.setCompetitionSampleVideo("https://www.youtube.com/embed/kmfeKUNDDYs");
        competitionEntityJazz.setCompetitionStart(cal.getTime());

        cal.add(Calendar.MONTH, 1);
        competitionEntityJazz.setCompetitionEnd(cal.getTime());
        competitionEntityJazz.setActive(false);
        competitionEntityJazz.setFuture(true);
        //Query queryJazz = em.createQuery("update CompetitionEntity ce set ce.active = 'false' where ce.competitionType = 1");
        //queryJazz.executeUpdate();
        em.persist(competitionEntityJazz);

        CompetitionEntity competitionEntityFree = new CompetitionEntity();
        cal.set(2018, 3, 30, 23, 59);
        competitionEntityFree.setCompetitionType(CompetitionType.FREE);
        competitionEntityFree.setCompetitionName("Свободная программа");
        competitionEntityFree.setCompetitionDesc("Любое произведение по Вашему выбору!");
        competitionEntityFree.setCompetitionSampleVideo("https://www.youtube.com/embed/3PIgSkVRboo");
        competitionEntityFree.setCompetitionStart(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        competitionEntityFree.setCompetitionEnd(cal.getTime());
        competitionEntityFree.setActive(false);
        competitionEntityFree.setFuture(true);
        //Query queryFree = em.createQuery("update CompetitionEntity ce set ce.active = 'false' where ce.competitionType = 2");
        //queryFree.executeUpdate();
        em.persist(competitionEntityFree);

        Query query = em.createQuery(
                "update CompetitionEntity ce set ce.future = 'true' where ce.active = 'true' and ce.competitionType in (" +
                        CompetitionType.CONCERT.getValue() + ", " + CompetitionType.COMPOSITION.getValue() + ")");
        query.executeUpdate();
        query = em.createQuery(
                "update CompetitionEntity ce set ce.competitionStart = '2018-03-05 23:59:00', ce.competitionEnd = '2018-04-05 23:59:00' where ce.active = 'true' and ce.competitionType in (" +
                        CompetitionType.CONCERT.getValue() + ", " + CompetitionType.COMPOSITION.getValue() + ")");
        query.executeUpdate();
        return "done";

    }

    private String getCompositionDescription() {
        return "<p>В композиторском конкурсе задание придумывает победитель предыдущего конкурса. Этот конкурс не первый - начало было положено на форуме blf.ru</p>" +
                "<p>Задание в композитрроском конкурсе - это обязательная последовательность нот, которая должна встретиться в сочинении хотя бы один раз.</p>" +
                "<p>Победитель предыдущего конкурса, <a target=\"blank\" href=\"https://www.youtube.com/watch?v=ux7u5MnsfSQ\">Максим Хорош</a>, придумал следующую последовательность нот: </br>" +
                "<b><font size=\"3\">Фа, ля, до, ре, си, соль, ми</font></b></p>" +
                "<p>И стишок:</br>" +
                "<font size=\"3\" color=\"blue\">Не каждый музыку в них поймет,</br>" +
                "Сколько в них не смотри.</br>" +
                "Но если их сыграет музыкант,</br>" +
                "То каждый этим нотам будет рад!</font></p>" +
                "<p>Последовательность не является темой - это лишь некоторый фрагмент темы, которую еще предстоит придумать. Mожно, как угодно, менять длительность заданных нот и делать акценты в произвольном месте.</p>" +
                "<p>Голосование будет подобным голосованию на фестивалях. Тот, кто наберёт больше листочков в данном конкурсе, придумывает задание на следующий раз.</p>" +
                "<p>Удачи!</p>";
    }

    private String getConcertDescription() {
        return "<p>Это не конкурс - голосование отсутствует.</p> <p> Тем не менее, лавровые листочки будут выдаваться автоматически каждому участнику из расчета количество номеров в концерте умножить на два.</p>" +
                "<p>Вы можете записать любые произведения в любом количестве, только, в случае нескольких произведений, их следует объединить в одно видео и в одну аудиозапись. </p>" +
                "<p>Горячо приветствуются ансамбли!</p>" +
                "<p>Удачи!</p>";
    }

    private String getClassicDescription() {
        return "<p><strong>Композитор</strong>: неизвестен (London 1728)</p>" +
                "<p><strong>Произведение</strong>: Прелюдия 1 из сочинения \"Prelude and Aria\"</p>" +
                "<p><strong>Пример исполнения</strong>: \"живой\" пример отсутствует: <a href=\"http://dudari.ru/assets/minus/PreludeFMajor.mp3\" target=\"_blank\">MIDI из нотного редактора</a></p>" +
                "<p><strong>Ноты: <a href=\"http://dudari.ru/assets/score/PreludeFMajor.pdf\" target=\"_blank\">фа мажор</a>, &nbsp;<a href=\"http://dudari.ru/assets/score/PreludeCMajor.pdf\" target=\"_blank\">до мажор</a></p>" +
                "<p>Стилистика произведения похожа на стиль композитора-клавесиниста Панкраса Руайе, который жил в то же время: " +
                "<a href=\"https://www.youtube.com/watch?v=8PxZSN-B6uI\" target=\"_blank\">Le Vertigo</a>, " +
                "<a href=\"https://imslp.nl/imglnks/usimg/f/f2/IMSLP42978-PMLP54515-PremierLivre.pdf\" target=\"_blank\">Ноты Le Vertigo находятся на странице 19</a>. " +
                "У него используются похожие украшения 32-ми. Если слушать с нотами, можно заметить, что играются такие украшения не совсем так, как написаною</p>" +
                "<p>Удачи!</p>";
    }


    private String getJazzDescription() {
        return "<p><strong>Композитор</strong>: Jerry Herman</p>" +
                "<p><strong>Произведение</strong>: &quot;Hello, Dolly&quot;</p>" +
                "<p><strong>Пример исполнения</strong>:<a href=\"https://www.youtube.com/watch?v=kmfeKUNDDYs\" target=\"_blank\">&nbsp;Луи Армстронг</a></p>" +
                "<p><strong>Ноты</strong>: <a href=\"http://www.jazzpla.net/H/Hellodolly.htm\" target=\"_blank\">Джазовый стандарт</a>, " +
                "<a href=\"http://dudari.ru/assets/score/HelloDollyCMajor.pdf\" target=\"_blank\">в до мажоре</a>, <a href=\"http://dudari.ru/assets/score/HelloDollyFMajor.pdf\" target=\"_blank\">в фа мажоре</a>" +
                "</p>" +
                "<p>Минусовки:&nbsp;</p>" +
                "<ul>" +
                "<li>До мажор: <a href=\"http://dudari.ru/assets/minus/LouisArmstrong-HelloDolly(minusCMajor).mp3\" target=\"_blank\">LouisArmstrong-HelloDolly(minusCMajor).mp3</a></li>" +
                "<li>Фа мажор: <a href=\"http://dudari.ru/assets/minus/LouisArmstrong-HelloDolly(minusFmajor).mp3\" target=\"_blank\">LouisArmstrong-HelloDolly(minusFmajor).mp3</a></li>" +
                "<li>Здесь можно скачать минусовку в любой тональности и темпе, и послушать плюс: <a href=\"http://x-minus.me/track/6035/hello-dolly\" target=\"_blank\">http://x-minus.me/track/6035/hello-dolly</a></li>" +
                "</ul>" +
                "<p>Ноты условные, так как точно не передают ритм. Слушайте примеры исполнения и пытайтесь играть на слух.</p>" +
                "<p>Удачи!</p>";
    }
}

