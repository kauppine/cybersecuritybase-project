package sec.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
     
        http.csrf().disable();
        
        http.headers().xssProtection().disable();
        http.headers().httpStrictTransportSecurity().disable();
        http.headers().frameOptions().disable();
        http.headers().cacheControl().disable();
        http.headers().disable();
        
        http.sessionManagement().sessionFixation().none();
        
        
       
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/index").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/register/").permitAll()
                .antMatchers("/register*").permitAll()
                .antMatchers("/images/*").permitAll()
                .antMatchers("/images/*/delete").permitAll()
                .anyRequest().authenticated();
        http.formLogin().loginPage("/login")
                .permitAll()            
                .and()
            .logout()                                    
                .permitAll();
        

        
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new PlaintextPasswordEncoder());
    }
}
