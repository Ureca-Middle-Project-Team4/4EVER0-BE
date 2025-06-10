package com.team4ever.backend.global.controller;

import com.team4ever.backend.global.security.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class JwtTokenController {
    private final JwtTokenProvider jwtProvider;
    private final RedisService redisService;

    public JwtTokenController(JwtTokenProvider jwtProvider,
                           RedisService redisService) {
        this.jwtProvider = jwtProvider;
        this.redisService = redisService;
    }

    @GetMapping("/{provider}")
    public void login(@PathVariable String provider, HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/" + provider);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String userId,
                                    HttpServletResponse response) {
        // 3-1) Redis에서 refresh token 삭제
        redisService.deleteRefreshToken(userId);

        // 3-2) ACCESS_TOKEN 쿠키를 즉시 만료시켜 제거
        ResponseCookie deleteCookie = ResponseCookie.from("ACCESS_TOKEN", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", deleteCookie.toString());

        return ResponseEntity.ok().body("Logged out");
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(@RequestParam String userId,
                                        HttpServletResponse response) {
        // 1) Redis 에서 저장된 리프레시 토큰 조회
        String refreshToken = redisService.getRefreshToken(userId);

        // 2) 없는 경우 또는 만료된 토큰인 경우 401 리턴
        if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 3) 새 액세스 토큰 생성
        String newAccessToken = jwtProvider.createAccessToken(userId);

        // 4) ResponseCookie 에 담아서 Set-Cookie 헤더로 추가
        ResponseCookie cookie = ResponseCookie.from("ACCESS_TOKEN", newAccessToken)
                .path("/")                            // 백엔드 전체 컨텍스트에 유효
                .maxAge(60 * 60 * 24)                 // 1일(초)
                .httpOnly(true)                       // JS 접근 불가
                .secure(true)                         // SameSite=None 인 경우 반드시 true
                .sameSite("None")                     // 크로스사이트에서도 전송
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 5) 쿠키만 던지고 빈 200 OK
        return ResponseEntity.ok().build();
    }
}