package com.project.spring.skillstack.dto;

import java.time.LocalDate;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSimpleInfoDto {
    
    private String phoneNumber;
    private LocalDate birthDate;
    private Map<String, Object> attr;
}
