package com.project.spring.skillstack.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.skillstack.classes.Role;
import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.entity.UserEntity;
import com.project.spring.skillstack.service.CustomUserDetails;
import com.project.spring.skillstack.utility.CookieUtil;
import com.project.spring.skillstack.utility.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/auth")
public class Auth {
    
    
    @Autowired
    UserRepository userRep;
    @Autowired
    CookieUtil cookieUtil;
    @Value("${spring.security.cors.site}")
    String corsOrigin;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtUtil jwtUtil;


    // 회원삭제
    @PostMapping("/delete")
    @Transactional
    public String deleteUser(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam("pw") String pw,
        HttpServletResponse response
    ) {
        if (userDetails == null) {
            return "redirect:" + corsOrigin + "/auth/signin?error=not_logged_in";
        }

        String loggedInUsername = userDetails.getUsername();

        if (!passwordEncoder.matches(pw, userDetails.getPassword())) {
            return "redirect:" + corsOrigin + "/auth/profile?error=wrong_password";
        }

        try {
            userRep.deleteByName(loggedInUsername);
            cookieUtil.RemoveJWTCookie(response);
            SecurityContextHolder.clearContext();
            return "redirect:" + corsOrigin + "/home?message=delete_success";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:" + corsOrigin + "/auth/profile?error=delete_failed";
        }
    }
    


    @PostMapping("/update")
    @Transactional
    public String updateUser(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam("password1") String password1,
        @RequestParam("password2") String password2,
        @RequestParam("name") String name,
        HttpServletResponse response
    ) {
        if (userDetails == null) {
            return "redirect:" + corsOrigin + "/auth/signin?error=not_logged_in";
        }

        String loggedInUsername = userDetails.getUsername();

        if (!passwordEncoder.matches(password1, userDetails.getPassword())) {
            return "redirect:" + corsOrigin + "/auth/profile?error=wrong_password";
        }
        
        UserEntity user = userRep.findByName(loggedInUsername).orElse(null);
        if (user == null) {
            return "redirect:" + corsOrigin + "/auth/signin?error=user_not_found";
        }

        if (password2 != null && !password2.isEmpty()) {
            user.setPass(passwordEncoder.encode(password2));
            
            String newToken = jwtUtil.generateToken(user.getName());
            cookieUtil.GenerateJWTCookie(newToken, response);
        }
        if (name != null && !name.isEmpty()) {
            user.setSocialName(name);
        }

        return "redirect:" + corsOrigin + "/auth/profile?message=update_success";
    }






    @GetMapping("/role")
    public Role role(@AuthenticationPrincipal CustomUserDetails user) {
        return new Role(user.getAuthorities().toArray()[0].toString().replaceFirst("ROLE_", "").toLowerCase());
    }
}
