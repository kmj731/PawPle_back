package com.project.spring.skillstack.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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



    
    // 회원정보 삭제
    @PostMapping("/delete")
    @Transactional
    public String deleteUser(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam("id") String id,
        @RequestParam("password") String password,
        HttpServletResponse response
    ) {
        if (userDetails == null) {
            return "redirect:" + corsOrigin + "/auth/signin?error=not_logged_in";
        }

        String loggedInUsername = userDetails.getUsername();

        if (!loggedInUsername.equals(id)) {
            return "redirect:" + corsOrigin + "/auth/profile?error=unauthorized";
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            return "redirect:" + corsOrigin + "/auth/profile?error=wrong_password";
        }

        userRep.deleteByName(id);
        cookieUtil.RemoveJWTCookie(response);

        return "redirect:" + corsOrigin + "/home?message=delete_success";
    }
    


    // 회원정보 수정
    @PostMapping("/update")
    @Transactional
    public String updateUser(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam("id") String id,
        @RequestParam("currentPassword") String currentPassword,
        @RequestParam("newPassword") String newPassword,
        @RequestParam("socialName") String socialName,
        HttpServletResponse response
    ) {
        if (userDetails == null) {
            return "redirect:" + corsOrigin + "/auth/signin?error=not_logged_in";
        }

        String loggedInUsername = userDetails.getUsername();

        if (!loggedInUsername.equals(id)) {
            return "redirect:" + corsOrigin + "/auth/profile?error=unauthorized";
        }

        if (!passwordEncoder.matches(currentPassword, userDetails.getPassword())) {
            return "redirect:" + corsOrigin + "/auth/profile?error=wrong_password";
        }

        UserEntity user = userRep.findByNameLike(id).orElse(null);
        if (user == null) {
            return "redirect:" + corsOrigin + "/auth/profile?error=user_not_found";
        }

        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPass(passwordEncoder.encode(newPassword));
        }
        if (socialName != null && !socialName.isEmpty()) {
            user.setSocialName(socialName);
        }

        userRep.save(user);

        return "redirect:" + corsOrigin + "/home?message=update_success";
    }




    @GetMapping("/role")
    public Role role(@AuthenticationPrincipal CustomUserDetails user) {
        return new Role(user.getAuthorities().toArray()[0].toString().replaceFirst("ROLE_", "").toLowerCase());
    }
}
