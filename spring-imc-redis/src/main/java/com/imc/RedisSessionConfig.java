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
//        redisConfig.setHostName("redis-18601.c250.eu-central-1-1.ec2.cloud.redislabs.com");
//        redisConfig.setPassword("iRVy5Tbpg9z8txMpJ55WlutEsSWRSX4f");


        // cf service
        redisConfig.setPort(1249);
        redisConfig.setHostName("master.rg-b53cf680-1fc8-41e2-8fa9-f3d045e784be.9u04ff.use1.cache.amazonaws.com");
        redisConfig.setPassword("XhLjKapfymjoxhYBpQIimzPxDgjJLfyo");

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
