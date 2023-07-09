package com.yello.server.global.configuration;

import com.yello.server.domain.user.dto.response.ServiceTokenVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

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

    /**
     * Redis는 비트 타입으로 데이터가 저장되기 때문에, 이 애플리케이션에서 String 타입으로 Key와 Value 데이터를 관리하기 위해 아래와 같이 Serializer를 정의
     */
    @Bean
    public RedisTemplate<String, Integer> redisTemplate() {
        RedisTemplate<String, Integer> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
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
