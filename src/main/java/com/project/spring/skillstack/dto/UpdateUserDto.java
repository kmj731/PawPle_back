package com.project.spring.skillstack.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {
    
    private String name;
    private String socialName;
    private String email;
    private List<String> roles;
    private String phoneNumber;
    private LocalDate birthDate;
}
