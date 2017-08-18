package wind.instrument.competitions;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Controller
public class HomeController {
    @PersistenceContext
    private EntityManager em;

    @RequestMapping(value="/home")
    public String showHomePage(ModelMap model){

        System.out.println("Current user:" + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return "welcome";
    }
    @RequestMapping(value="/editBytiny", method = RequestMethod.GET)
    public String showEditorTiny(ModelMap model) {
        return "ThemeThreadTinyTest";
    }

    @RequestMapping(value="/editBynicEdit", method = RequestMethod.GET)
    public String showEditor(ModelMap model) {
        return "NicEditTest";
    }



}
