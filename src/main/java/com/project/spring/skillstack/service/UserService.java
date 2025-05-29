package com.project.spring.skillstack.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.spring.skillstack.dao.CommentRepository;
import com.project.spring.skillstack.dao.PetRepository;
import com.project.spring.skillstack.dao.PostRepository;
import com.project.spring.skillstack.dao.UserRepository;


import com.project.spring.skillstack.dto.UserDtoWithoutPass;
import com.project.spring.skillstack.entity.HealthCheckRecord;
import com.project.spring.skillstack.entity.PetEntity;
import com.project.spring.skillstack.entity.UserEntity;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;



@Service
public class UserService {
    
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PetRepository petRepository;
    
    
    public UserService(CommentRepository commentRepository ,UserRepository userRepository,PostRepository postRepository, PetRepository petRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.petRepository = petRepository;
        
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


    // 게시글 삭제
    @Transactional
    public boolean deleteUserById(Long userId) {
    if (!userRepository.existsById(userId)) {
        return false;
    }

    // 1. 댓글 삭제 (userId가 작성자)
    commentRepository.deleteByUser_Id(userId);

    // 2. 게시글 삭제 (userId가 작성자)
    postRepository.deleteByUser_Id(userId);

    // 3. 펫 정보 삭제 (userId가 owner)
    petRepository.deleteByOwnerId(userId);

    // 4. 유저 역할 관계 삭제 (필요시)
    // userRoleRepository.deleteByUserId(userId);

    // 5. 최종 유저 삭제
    userRepository.deleteById(userId);

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
    

    public void deleteUserByID(Long userId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 1. PetEntity의 healthRecords 자식 삭제
        for (PetEntity pet : user.getPets()) {
            pet.getHealthRecords().clear(); // HealthCheckRecord 삭제 (orphanRemoval)
        }

        // 2. User의 pets 컬렉션에서 PetEntity 제거 (orphanRemoval에 의해 pet 삭제)
        user.getPets().clear();

        // 3. User 삭제
        userRepository.delete(user);
    }



    
}
