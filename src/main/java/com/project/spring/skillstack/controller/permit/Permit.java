////////////////////////////////////////////// 백엔드 테스트용 컨트롤러 //////////////////////////////////////////////

package com.project.spring.skillstack.controller.permit;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.entity.UserEntity;




@RestController
@RequestMapping("/permit")
public class Permit {

    @Autowired
    UserRepository userRep;

    @GetMapping("/test")
    public List<String> getMethodName() {
        return List.of("Hello", "Bye");
    }







    // 모든 사용자 리스트 조회 API
    @GetMapping("/test/users")
    public List<String> getAllUsers() {
        return userRep.findAll().stream()
                .map(UserEntity::getName)
                .collect(Collectors.toList());
    }
}
