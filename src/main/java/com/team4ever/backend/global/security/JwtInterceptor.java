package com.team4ever.backend.global.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor; // [추가]

import java.util.Collections;

@Component                                               // [추가]
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;     // [추가]

    public JwtInterceptor(JwtTokenProvider jwtTokenProvider) { // [추가]
        this.jwtTokenProvider = jwtTokenProvider;             // [추가]
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String token = null;

        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            throw new JwtException("쿠키가 존재하지 않습니다.");
        }
        boolean found = false;
        for(Cookie cookie : cookies) {
            if("ACCESS_TOKEN".equals(cookie.getName())){
                token = cookie.getValue();
                found = true;
                break;
            }
        }
        if (!found) {
            throw new JwtException("No access token found");
        }

        if (token != null) {
            try {
                if (jwtTokenProvider.validateToken(token)) {
                    String userId = jwtTokenProvider.getUserId(token);
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userId,
                                    null,
                                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                            );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JwtException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 JWT 토큰");
                return false; // 인증 실패 시 요청 중단
            }
        }
        return true;
    }
}
