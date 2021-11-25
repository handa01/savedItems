//package com.imc;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.session.data.redis.config.ConfigureRedisAction;
//import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
//import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;
//
//@Configuration
//@EnableRedisHttpSession
//public class RedisSessionConfig extends AbstractHttpSessionApplicationInitializer{
//
//    @Bean
//    @Primary
//    public RedisConnectionFactory redisConnectionFactory() {
//
//        RedisStandaloneConfiguration a = new RedisStandaloneConfiguration();
//        a.setPort(6379);
//        a.setHostName("localhost");
//
////        a.setPort(18601);
////        a.setHostName("redis-18601.c250.eu-central-1-1.ec2.cloud.redislabs.com");
////        a.setPassword("iRVy5Tbpg9z8txMpJ55WlutEsSWRSX4f");
//
//        return new JedisConnectionFactory(a);
//    }
//}
