package com.team4ever.backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import com.team4ever.backend.global.security.RedisAuthorizationRequestRepository;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(
            @Value("${spring.data.redis.host}") String host,
            @Value("${spring.data.redis.port}") int port) {
        return new LettuceConnectionFactory(host, port);
    }

    // (A) OAuth2 인가 요청 저장소 전용 템플릿
    @Bean
    public RedisTemplate<String, OAuth2AuthorizationRequest> authRequestRedisTemplate(
            RedisConnectionFactory cf) {
        RedisTemplate<String, OAuth2AuthorizationRequest> tpl = new RedisTemplate<>();
        tpl.setConnectionFactory(cf);
        tpl.setKeySerializer(new StringRedisSerializer());
        // JDK Serialization으로 저장/로드
        tpl.setValueSerializer(new JdkSerializationRedisSerializer());
        tpl.afterPropertiesSet();
        return tpl;
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository(
            RedisTemplate<String, OAuth2AuthorizationRequest> authRequestRedisTemplate) {
        return new RedisAuthorizationRequestRepository(authRequestRedisTemplate, Duration.ofMinutes(5));
    }

    // (B) JWT/리프레시 토큰 전용 문자열 템플릿
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory cf) {
        return new StringRedisTemplate(cf);
    }
}
