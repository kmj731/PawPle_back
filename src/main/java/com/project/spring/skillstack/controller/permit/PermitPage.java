package com.project.spring.skillstack.controller.permit;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.entity.UserEntity;
// import com.project.spring.skillstack.service.CustomUserDetails;
import com.project.spring.skillstack.utility.CookieUtil;
import com.project.spring.skillstack.utility.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/permit/user")
public class PermitPage {

    @Autowired
    UserRepository userRep;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    CookieUtil cookieUtil;
    @Value("${spring.security.cors.site}")
    String corsOrigin;

    @PostMapping("/signup")
    @Transactional
    public String signup(
        @RequestParam("id")String id,
        @RequestParam("pw")String pw,
        @RequestParam("pwr")String pwr,
        @AuthenticationPrincipal UserDetails userDetails,
        HttpServletResponse response
    ) {
        if(userDetails != null) return "redirect:" + corsOrigin + "/home";
        if(id.isEmpty() || pw.isEmpty() || pwr.isEmpty()) return "redirect:" + corsOrigin + "/auth/signup?error=empty_input";
        if(userRep.findByNameLike(id).isPresent()) return "redirect:" + corsOrigin + "/auth/signup?error=user_exists";
        if(!pw.equals(pwr)) return "redirect:" + corsOrigin + "/auth/signup?error=password_mismatch";

        UserEntity user = new UserEntity(null, id, new BCryptPasswordEncoder().encode(pw), id, List.of("USER"), LocalDateTime.now(), null);
        userRep.save(user);
        String token = jwtUtil.generateToken(user.getName());
        cookieUtil.GenerateJWTCookie(token, response);

        return "redirect:" + corsOrigin + "/home";
        
    }





    
    /////////////////////////////////////////////// 백엔드 테스트용 ///////////////////////////////////////////////
    // @PostMapping("/signup")
    // @Transactional
    // public String signup(@RequestParam("id")String id, @RequestParam("pw")String pw, @RequestParam("pwr")String pwr, @AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) {
    //     if(userDetails != null) return "redirect:/home";
    //     if(id.isEmpty() || pw.isEmpty() || pwr.isEmpty()) return "redirect:/auth/signup?error=empty_input";
    //     if(userRep.findByNameLike(id).isPresent()) return "redirect:/auth/signup?error=user_exists";
    //     if(!pw.equals(pwr)) return "redirect:/auth/signup?error=password_mismatch";

    //     UserEntity user = new UserEntity(null, id, new BCryptPasswordEncoder().encode(pw), id, List.of("USER"), LocalDateTime.now(), null);
    //     userRep.save(user);
    //     // String token = jwtUtil.generateToken(new CustomUserDetails(user.toDto()));
    //     String token = jwtUtil.generateToken(user.getName());
    //     cookieUtil.GenerateJWTCookie(token, response);

    //     return "redirect:/home";
        
    // }
}
