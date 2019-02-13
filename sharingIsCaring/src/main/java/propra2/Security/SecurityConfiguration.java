package propra2.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .anyRequest().permitAll();
//        http.authorizeRequests()
//                .antMatchers("/registration").permitAll()
//                .antMatchers("/admin").hasRole("ADMIN")
//                .anyRequest().authenticated()
//                .and().formLogin().permitAll()
//                .and().logout().permitAll();
//        http.userDetailsService(userDetailsService);
    }

}
