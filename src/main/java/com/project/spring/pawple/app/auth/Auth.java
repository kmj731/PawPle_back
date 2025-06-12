package com.project.spring.pawple.app.auth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import com.project.spring.pawple.app.user.Role;
import com.project.spring.pawple.app.user.UserEntity;
import com.project.spring.pawple.app.user.UserRepository;

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

    @GetMapping("/role")
    public Role role(@AuthenticationPrincipal CustomUserDetails user) {
        return new Role(user.getAuthorities().toArray()[0].toString().replaceFirst("ROLE_", "").toLowerCase());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        UserEntity user = userRep.findByName(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "nickname", user.getSocialName(),
                "username", user.getName()));
    }

}
