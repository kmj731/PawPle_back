////////////////////////////////////////////// 백엔드 테스트용 컨트롤러 //////////////////////////////////////////////

package com.project.spring.skillstack.controller.permit;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.skillstack.dao.PetRepository;
import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.entity.PetEntity;
import com.project.spring.skillstack.entity.UserEntity;




@RestController
@RequestMapping("/permit")
public class Permit {

    @Autowired
    UserRepository userRep;
    @Autowired
    PetRepository petRep;

    @GetMapping("/test")
    public List<String> getMethodName() {
        return List.of("Hello", "Bye");
    }







    // 모든 사용자 리스트 조회 API
    @GetMapping("/test/users")
    public List<String> getAllUsers() {
        // 이름만 조회
        return userRep.findAll().stream()
                .map(UserEntity::getName)
                .collect(Collectors.toList());
        // return userRep.findAll();
    }

    // 모든 펫 리스트 조회 API
    @GetMapping("/test/pets")
    public List<String> getAllPets() {
        return petRep.findAll().stream()
                .map(PetEntity::getPetName)
                .collect(Collectors.toList());
    }

    // 사용자 리스트 + 펫 리스트 조회 API
    @GetMapping("/test/userspets")
    public List<String> getAllUsersWithPets() {
        return userRep.findAll().stream()
                .map(user -> "User: " + user.getName() + ", Pets: " +
                        petRep.findByOwner(user).stream()
                              .map(PetEntity::getPetName)
                              .collect(Collectors.joining(", ")))
                .collect(Collectors.toList());
    }



}
