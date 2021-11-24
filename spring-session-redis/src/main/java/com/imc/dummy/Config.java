package com.imc.dummy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
public class Config {

	@Bean
	@Primary
	public LettuceConnectionFactory redisConnectionFactory() {

		RedisStandaloneConfiguration a = new RedisStandaloneConfiguration();

		//a.setDatabase(0);
		a.setPort(18601);
		a.setHostName("redis-18601.c250.eu-central-1-1.ec2.cloud.redislabs.com");
		a.setPassword("iRVy5Tbpg9z8txMpJ55WlutEsSWRSX4f");

		//LettuceClientConfiguration c = LettuceClientConfiguration.builder().useSsl().build();

		LettuceConnectionFactory l = new LettuceConnectionFactory(a);

		return l;
	}

}
