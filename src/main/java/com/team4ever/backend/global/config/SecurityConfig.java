// src/main/java/com/example/demo/config/SecurityConfig.java
package com.team4ever.backend.global.config;

import com.team4ever.backend.global.security.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRepo;
    private final CustomOAuth2UserService customUserService;
    private final CustomOAuth2SuccessHandler successHandler;
    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;

    // ↓ RedisTemplate 대신 AuthorizationRequestRepository 를 주입받도록 변경
    public SecurityConfig(
            AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRepo,
            CustomOAuth2UserService customUserService,
            CustomOAuth2SuccessHandler successHandler,
            RedisService redisService,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.authorizationRepo = authorizationRepo;
        this.customUserService = customUserService;
        this.successHandler    = successHandler;
        this.redisService      = redisService;
        this.jwtTokenProvider  = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/coupons").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user").permitAll()
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/refresh",
                                "/api/plans",
                                "/api/plans/**",
                                "/api/subscriptions/main",
                                "/api/subscriptions/brands",
                                "/api/popups",
                                "/api/popups/**",
                                "/api/coupons",
                                "/api/coupons/**",
                                "/api/chat",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint ->
                                endpoint.baseUri("/api/auth")
                                        .authorizationRequestRepository(authorizationRepo)
                        )
                        .userInfoEndpoint(u -> u
                                .userService(customUserService)
                        )
                        .successHandler(successHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("ACCESS_TOKEN", "JSESSIONID")
                        .addLogoutHandler((request, response, authentication) -> {
                            if (authentication != null && authentication.getName() != null) {
                                redisService.deleteRefreshToken(authentication.getName());
                            }
                        })
                        .logoutSuccessHandler((req, res, auth) -> {
                            res.setStatus(HttpServletResponse.SC_OK);
                        })
                )
                // 👉 이 한 줄만 추가!
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}