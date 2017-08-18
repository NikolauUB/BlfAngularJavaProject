package wind.instrument.competitions;

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



    @RequestMapping(value="/testlogin", method = RequestMethod.GET)
    public String loginForm(ModelMap model){
        return "login";
    }

    /*@RequestMapping(value="/registerNewUser", method = RequestMethod.GET)
    public String registerNewUserForm(ModelMap model, UserEntity userEntity){
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();
        return showRegisterForm(model, session, userEntity);
    }*/

    /*@RequestMapping(value="/registerNewUser", method = RequestMethod.POST)
    public String registerNewUserSubmit(ModelMap model,
                                        @RequestBody MultiValueMap<String,String> formData,
                                        UserEntity userEntity,
                                        BindingResult bindingResult){
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();

        //System.out.println("user reply: " + formData.get("replies[]"));
        try {
            Question.checkUserReply((LinkedList<String>)formData.get("replies[]"),
                    (ArrayList<String>)session.getAttribute("question_answers"));
        } catch (BotCheckException e) {
            bindingResult.addError(new ObjectError("question", e.getLocalizedMessage()));
        }
        if(userEntity.getEmail() == null || userEntity.getEmail().length() == 0) {
            bindingResult.addError(new ObjectError("email", "Введите адрес электронной почты"));
        }

        userEntity.setEmail(userEntity.getEmail().toLowerCase());
        //todo email validation
        //else if(Utils.isEmailValid(userEntity.getEmail())) {
        //    bindingResult.addError(new ObjectError("email", "Неверный адрес электронной почты"));
        //}

        if (userEntity.getTypedPassword1() == null || userEntity.getTypedPassword1().length() == 0) {
            bindingResult.addError(new ObjectError("password", "Введите пароль"));
        } else if (userEntity.getTypedPassword2() == null || userEntity.getTypedPassword2().length() == 0) {
            bindingResult.addError(new ObjectError("password", "Повторите ввод пароля"));
        } else if (!userEntity.getTypedPassword1().equals(userEntity.getTypedPassword2())){
            bindingResult.addError(new ObjectError("password", "Пароли не совпадают"));
        }

        try {
            userEntity.generatePasswordHashes();
        } catch (Exception e) {
            bindingResult.addError(new ObjectError("password", e.getLocalizedMessage()));
        }
        if (bindingResult.hasErrors()) {
            return showRegisterForm(model, session, userEntity);
        }
        //todo validation
        em.persist(userEntity);

        model.addAttribute("email", userEntity.getEmail());
        return "login";
    }*/


    @RequestMapping(value="/welcome", method = RequestMethod.GET)
    public String welcome(ModelMap model){
        return "welcome";
    }

    /*private String showRegisterForm(ModelMap model, HttpSession session, UserEntity userEntity) {
        Question antiBotQuestion = new Question();
        model.addAttribute("questionText", antiBotQuestion.getQuestion());
        model.addAttribute("userEntity", userEntity);
        model.addAttribute("answers", antiBotQuestion.getQuestionAnswers());
        session.setAttribute("question_answers", antiBotQuestion.getCorrectAnswers());
        return "registerNewUser";
    }*/

}


