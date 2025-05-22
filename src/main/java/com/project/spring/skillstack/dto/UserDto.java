package com.project.spring.skillstack.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.project.spring.skillstack.entity.PetEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

private Long id;

    private String name;
    private String pass;

    private String socialName;

    private List<String> roles;

    private String email;
    private String phoneNumber;

    private LocalDate birthDate;
    
    private LocalDateTime created;

    private Map<String, Object> attr;

    private List<PetEntity> pets;
}
