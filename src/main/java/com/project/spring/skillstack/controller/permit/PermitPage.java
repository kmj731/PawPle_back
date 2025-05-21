package com.project.spring.skillstack.controller.permit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.dto.UserDto;
import com.project.spring.skillstack.entity.UserEntity;
import com.project.spring.skillstack.utility.CookieUtil;
import com.project.spring.skillstack.utility.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;

@RestController
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
    @ResponseBody
    @Transactional
    public ResponseEntity<?> signup(@RequestBody UserDto dto, HttpServletResponse response) {

        // // ✅ 디버깅 로그 추가
        // System.out.println("[회원가입 요청 DTO]");
        // System.out.println("name: " + dto.getName());
        // System.out.println("pass: " + dto.getPass());
        // System.out.println("email: " + dto.getEmail());
        // System.out.println("phone: " + dto.getPhoneNumber());
        // System.out.println("birth: " + dto.getBirthDate());

        if (dto.getName() == null || dto.getPass() == null || dto.getEmail() == null ||
            dto.getPhoneNumber() == null || dto.getBirthDate() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "empty_input"));
        }

        if (userRep.findByName(dto.getName()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "user_exists"));
        }

        if (userRep.findByEmail(dto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "user_exists"));
        }

        if (userRep.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "user_exists"));
        }

        List<String> roles = new ArrayList<>();
        roles.add("USER");

        UserEntity user = new UserEntity(
            null,
            dto.getName(),
            new BCryptPasswordEncoder().encode(dto.getPass()),
            dto.getSocialName() != null ? dto.getSocialName() : dto.getName(),
            roles,
            dto.getEmail(),
            dto.getPhoneNumber(),
            dto.getBirthDate(),
            LocalDateTime.now(),
            null,
            null
        );

        userRep.save(user);

        String token = jwtUtil.generateToken(user.getName());
        cookieUtil.GenerateJWTCookie(token, response);

        return ResponseEntity.ok(Map.of("message", "success"));
    }



    // @PostMapping("/signup")
    // @Transactional
    // public String signup(
    //     @RequestParam("id")String id,
    //     @RequestParam("password1")String password1,
    //     @RequestParam("password2")String password2,
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
    //     if(id.isEmpty() || 
    //        password1.isEmpty() || 
    //        password2.isEmpty() || 
    //        name.isEmpty() || 
    //        email1.isEmpty() || 
    //        email2.isEmpty() || 
    //        name.isEmpty() ||
    //        phoneNumber2.isEmpty() ||
    //        phoneNumber3.isEmpty() ||
    //        birthDate==null
    //      ) return "redirect:" + corsOrigin + "/auth/signup?error=empty_input";
    //     if(userRep.findByName(id).isPresent()) return "redirect:" + corsOrigin + "/auth/signup?error=user_exists";
    //     if(userRep.findByEmail(email).isPresent()) return "redirect:" + corsOrigin + "/auth/signup?error=user_exists";
    //     if(userRep.findByPhoneNumber(phoneNumber).isPresent()) return "redirect:" + corsOrigin + "/auth/signup?error=user_exists";
    //     if(!password1.equals(password2)) return "redirect:" + corsOrigin + "/auth/signup?error=password_mismatch";

    //     List<String> roles = new ArrayList<>();
    //     roles.add("USER");

    //     UserEntity user = new UserEntity(null, id, new BCryptPasswordEncoder().encode(password1),
    //                         name, roles, email, phoneNumber, birthDate, LocalDateTime.now(),  null, null
    //                         );
                            
    //     userRep.save(user);
    //     String token = jwtUtil.generateToken(user.getName());
    //     cookieUtil.GenerateJWTCookie(token, response);

    //     return "redirect:" + corsOrigin + "/home";
        
    // }

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


    
    @PostMapping("/findid")
    @ResponseBody
    public String findUserId(
        @RequestParam("email1") String email1,
        @RequestParam("email2") String email2,
        @RequestParam("phoneNumber2") String phoneNumber2,
        @RequestParam("phoneNumber3") String phoneNumber3
    ) {
        String email = email1 + "@" + email2;
        String phoneNumber = "010" + phoneNumber2 + phoneNumber3;

        Optional<UserEntity> optUser = userRep.findByEmail(email)
            .filter(user -> phoneNumber.equals(user.getPhoneNumber()));

        return optUser.map(UserEntity::getName)
                    .orElse("일치하는 회원 정보가 없습니다.");
    }

    @PostMapping("/findpw")
    public String findpw(
        @RequestParam("userId") String userId,
        @RequestParam("email1") String email1,
        @RequestParam("email2") String email2,
        @RequestParam("phone2") String phone2,
        @RequestParam("phone3") String phone3,
        Model model
    ) {
        String email = email1 + "@" + email2;
        String phone = "010" + phone2 + phone3;

        Optional<UserEntity> opt = userRep.findByName(userId)
            .filter(u -> email.equals(u.getEmail()) && phone.equals(u.getPhoneNumber()));

        if (opt.isPresent()) {
            model.addAttribute("userId", userId);
            return "redirect:" + corsOrigin + "/resetpw"; 
        } else {
            model.addAttribute("error", "일치하는 회원 정보가 없습니다.");
            return "redirect:" + corsOrigin + "/findpw"; 
        }
    }

    @PostMapping("/resetpw")
    @Transactional
    public String resetpw(
        @RequestParam("userId") String userId,
        @RequestParam("newPassword1") String pw1,
        @RequestParam("newPassword2") String pw2,
        Model model
    ) {
        if (!pw1.equals(pw2)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("userId", userId);
            return "redirect:" + corsOrigin + "/resetpw"; 
        }

        Optional<UserEntity> opt = userRep.findByName(userId);
        if (opt.isPresent()) {
            UserEntity user = opt.get();
            user.setPass(new BCryptPasswordEncoder().encode(pw1));
            userRep.save(user);
            return "redirect:" + corsOrigin + "/signin?reset=success";
        } else {
            return "redirect:" + corsOrigin + "/findpw?error=유저를 찾을 수 없습니다.";
        }
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

    // @PostMapping("/findid")
    // @ResponseBody
    // public String findUserId(
    //     @RequestParam("email1") String email1,
    //     @RequestParam("email2") String email2,
    //     @RequestParam("phoneNumber2") String phoneNumber2,
    //     @RequestParam("phoneNumber3") String phoneNumber3
    // ) {
    //     String email = email1 + "@" + email2;
    //     String phoneNumber = "010" + phoneNumber2 + phoneNumber3;

    //     Optional<UserEntity> optUser = userRep.findByEmail(email)
    //         .filter(user -> phoneNumber.equals(user.getPhoneNumber()));

    //     return optUser.map(UserEntity::getName)
    //                 .orElse("일치하는 회원 정보가 없습니다.");
    // }

    // @PostMapping("/findpw")
    // public String findpw(
    //     @RequestParam("userId") String userId,
    //     @RequestParam("email1") String email1,
    //     @RequestParam("email2") String email2,
    //     @RequestParam("phone2") String phone2,
    //     @RequestParam("phone3") String phone3,
    //     Model model
    // ) {
    //     String email = email1 + "@" + email2;
    //     String phone = "010" + phone2 + phone3;

    //     Optional<UserEntity> opt = userRep.findByName(userId)
    //         .filter(u -> email.equals(u.getEmail()) && phone.equals(u.getPhoneNumber()));

    //     if (opt.isPresent()) {
    //         model.addAttribute("userId", userId);
    //         return "resetpw"; 
    //     } else {
    //         model.addAttribute("error", "일치하는 회원 정보가 없습니다.");
    //         return "findpw"; 
    //     }
    // }

    // @PostMapping("/resetpw")
    // @Transactional
    // public String resetpw(
    //     @RequestParam("userId") String userId,
    //     @RequestParam("newPassword1") String pw1,
    //     @RequestParam("newPassword2") String pw2,
    //     Model model
    // ) {
    //     if (!pw1.equals(pw2)) {
    //         model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
    //         model.addAttribute("userId", userId);
    //         return "resetpw";
    //     }

    //     Optional<UserEntity> opt = userRep.findByName(userId);
    //     if (opt.isPresent()) {
    //         UserEntity user = opt.get();
    //         user.setPass(new BCryptPasswordEncoder().encode(pw1));
    //         userRep.save(user);
    //         return "redirect:/signin?reset=success";
    //     } else {
    //         return "redirect:/findpw?error=유저를 찾을 수 없습니다.";
    //     }
    // }

}
