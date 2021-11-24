package com.imc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

//@Configuration
public class SessionRegistry extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()

//                .formLogin().loginPage("/login.html").permitAll().and()//
//                .logout().logoutUrl("/logout").logoutSuccessUrl("/").deleteCookies("JSESSIONID")
                .formLogin()
                .and()
                .sessionManagement()
                .maximumSessions(1)
                .sessionRegistry(sessionRegistry());
    }

    @Bean
    public org.springframework.security.core.session.SessionRegistry sessionRegistry() {
//        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
//        jedisConnectionFactory.setHostName("localhost");
//        jedisConnectionFactory.setPort(6379);
//        jedisConnectionFactory.setUsePool(true);
//        RedisOperationsSessionRepository sessionRepository = new RedisOperationsSessionRepository(jedisConnectionFactory);
//        return new SpringSessionBackedSessionRegistry<>(sessionRepository);
        return new SessionRegistryImpl();
    }

}
