package com.project.spring.pawple.application.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    public Optional<UserEntity> findByName(String name);
    public Optional<UserEntity> findByEmail(String email);
    public Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    public void deleteByName(String name);

    boolean existsByName(String name);

    List<UserEntity> findAll();
    
    List<UserEntity> findByNameContainingIgnoreCase(String name); // 이름을 포함한 사용자 검색(대소문자 구분 x)
    List<UserEntity> findByEmailContainingIgnoreCase(String email); // 이메일을 포함한 사용자 검색(대소문자 구분 X)
    List<UserEntity> findBySocialNameContainingIgnoreCase(String socialName);// 소셜이름을 포함한 사용자 검색 (대소문자 구분 x)
    Optional<UserEntity> findByNameAndEmailAndPhoneNumber(String name, String email, String phoneNumber);


    
    
}

