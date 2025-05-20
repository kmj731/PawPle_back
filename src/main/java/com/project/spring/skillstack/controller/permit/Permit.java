////////////////////////////////////////////// 백엔드 테스트용 컨트롤러 //////////////////////////////////////////////

package com.project.spring.skillstack.controller.permit;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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







    // 모든 유저 리스트 조회 API (이름만)
    @GetMapping("/test/users")
    public List<String> getAllUsers() {
        return userRep.findAll().stream()
                .map(UserEntity::getName)
                .collect(Collectors.toList());
    }

    // 모든 펫 리스트 조회 API (이름만)
    @GetMapping("/test/pets")
    public List<String> getAllPets() {
        return petRep.findAll().stream()
                .map(PetEntity::getPetName)
                .collect(Collectors.toList());
    }

    // 로그인한 유저의 펫 리스트 조회 API (이름만)
    @GetMapping("/test/userpets")
    public List<String> getMyPets(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            return List.of();
        }

        Optional<UserEntity> optUser = userRep.findByName(userDetails.getUsername());
        if (optUser.isEmpty()) {
            return List.of();
        }

        UserEntity user = optUser.get();

        return user.getPets().stream()
                .map(PetEntity::getPetName)
                .collect(Collectors.toList());
    }


    // 로그인한 유저의 펫 리스트 조회 API (모든 데이터)
    @GetMapping("/test/info")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (userDetails == null || userDetails.getUsername() == null) {
            result.put("value", false);
            result.put("message", "로그인 정보가 없습니다.");
            return result;
        }

        Optional<UserEntity> optUser = userRep.findByName(userDetails.getUsername());
        if (optUser.isEmpty()) {
            result.put("value", false);
            result.put("message", "해당 유저가 존재하지 않습니다.");
            return result;
        }

        UserEntity user = optUser.get();

        List<PetEntity> pets = user.getPets() != null ? user.getPets() : new ArrayList<>();
        List<Map<String, Object>> petList = pets.stream()
            .map(pet -> {
                Map<String, Object> petMap = new LinkedHashMap<>();
                petMap.put("id", pet.getId());
                petMap.put("name", pet.getPetName());
                petMap.put("type", pet.getPetType());
                petMap.put("age", pet.getPetAge());
                petMap.put("gender", pet.getPetGender());
                petMap.put("breed", pet.getPetBreed());
                petMap.put("weight", pet.getWeight());
                petMap.put("registrationDate", pet.getRegistrationDate());
                return petMap;
            })
            .collect(Collectors.toList());

        Map<String, Object> userMap = new LinkedHashMap<>();
        userMap.put("id", user.getId());
        userMap.put("name", user.getName());
        userMap.put("email", user.getEmail());
        userMap.put("phoneNumber", user.getPhoneNumber());
        userMap.put("birthDate", user.getBirthDate());
        userMap.put("created", user.getCreated());
        userMap.put("roles", user.getRoles());

        result.put("value", true);
        result.put("user", userMap);
        result.put("pets", petList);
        result.put("petCount", petList.size());

        return result;
    }









}
