// src/main/java/com/example/demo/config/SecurityConfig.java
package com.team4ever.backend.global.config;

import com.team4ever.backend.global.security.CustomOAuth2SuccessHandler;
import com.team4ever.backend.global.security.CustomOAuth2UserService;
import com.team4ever.backend.global.security.RedisService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRepo;
    private final CustomOAuth2UserService customUserService;
    private final CustomOAuth2SuccessHandler successHandler;
    private final RedisService redisService;

    // ↓ RedisTemplate 대신 AuthorizationRequestRepository 를 주입받도록 변경
    public SecurityConfig(
            AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRepo,
            CustomOAuth2UserService customUserService,
            CustomOAuth2SuccessHandler successHandler,
            RedisService redisService
    ) {
        this.authorizationRepo = authorizationRepo;
        this.customUserService = customUserService;
        this.successHandler    = successHandler;
        this.redisService      = redisService;

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1) 쿠폰 전체 조회는 인증 없이 허용
                        .requestMatchers(HttpMethod.GET, "/api/coupons").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user").permitAll()
                        // 2) OAuth 로그인용 엔드포인트
                        .requestMatchers(
                                // 허용 API 목록
//                                "/**",
                                "/api/auth/**",
                                "/api/refresh",
                                "/api/plans",
                                "/api/plans/**",
                                "/api/subscriptions/main",
                                "/api/subscriptions/brands",
                                "/api/popups",
                                "/api/popups/**",
                                "/api/coupons",
                                "/api/chat",

                                // ↓ Swagger
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        // 1) /login/{provider} 경로로 인가 요청을 받도록 설정
                        .authorizationEndpoint(endpoint ->
                                endpoint.baseUri("/api/auth")
                                .authorizationRequestRepository(authorizationRepo)
                        )
                        // 2) userInfo
                        .userInfoEndpoint(u -> u
                                .userService(customUserService)
                        )
                        // 3) 로그인 성공 핸들러
                        .successHandler(successHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")                      // 클라이언트가 호출할 로그아웃 URL
                        .invalidateHttpSession(true)
                        .deleteCookies("ACCESS_TOKEN", "JSESSIONID")
                        .addLogoutHandler((request, response, authentication) -> {
                            if (authentication != null && authentication.getName() != null) {
                                redisService.deleteRefreshToken(authentication.getName());
                            }
                        })
                        // 필요하다면 쿠키 이름들
                        .logoutSuccessHandler((req, res, auth) -> {
                            res.setStatus(HttpServletResponse.SC_OK);
                            // JSON 응답이 필요하면 작성하거나, redirect 하려면
                            // res.sendRedirect("http://localhost:5173/login");
                        })
                );

        return http.build();
    }
}