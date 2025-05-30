package com.project.spring.skillstack.filter;

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

import com.project.spring.skillstack.service.CustomUserDetailService;
import com.project.spring.skillstack.utility.CookieUtil;
import com.project.spring.skillstack.utility.JwtUtil;

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
                String username = jwtUtil.extractUsername(token); // JWT에서 username 추출

                if (StringUtils.hasText(username) &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                    UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

                    if (jwtUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        // ✅ 3. 인증 객체 설정
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        // ❗ 토큰 유효하지 않으면 쿠키 삭제
                        cookieUtil.RemoveJWTCookie(response);
                    }
                }
            } catch (Exception e) {
                System.out.println("[JWT 필터 오류] " + e.getClass().getSimpleName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        filterChain.doFilter(request, response); // 다음 필터로 전달
    }


}
