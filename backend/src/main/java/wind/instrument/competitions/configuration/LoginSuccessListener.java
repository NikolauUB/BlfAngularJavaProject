package wind.instrument.competitions.configuration;

import wind.instrument.competitions.data.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpSession;

@Component
public class LoginSuccessListener implements ApplicationListener {

    private static Logger LOG = LoggerFactory.getLogger(LoginSuccessListener.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private HttpSession httpSession;

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if(applicationEvent instanceof  AuthenticationSuccessEvent) {
            AuthenticationSuccessEvent evt = (AuthenticationSuccessEvent)applicationEvent;
            String login = evt.getAuthentication().getName();
            TypedQuery<UserEntity> userQ = em.createQuery("select u from UserEntity u where u.email = :email", UserEntity.class);
            UserEntity user = userQ.setParameter("email",login).getSingleResult();
            //todo unset it on logout
            httpSession.setAttribute(SessionParameters.USERNAME.name(), user.getUsername());
            httpSession.setAttribute(SessionParameters.USER_CREATED.name(), user.getCreated());
            httpSession.setAttribute(SessionParameters.USER_ID.name(), user.getUserId());
        }
    }
}