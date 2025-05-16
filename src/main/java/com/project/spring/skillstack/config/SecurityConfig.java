package com.project.spring.skillstack.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.project.spring.skillstack.filter.JwtAuthorizationFilter;
import com.project.spring.skillstack.service.CustomUserDetailService;
import com.project.spring.skillstack.utility.CookieUtil;
import com.project.spring.skillstack.utility.JwtUtil;

@Configuration
public class SecurityConfig {

    @Autowired
    CustomUserDetailService customUserDetailService;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    CookieUtil cookieUtil;
    @Autowired
    JwtAuthorizationFilter jwtAuthorizationFilter;
    
    @Value("${spring.security.jwt.expires}")
    Integer jwtExpire;
    @Value("${spring.security.cors.site}")
    String corsOrigin;
    @Value("${spring.security.cors.same.domain}")
    String corsDomain;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin(corsOrigin);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf->csrf
                .disable()
            )
            .authorizeHttpRequests(auth->auth
                .requestMatchers("/public/**", "/permit/**", "/docs", "/swagger-ui/**", "/v3/**", "/favicon.ico").permitAll()
                .requestMatchers("/auth/signup", "/auth/login", "/auth/signin", "/oauth2/**").permitAll()
                .requestMatchers("/user/delete").authenticated() 
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form->form
                .loginPage(corsOrigin + "/auth/signin") // 프론트에서 로그인을 만들경로
                .loginProcessingUrl("/permit/signin") // 로그인 프로세싱 경로 수정 필요 X
                .failureUrl(corsOrigin + "/auth/signin") // 실패시 이동 경로
                .usernameParameter("id") // 프론트에서 input 태그에 적을 name
                .passwordParameter("pw") // 프론트에서 input 태그에 적을 name
                .successHandler((request, response, authentication)->{
                    String token = jwtUtil.generateToken((UserDetails)authentication.getPrincipal());
                    cookieUtil.GenerateJWTCookie(token, response);
                    response.sendRedirect(corsOrigin + "/home"); // 로그인 성공시 이동 경로
                })
                .permitAll()
            )
            .logout(logout->logout
                .logoutUrl("/logout") // 백엔드 로그아웃 경로
                .logoutSuccessHandler((request, response, authentication)->{
                    cookieUtil.RemoveJWTCookie(response);
                    response.sendRedirect(corsOrigin + "/home"); // 로그아웃 성공시 이동 경로
                })
                .permitAll()
            )
            .oauth2Login(oauth -> oauth
                .loginPage(corsOrigin + "/auth/oauth2login")
                .defaultSuccessUrl(corsOrigin + "/home")
                .failureUrl(corsOrigin + "/auth/oauth2login")
                .userInfoEndpoint(userInfo -> userInfo.userService(customUserDetailService))
                .successHandler((request, response, authentication) -> {
                    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                    String token = jwtUtil.generateToken((UserDetails) customUserDetailService.loadUserByUsername(oAuth2User.getName()));
                    cookieUtil.GenerateJWTCookie(token, response);
                    response.sendRedirect(corsOrigin + "/home");
                })
            )
            .exceptionHandling(error->error
                .authenticationEntryPoint((request, response, authException)->{
                    response.getWriter().write("{\"message\":\"Authentication Error\",\"type\":\"Failed Authenticate\"}");
                })
            )
            .sessionManagement(session->session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
            .userDetailsService(customUserDetailService);
        return http.getOrBuild();
    }
}
