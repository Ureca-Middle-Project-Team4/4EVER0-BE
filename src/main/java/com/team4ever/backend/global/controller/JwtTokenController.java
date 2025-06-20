package com.team4ever.backend.global.controller;

import com.team4ever.backend.global.security.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;


@RestController
@RequestMapping("/api")
@Tag(name = "JWT 관련 API", description = "로그인/로그아웃, 토큰 재발급 처리 API")
public class JwtTokenController {
    private final JwtTokenProvider jwtProvider;
    private final RedisService redisService;

    public JwtTokenController(JwtTokenProvider jwtProvider,
                           RedisService redisService) {
        this.jwtProvider = jwtProvider;
        this.redisService = redisService;
    }

    @Operation(
            summary = "로그인",
            description = "OAuth2 인증 절차를 시작합니다. provider에는 'kakao', 'naver', 'google' 등이 들어갑니다."
    )
    @ApiResponse(responseCode = "302", description = "OAuth2 인증 페이지로 리다이렉트")
    @GetMapping("/auth/{provider}")
    public void login(@PathVariable String provider, HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/" + provider);
    }

    @Operation(
            summary = "로그아웃",
            description = "RefreshToken을 삭제하고 ACCESS_TOKEN 쿠키를 만료시켜 로그아웃 처리합니다."
    )
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
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

    @Operation(
            summary = "토큰 재발급",
            description = "RefreshToken이 유효한 경우 새로운 AccessToken을 쿠키에 담아 응답합니다."
    )
    @ApiResponse(responseCode = "200", description = "AccessToken 재발급 성공")
    @ApiResponse(responseCode = "401", description = "RefreshToken이 없거나 유효하지 않음")
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