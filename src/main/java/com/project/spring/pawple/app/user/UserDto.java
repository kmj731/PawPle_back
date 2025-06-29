package com.project.spring.pawple.app.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.project.spring.pawple.app.pet.PetEntity;

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
    
    private String imageUrl;
    private String thumbnailUrl;

    private List<Long> followingIds;
    private List<Long> blockedIds;

    private LocalDateTime created;

    private Map<String, Object> attr;

    private List<PetEntity> pets;
    
    private Integer point;
    
    

    

}
