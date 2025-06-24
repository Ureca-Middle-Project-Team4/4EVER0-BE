package com.team4ever.backend.global.security;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;

public class RedisAuthorizationRequestRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final String PREFIX = "oauth2_auth_request:";
    private final RedisTemplate<String, OAuth2AuthorizationRequest> redis;
    private final Duration expire;

    public RedisAuthorizationRequestRepository(
            RedisTemplate<String, OAuth2AuthorizationRequest> redisTemplate,
            Duration expire) {
        this.redis = redisTemplate;
        this.expire = expire;
    }

    private String key(String state) {
        return PREFIX + state;
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        String state = request.getParameter(OAuth2ParameterNames.STATE);
        if (state == null) return null;
        return redis.opsForValue().get(key(state));
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        if (authRequest == null) return;
        redis.opsForValue()
                .set(key(authRequest.getState()), authRequest, expire);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        String state = request.getParameter(OAuth2ParameterNames.STATE);
        if (state == null) return null;
        String k = key(state);
        OAuth2AuthorizationRequest req = redis.opsForValue().get(k);
        redis.delete(k);
        return req;
    }
}
