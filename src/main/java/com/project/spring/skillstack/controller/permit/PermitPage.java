package com.project.spring.skillstack.controller.permit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import com.project.spring.skillstack.utility.CookieUtil;
import com.project.spring.skillstack.utility.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/permit/auth")
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
        @RequestParam("password1")String password1,
        @RequestParam("password2")String password2,
        @RequestParam("name") String name,
        @RequestParam("email1") String email1,
        @RequestParam("email2") String email2,
        @RequestParam("phoneNumber2") String phoneNumber2,
        @RequestParam("phoneNumber3") String phoneNumber3,
        @RequestParam("birthDate") String birthDateStr,
        @AuthenticationPrincipal UserDetails userDetails,
        HttpServletResponse response
    ) {
        String email = email1 + "@" + email2;
        String phoneNumber = "010" + phoneNumber2 + phoneNumber3;
        LocalDate birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        if(userDetails != null) return "redirect:" + corsOrigin + "/home";
        if(id.isEmpty() || 
           password1.isEmpty() || 
           password2.isEmpty() || 
           name.isEmpty() || 
           email1.isEmpty() || 
           email2.isEmpty() || 
           name.isEmpty() ||
           phoneNumber2.isEmpty() ||
           phoneNumber3.isEmpty() ||
           birthDate==null
         ) return "redirect:" + corsOrigin + "/auth/signup?error=empty_input";
        if(userRep.findByName(id).isPresent()) return "redirect:" + corsOrigin + "/auth/signup?error=user_exists";
        if(userRep.findByEmail(email).isPresent()) return "redirect:" + corsOrigin + "/auth/signup?error=user_exists";
        if(userRep.findByPhoneNumber(phoneNumber).isPresent()) return "redirect:" + corsOrigin + "/auth/signup?error=user_exists";
        if(!password1.equals(password2)) return "redirect:" + corsOrigin + "/auth/signup?error=password_mismatch";

        List<String> roles = new ArrayList<>();
        roles.add("USER");

        UserEntity user = new UserEntity(null, id, new BCryptPasswordEncoder().encode(password1),
                            name, roles, email, phoneNumber, birthDate, LocalDateTime.now(),  null, null
                            );
                            
        userRep.save(user);
        String token = jwtUtil.generateToken(user.getName());
        cookieUtil.GenerateJWTCookie(token, response);

        return "redirect:" + corsOrigin + "/home";
        
    }

    @PostMapping("/signin")
    public String signin(
        @RequestParam("userId") String userId,
        @RequestParam("password") String password,
        HttpServletResponse response
    ) {
        UserEntity user = userRep.findByName(userId).orElse(null);
        
        if (user == null) {
            return "redirect:" + corsOrigin + "/signin?error=not_found";
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, user.getPass())) {
            return "redirect:" + corsOrigin + "/signin?error=wrong_password";
        }

        String token = jwtUtil.generateToken(user.getName());
        cookieUtil.GenerateJWTCookie(token, response);

        return "redirect:" + corsOrigin + "/home";
    }






    
    /////////////////////////////////////////////// 백엔드 테스트용 ///////////////////////////////////////////////
    // @PostMapping("/signup")
    // @Transactional
    // public String signup(
    //     @RequestParam("id") String userid,
    //     @RequestParam("password1") String password1,
    //     @RequestParam("password2") String password2,
    //     @RequestParam("name") String name,
    //     @RequestParam("email1") String email1,
    //     @RequestParam("email2") String email2,
    //     @RequestParam("phoneNumber2") String phoneNumber2,
    //     @RequestParam("phoneNumber3") String phoneNumber3,
    //     @RequestParam("birthDate") String birthDateStr,
    //     @AuthenticationPrincipal UserDetails userDetails,
    //     HttpServletResponse response
    // ) {
    //     String email = email1 + "@" + email2;
    //     String phoneNumber = "010" + phoneNumber2 + phoneNumber3;
    //     LocalDate birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    //     if(userDetails != null) return "redirect:" + corsOrigin + "/home";
    //     if(userid.isEmpty() || 
    //        password1.isEmpty() || 
    //        password2.isEmpty() || 
    //        name.isEmpty() || 
    //        email1.isEmpty() || 
    //        email2.isEmpty() || 
    //        phoneNumber2.isEmpty() ||
    //        phoneNumber3.isEmpty() ||
    //        birthDate==null
    //      ) return "redirect:/signup?error=empty_input";
    //     if(userRep.findByName(userid).isPresent()) return "redirect:/signup?error=user_id_exists";
    //     if(userRep.findByEmail(email).isPresent()) return "redirect:/signup?error=user_email_exists";
    //     if(userRep.findByPhoneNumber(phoneNumber).isPresent()) return "redirect:/signup?error=user_phone_exists";
    //     if(!password1.equals(password2)) return "redirect:/signup?error=password_mismatch";

    //     List<String> roles = new ArrayList<>();
    //     roles.add("USER");
        
    //     UserEntity user = new UserEntity(null, userid, new BCryptPasswordEncoder().encode(password1),
    //                         name, roles, email, phoneNumber, birthDate, LocalDateTime.now(), null, null
    //                         );

    //     userRep.save(user);
    //     String token = jwtUtil.generateToken(user.getName());
    //     cookieUtil.GenerateJWTCookie(token, response);

    //     return "redirect:/home";
        
    // }


    // @PostMapping("/signin")
    // public String signin(
    //     @RequestParam("userId") String userId,
    //     @RequestParam("password") String password,
    //     HttpServletResponse response
    // ) {
    //     UserEntity user = userRep.findByName(userId).orElse(null);
        
    //     if (user == null) {
    //         return "redirect:/signin?error=not_found";
    //     }

    //     BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    //     if (!encoder.matches(password, user.getPass())) {
    //         return "redirect:/signin?error=wrong_password";
    //     }

    //     String token = jwtUtil.generateToken(user.getName());
    //     cookieUtil.GenerateJWTCookie(token, response);

    //     return "redirect:/home";
    // }


}
