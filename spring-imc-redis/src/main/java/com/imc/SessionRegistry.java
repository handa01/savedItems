package com.imc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

//@Configuration
public class SessionRegistry extends WebSecurityConfigurerAdapter {

//    @Autowired
//    private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()

//                .formLogin().loginPage("/login.html").permitAll().and()//
//                .logout().logoutUrl("/logout").logoutSuccessUrl("/").deleteCookies("JSESSIONID")
                .formLogin()
                .and()
                .sessionManagement()
                .maximumSessions(3)
                .sessionRegistry(sessionRegistry());
    }

    @Bean
    public org.springframework.security.core.session.SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
//        System.out.println(">>>>>>>>>>>>>>>>> I'm a log");
//        return new IssSessionRegistry(sessionRepository);
    }

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .passwordEncoder(passwordEncoder)
                .withUser("user").password(passwordEncoder.encode("123456")).roles("USER")
                .and()
                .withUser("admin").password(passwordEncoder.encode("imc")).roles("USER", "ADMIN");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
