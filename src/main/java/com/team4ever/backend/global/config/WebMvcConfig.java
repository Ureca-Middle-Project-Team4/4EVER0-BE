package com.team4ever.backend.global.config;

import com.team4ever.backend.global.security.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    public WebMvcConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("", c ->
                !c.getPackageName().startsWith("org.springframework.web.servlet.resource"));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 보호할 경로 명시적으로 지정
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        // === 인증 관련 (공개 API) ===
                        "/api/user",
                        "/api/auth/**",
                        "/api/refresh",

                        // === 공개 조회 API (인증 불필요) ===
                        // 요금제 공개 조회
                        "/api/plans",           // 전체 요금제 조회
                        "/api/plans/*",         // 요금제 상세 조회 (GET /api/plans/{id})

                        // 구독 관련 공개 조회
                        "/api/subscriptions/main",
                        "/api/subscriptions/brands",

                        // 팝업 관련 공개 조회
                        "/api/popups",
                        "/api/popups/*",

                        // 쿠폰 공개 조회만 허용
                        "/api/coupons",         // GET /api/coupons 만 허용
                        "/api/coupons/**",
                        // 일반 채팅
                        "/api/chat",

                        // === Swagger 관련 ===
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**",

                        // === 정적 리소스 ===
                        "/images/**",
                        "/static/**",
                        "/favicon.ico",
                        "/error"
                );
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converters.add(stringConverter);
    }
}