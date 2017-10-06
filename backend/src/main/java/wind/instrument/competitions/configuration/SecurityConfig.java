package wind.instrument.competitions.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private EntityManager em;

    private DataSource getJPADataSource() {
        EntityManagerFactoryInfo info = (EntityManagerFactoryInfo) em.getEntityManagerFactory();
        return info.getDataSource();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(this.getJPADataSource())
                .passwordEncoder(new PasswordEncoder())
                .usersByUsernameQuery(
                        "select email, password_hash||';'||password_salt, true from forumdata.users where email=LOWER(?)"
                )
                .authoritiesByUsernameQuery(
                        "select email, 'USER' from forumdata.users where email=LOWER(?)"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/testlogin").permitAll()
                .antMatchers("/api/votedata").permitAll()
                .antMatchers("/api/getVotingOpinions").permitAll()
                .antMatchers("/api/checkAuth").permitAll()
                .antMatchers("/api/getCompetitionMembers").permitAll()
                .antMatchers("/api/getChangedKeywords").permitAll()
                .antMatchers("/api/getUsersUpdates").permitAll()
                .antMatchers("/api/getUserDetails").permitAll()
                .antMatchers("/api/getActiveCompetitions").permitAll()
                .antMatchers("/api/getActiveCompetitionData").permitAll()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/api/question").permitAll()
                .antMatchers("/api/register").permitAll()
                .antMatchers("/api/changepasswordtid").permitAll()
                //.antMatchers("/editBytiny").permitAll()
                //.antMatchers("/editBynicEdit").permitAll()
                .antMatchers("/css").permitAll()
                .anyRequest().authenticated()
                //.and()
                //.formLogin()
                //.loginPage("/api/checkAuth")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .deleteCookies("remove")
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/");
    }
}
