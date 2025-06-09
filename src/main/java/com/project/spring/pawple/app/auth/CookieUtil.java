package com.project.spring.pawple.app.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Component
public final class CookieUtil {
    
    @Value("${spring.security.jwt.expires}")
    Integer jwtExpire;
    @Value("${spring.security.jwt.cookie.name}")
    String jwtAuthCookieName;
    @Value("${spring.security.cors.same.domain}")
    String corsDomain;
    
    public void GenerateJWTCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(jwtAuthCookieName, token);
        cookie.setPath("/");
        cookie.setMaxAge(jwtExpire);
        cookie.setDomain(corsDomain);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        // cookie.setHttpOnly(true); -> 백엔드 지워야할것
        // cookie.setAttribute("SameSite", "Lax"); -> 백엔드 지워야할것

        // ✅ 로그 추가
        System.out.println("[쿠키 생성됨]");
        System.out.println("  이름: " + jwtAuthCookieName);
        System.out.println("  도메인: " + corsDomain);
        System.out.println("  MaxAge: " + jwtExpire);
        System.out.println("  값: " + token);
    }

    public void RemoveJWTCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(jwtAuthCookieName, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setDomain(corsDomain);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }


}
