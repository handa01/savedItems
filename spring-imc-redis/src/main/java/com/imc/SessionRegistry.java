package com.imc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

//@Configuration
public class SessionRegistry extends WebSecurityConfigurerAdapter {

    @Autowired
    private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

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
                .maximumSessions(3)
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
//        return new SessionRegistryImpl();
        return new IssSessionRegistry(sessionRepository);
    }

//    @SuppressWarnings("unchecked")
//    @Bean
//    public SpringSessionBackedSessionRegistry sessionRegistry() {
////        RedisConnectionFactory redisConnectionFactory = redisConnectionFactory();
////        RedisTemplate<Object, Object> redisTemplate = stringTemplate(redisConnectionFactory);
//        return new SpringSessionBackedSessionRegistry(this.sessionRepository) {
//            @Override
//            public void registerNewSession(final String sessionId, final Object principal) {
//                super.registerNewSession(sessionId, principal);
//
////                final StopWatchISS stopWatchISS = new StopWatchISS();
////                stopWatchISS.start();
//
//                try {
////                    final IMCUserDetails userDetails = (IMCUserDetails) principal;
////                    LOGGER.debug("Registering new Session: {}", sessionId);
////
////                    final UsageDTO usage = new UsageDTO();
////                    usage.setSessionID(sessionId);
////                    usage.setUserBO(userDetails.getUserBO());
////                    usage.setUserName(userDetails.getUsername().toLowerCase());
////                    usage.setClient(getBrowserDetails());
////
////                    usageService.startSessionUsage(usage);
//
////                    sessionIdsTemplate.opsForValue().set(sessionId, new SessionInformation(principal, sessionId, new Date()));
////
////                    principalsTemplate.opsForSet().add(buildPrincipalKey(principal), sessionId);
//                } catch (final Exception e) {
//                    System.out.println("Unable to add usage. " + sessionId);
//                }
//
//                System.out.println("Time taken to save session id: {} ms" + sessionId);
//            }
//        };
//    }

}
