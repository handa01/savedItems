package com.imc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

@Configuration
@EnableRedisHttpSession
public class RedisSessionConfig {

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {

        RedisStandaloneConfiguration a = new RedisStandaloneConfiguration();
        a.setPort(6379);
        a.setHostName("localhost");

        LettuceConnectionFactory l = new LettuceConnectionFactory(a);
        return l;
    }

    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }
}
