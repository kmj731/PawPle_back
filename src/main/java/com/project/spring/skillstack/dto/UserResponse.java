package com.project.spring.skillstack.dto;

import java.util.List;

import com.project.spring.skillstack.entity.UserEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    
    private Long id;
    private String name;
    private String email;
    private List<String> roles;

    public UserResponse(UserEntity user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.roles = user.getRoles();
    }

    // getters, setters 생략
}

