package com.project.spring.skillstack.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.spring.skillstack.controller.ManagerController;
import com.project.spring.skillstack.dao.UserRepository;


import com.project.spring.skillstack.dto.UserDtoWithoutPass;
import com.project.spring.skillstack.entity.PetEntity;
import com.project.spring.skillstack.entity.UserEntity;

import jakarta.transaction.Transactional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        
    }

    public void addAdminRole(Long userId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User가 존재하지 않습니다."));
        
        List<String> roles = user.getRoles();

        if(!roles.contains("ADMIN")){
            roles.add("ADMIN");
            user.setRoles(roles);
            userRepository.save(user);
        }
    }

    // 전체 회원 조회
    public List<UserDtoWithoutPass> getAllUsersWithoutPass() {
        List<UserDtoWithoutPass> users = userRepository.findAll().stream()
                            .map(UserEntity::toDtoWithoutPass)
                            .collect(Collectors.toList());
        if (users.isEmpty()){
            throw new UsernameNotFoundException("회원이 존재하지 않습니다.");
            
        }
        return users;
    }

    // 이름으로 회원 검색 (비밀번호 제외)
    public List<UserDtoWithoutPass> searchUserByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name).stream()
            .map(UserEntity::toDtoWithoutPass)
            .collect(Collectors.toList());
    }

    // 이메일로 회원 검색 (비밀번호 제외)
    public List<UserDtoWithoutPass> searchUsersByEmail(String email) {
        return userRepository.findByEmailContainingIgnoreCase(email).stream()
            .map(UserEntity::toDtoWithoutPass)
            .collect(Collectors.toList());
    }

    // 소셜 이름으로 회원 검색 (비밀번호 제외)
    public List<UserDtoWithoutPass> searchUsersBySocialName(String socialName) {
        return userRepository.findBySocialNameContainingIgnoreCase(socialName).stream()
            .map(UserEntity::toDtoWithoutPass)
            .collect(Collectors.toList());
    }

    // 회원 삭제
    public boolean deleteUserById(Long id) {
        // 회원 존재 여부 확인
        Optional<UserEntity> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return false; // 회원이 없으면 false 리턴
        }

        // 회원이 있으면 삭제
        userRepository.deleteById(id);
        return true;
    }


    // 유저 펫 조회
    public List<PetEntity> getPetsByUserId(Long userId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자 없음"));
        return user.getPets();
    }


    // 회원 수 조회
    public long getUserCount(){
        return userRepository.count();
    }
    
    
}
