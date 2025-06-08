package com.team4ever.backend.global.security;

import org.springframework.http.ResponseCookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private static final String FRONTEND_URL = "http://localhost:5173";

    private final JwtTokenProvider jwtProvider;
    private final RedisService redisService;

    public CustomOAuth2SuccessHandler(JwtTokenProvider jwtProvider,
                                      RedisService redisService) {
        this.jwtProvider = jwtProvider;
        this.redisService = redisService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User user = oauthToken.getPrincipal();
        String regId = oauthToken.getAuthorizedClientRegistrationId();

        // 사용자 ID 결정 (구글은 "sub", 그 외는 "id")
        String userId = "google".equals(regId)
                ? user.getAttribute("sub")
                : user.getAttribute("id");
        if (userId == null) {
            userId = authentication.getName();
        }

        // 토큰 생성 및 Redis 저장
        String accessToken  = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);
        redisService.storeRefreshToken(userId, refreshToken);

        // 쿠키 세팅
        ResponseCookie cookie = ResponseCookie.from("ACCESS_TOKEN", accessToken)
                .path("/")
                .maxAge(60 * 60 * 24)       // 1일
                .httpOnly(true)
                .secure(true)               // SameSite=None 일 때는 반드시 secure
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        // Redirect 제거 → 200 OK 만 반환
        response.sendRedirect(FRONTEND_URL);
    }
}
