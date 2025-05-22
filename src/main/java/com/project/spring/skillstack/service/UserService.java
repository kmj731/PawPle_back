package com.project.spring.skillstack.service;

import java.util.List;

import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.dto.UpdateUserDto;

import com.project.spring.skillstack.dto.UserDtoWithoutPass;
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
    @Transactional
    public void deleteUser(Long userId){
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            userRepository.delete(user);
        }



    
    
}
