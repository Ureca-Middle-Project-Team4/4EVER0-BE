// src/main/java/com/example/demo/config/SecurityConfig.java
package com.team4ever.backend.global.config;

import com.team4ever.backend.global.security.CustomOAuth2SuccessHandler;
import com.team4ever.backend.global.security.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRepo;
    private final CustomOAuth2UserService customUserService;
    private final CustomOAuth2SuccessHandler successHandler;

    // ↓ RedisTemplate 대신 AuthorizationRequestRepository 를 주입받도록 변경
    public SecurityConfig(
            AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRepo,
            CustomOAuth2UserService customUserService,
            CustomOAuth2SuccessHandler successHandler
    ) {
        this.authorizationRepo = authorizationRepo;
        this.customUserService = customUserService;
        this.successHandler    = successHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login/**",
                                "/oauth2/**",
                                "/css/**",
                                "/auth/**",

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
                        .authorizationEndpoint(endpoint -> endpoint
                                .baseUri("/auth")
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
                        .logoutUrl("/auth/logout")                      // 클라이언트가 호출할 로그아웃 URL
                        .invalidateHttpSession(true)
                        .deleteCookies("ACCESS_TOKEN", "JSESSIONID")    // 필요하다면 쿠키 이름들
                        .logoutSuccessHandler((req, res, auth) -> {
                            res.setStatus(HttpServletResponse.SC_OK);
                            // JSON 응답이 필요하면 작성하거나, redirect 하려면
                            // res.sendRedirect("http://localhost:5173/login");
                        })
                );

        return http.build();
    }
}