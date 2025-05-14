package com.project.spring.skillstack.utility;

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
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(jwtExpire);
        cookie.setDomain(corsDomain);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
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
