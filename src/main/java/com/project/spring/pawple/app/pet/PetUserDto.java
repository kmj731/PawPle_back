package com.project.spring.pawple.app.pet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetUserDto {

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

    private LocalDateTime created;

    private Map<String, Object> attr;

    private List<PetShowing> pets;
    private Integer point;
    
    
    

    

}

