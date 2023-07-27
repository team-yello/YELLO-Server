package com.yello.server.infrastructure.redis.configuration;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfiguration {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<Long, ServiceTokenVO> redisTokenTemplate() {
        RedisTemplate<Long, ServiceTokenVO> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(RedisSerializer.java(Long.class.getClassLoader()));
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(ServiceTokenVO.class));
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public ValueOperations<Long, ServiceTokenVO> tokenValueOperations() {
        return redisTokenTemplate().opsForValue();
    }
}
