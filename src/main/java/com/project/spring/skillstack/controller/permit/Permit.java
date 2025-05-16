package com.project.spring.skillstack.controller.permit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.entity.UserEntity;

import jakarta.transaction.Transactional;



@RestController
@RequestMapping("/permit")
public class Permit {

    @Autowired
    UserRepository userRep;

    @GetMapping("/test")
    public List<String> getMethodName() {
        return List.of("Hello", "Bye");
    }






    // 회원가입 테스트 API
    @GetMapping("/test/signup")
    public Map<String, String> testSignin(
            @RequestParam("id") String id,
            @RequestParam("pw") String pw) {

        if (id.isEmpty() || pw.isEmpty()) {
            return Map.of("message", "ID와 비밀번호를 모두 입력해야 합니다.");
        }

        if (userRep.findByNameLike(id).isPresent()) {
            return Map.of("message", "이미 존재하는 사용자입니다.");
        }

        UserEntity user = new UserEntity(null, id, new BCryptPasswordEncoder().encode(pw), id, List.of("USER"), LocalDateTime.now(), null);
        userRep.save(user);
        
        return Map.of("message", "회원가입 성공", "id", id);
    }

    // 로그인 테스트 API
    @GetMapping("/test/signin")
    public Map<String, String> testLogin(
            @RequestParam("id") String id,
            @RequestParam("pw") String pw) {

        if (id.isEmpty() || pw.isEmpty()) {
            return Map.of("message", "ID와 비밀번호를 모두 입력해야 합니다.");
        }

        Optional<UserEntity> optionalUser = userRep.findByNameLike(id);

        if (optionalUser.isEmpty()) {
            return Map.of("message", "사용자가 존재하지 않습니다.");
        }

        UserEntity user = optionalUser.get();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(pw, user.getPass())) {
            return Map.of("message", "비밀번호가 일치하지 않습니다.");
        }

        return Map.of("message", "로그인 성공", "id", id);
    }

    

    // 회원탈퇴 테스트 API (GET 방식도 허용)
    @GetMapping("/test/delete")
    @Transactional
    public String testDeleteUserGet(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam("id") String id
    ) {
        // if (userDetails == null) {
        //     return "";
        // }

        String loggedInUsername = userDetails.getUsername();

        if (!loggedInUsername.equals(id)) {
            return "Error: Unauthorized to delete this user";
        }

        // 삭제 수행
        userRep.deleteByName(id);
        return "Success: User deleted (GET method)";
    }

    // 모든 사용자 리스트 조회 API
    @GetMapping("/test/users")
    public List<String> getAllUsers() {
        return userRep.findAll().stream()
                .map(UserEntity::getName)
                .collect(Collectors.toList());
    }
}
