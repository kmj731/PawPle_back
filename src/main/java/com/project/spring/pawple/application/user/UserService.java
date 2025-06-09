package com.project.spring.pawple.application.user;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.spring.pawple.application.comment.CommentRepository;
import com.project.spring.pawple.application.health.HealthCheckRecord;
import com.project.spring.pawple.application.pet.PetEntity;
import com.project.spring.pawple.application.pet.PetRepository;
import com.project.spring.pawple.application.post.PostEntity;
import com.project.spring.pawple.application.post.PostRepository;

import jakarta.persistence.EntityNotFoundException;




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
    


    // 회원 삭제 
    public void deleteUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        
        // 자식 Pet의 healthRecord도 비워야 Pet이 삭제 가능
        for(PetEntity pet : user.getPets()) {
            pet.getHealthRecords().clear();
        }
        
        user.getPets().clear();
        // PetEntity는 cascade + orphanRemoval로 인해 자동 삭제됨
        userRepository.delete(user);
    }


    // roles 변경
    @Transactional
    public UserDto updateRoles(Long userId, List<String> newRoles) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRoles(newRoles); // roles만 수정됨

        // 변경 감지(dirty checking)로 자동 update
        return user.toDto(); // ✅ UserDto 반환
    }

    // 게시글 삭제


    // roles 변경
//     @Transactional
//     public UserDto updateRoles(Long userId, List<String> newRoles) {
//     UserEntity user = userRepository.findById(userId)
//         .orElseThrow(() -> new RuntimeException("User not found"));

//     // 필요한 경우 역할 유효성 검사 추가 가능
//     validateRoles(newRoles);

//     user.setRoles(newRoles);

//     // JPA 변경 감지로 업데이트 처리됨
//     return user.toDto();
// }

//     private void validateRoles(List<String> roles) {
//         List<String> validRoles = List.of("ADMIN", "USER", "VET");
//         for (String role : roles) {
//             if (!validRoles.contains(role)) {
//             throw new IllegalArgumentException("Invalid role: " + role);
//         }
//     }
// }



    // 회원 상세 정보 조회
    public UserSimpleInfoDto getUserSimpleInfoDto(Long UserId){
        UserEntity user = userRepository.findById(UserId)
            .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        
        return new UserSimpleInfoDto(user.getPhoneNumber(), user.getBirthDate(), user.getPoint() != null ? user.getPoint() : 0,user.getAttr());
    }



    public UserEntity getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + id));
    }



    
}
