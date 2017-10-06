package wind.instrument.competitions.experiments;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Controller
@Transactional
public class LoginController {

    @PersistenceContext
    private EntityManager em;

    /**
     * Test login
      * @param model
     * @return
     */
    @RequestMapping(value="/testlogin", method = RequestMethod.GET)
    public String loginForm(ModelMap model){
        return "login";
    }


    @RequestMapping(value="/welcome", method = RequestMethod.GET)
    public String welcome(ModelMap model){
        return "welcome";
    }


}


