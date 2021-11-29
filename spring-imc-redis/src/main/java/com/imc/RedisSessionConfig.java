package com.imc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

import java.time.Duration;

@Configuration
@EnableRedisHttpSession
public class RedisSessionConfig extends AbstractHttpSessionApplicationInitializer {

    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {

        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
//        redisConfig.setPort(6379);
//        redisConfig.setHostName("localhost");

        // Enterprise cloud
//        redisConfig.setPort(18601);
//        redisConfig.setHostName("<<redis-hostname>>");
//        redisConfig.setPassword("<<redis-password>>");


        // cf service
        redisConfig.setPort(1249);
        redisConfig.setHostName("<<redis-cache-hostname>>");
        redisConfig.setPassword("<<redis-cache-password>>");

        JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder()
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(10))
                .usePooling()
                .and()
                .useSsl()
                .build();


        return new JedisConnectionFactory(redisConfig, jedisClientConfiguration);

    }
}
