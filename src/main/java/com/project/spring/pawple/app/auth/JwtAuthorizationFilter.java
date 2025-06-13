package com.project.spring.pawple.app.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    CookieUtil cookieUtil;

    @Autowired
    CustomUserDetailService customUserDetailService;

    @Value("${spring.security.jwt.cookie.name}")
    String jwtAuthCookieName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
    // String uri = request.getRequestURI();
    // // ✅ 구조동물 API는 인증 로직 생략
    // if (uri.startsWith("/api/animals")) {
    //     System.out.println("🟢 인증 우회됨: " + uri);
    //     filterChain.doFilter(request, response);
    //     return;
    // }

        String token = null;

        // ✅ 1. 쿠키가 존재하면 JWT 토큰 추출
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (jwtAuthCookieName.equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // ✅ 2. 토큰이 있고 아직 인증되지 않았다면
if (StringUtils.hasText(token)) {
    try {
        String username = jwtUtil.extractUsername(token);
        if (StringUtils.hasText(username) &&
            SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                cookieUtil.RemoveJWTCookie(response);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰");
                return; // ❗ 요청 중단
            }
        }
    } catch (Exception e) {
        System.out.println("[JWT 필터 오류] " + e.getClass().getSimpleName() + ": " + e.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰 인증 실패: " + e.getMessage());
        return; // ❗ 요청 중단
    }
}


        filterChain.doFilter(request, response); // 다음 필터로 전달
    }


}
