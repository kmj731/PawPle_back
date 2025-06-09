package com.project.spring.pawple.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

import com.project.spring.pawple.application.auth.CookieUtil;
import com.project.spring.pawple.application.auth.CustomUserDetailService;
import com.project.spring.pawple.application.auth.JwtAuthorizationFilter;
import com.project.spring.pawple.application.auth.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;

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
        config.addAllowedOriginPattern(corsOrigin);
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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf
                        .disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/comments/**").permitAll() 
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll() // GET 요청은 인증없이 허용
                        .requestMatchers(HttpMethod.PUT, "/vaccine/**").authenticated()
                        .requestMatchers("/api/**").authenticated() // 나머지 API는 인증 필요
                        .requestMatchers("/docs", "/swagger-ui/**", "/v3/**", "/vaccine/**", "/favicon.ico").permitAll()
                        .requestMatchers("/posts/**", "/public/**", "/permit/**", "/animal/**").permitAll()
                        .requestMatchers("/oauth2/**", "/logout").permitAll()
                        .requestMatchers("/auth/**", "/user/**", "/pet/**", "/consult/**").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/health/**", "/", "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated())

                .formLogin(form -> form
                        .loginPage(corsOrigin + "/login")
                        .loginProcessingUrl("/permit/signin")
                        .failureUrl(corsOrigin + "/login?error=true")
                        .usernameParameter("userId")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            String token = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());
                            cookieUtil.GenerateJWTCookie(token, response);
                            response.sendRedirect(corsOrigin + "/");
                        })
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            cookieUtil.RemoveJWTCookie(response);
                            response.setStatus(HttpServletResponse.SC_OK);
                            // response.sendRedirect(corsOrigin + "/home");
                        })
                        .permitAll())
                .oauth2Login(oauth -> oauth
                        .loginPage(corsOrigin + "/auth/signin")
                        .defaultSuccessUrl(corsOrigin + "/home")
                        .failureUrl(corsOrigin + "/auth/signin")
                        .userInfoEndpoint(userInfo -> userInfo.userService(customUserDetailService))
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                            String token = jwtUtil.generateToken(
                                    (UserDetails) customUserDetailService.loadUserByUsername(oAuth2User.getName()));
                            cookieUtil.GenerateJWTCookie(token, response);
                            response.sendRedirect(corsOrigin + "/home");
                        }))
                .exceptionHandling(error -> error
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter()
                                    .write("{\"message\":\"Authentication Error\",\"type\":\"Failed Authenticate\"}");
                        }))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(customUserDetailService);
        return http.getOrBuild();
    }

}