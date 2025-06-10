package com.team4ever.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 2) 정적 리소스(Resource) 핸들러 설정
     *    예시: /images/** 요청은 classpath:/static/images/ 폴더에서 찾기
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");

        // (필요하다면 다른 리소스 경로도 여기에 추가)
        // registry.addResourceHandler("/css/**")
        //         .addResourceLocations("classpath:/static/css/");
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 모든 /auth/** 는 컨트롤러/시큐리티로 넘기도록
        configurer.addPathPrefix("", c ->
                !c.getPackageName().startsWith("org.springframework.web.servlet.resource"));
    }
    /**
     * 3) 인터셉터(Interceptor) 등록
     *    예시: MyInterceptor 를 /api/** 경로에 걸고 싶을 때
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // registry.addInterceptor(new MyInterceptor())
        //         .addPathPatterns("/api/**")
        //         .excludePathPatterns("/api/auth/**");
    }

    // 여기 아래에 addViewControllers, configureMessageConverters 등
    // WebMvcConfigurer 에 다른 메서드를 추가해도 됩니다.
}
