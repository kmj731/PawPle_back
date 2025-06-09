package com.project.spring.pawple.application.user;

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
    private int point;
    private Map<String, Object> attr;
}
