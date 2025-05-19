package com.project.spring.skillstack.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

        // 쿠키에서 토큰 가져오기
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (jwtAuthCookieName.equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        try {
            // 토큰이 있고 인증이 안 되어 있으면 처리
            if (StringUtils.hasText(token)) {
                String username = jwtUtil.extractUsername(token);

                if (StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

                    if (jwtUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        // 유효하지 않은 토큰이면 쿠키 삭제
                        cookieUtil.RemoveJWTCookie(response);
                    }
                }
            }
        } catch (Exception e) {
            // 예외가 발생해도 무시하고 다음 필터로 진행
            // (예: 잘못된 토큰, UserDetails 로딩 실패 등)
        }

        // 무조건 다음 필터로 넘기기
        filterChain.doFilter(request, response);
    }
    // @Override
    // protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    //         throws ServletException, IOException {
        
    //     String authorizationHeader = "";
    //     if(request.getCookies() != null) {
    //         for(Cookie cookie : request.getCookies()){
    //             if(cookie.getName().equals(jwtAuthCookieName))
    //                 authorizationHeader = cookie.getValue();
    //         }
    //     }
    //     String username = "";

    //     if(StringUtils.hasText(authorizationHeader)) {
    //         username = jwtUtil.extractUsername(authorizationHeader);
    //     }
    //     if(StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
    //         UserDetails userDetails = this.customUserDetailService.loadUserByUsername(username);
    //         if(jwtUtil.validateToken(authorizationHeader, userDetails)) {
    //             UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    //             SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    //         } else cookieUtil.RemoveJWTCookie(response);
    //     }
    //     filterChain.doFilter(request, response);
    // }
}
