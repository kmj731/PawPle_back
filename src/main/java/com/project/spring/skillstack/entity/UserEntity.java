package com.project.spring.skillstack.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.project.spring.skillstack.dto.UserDto;
import com.project.spring.skillstack.dto.UserDtoWithoutPass;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "UserTable")
@SequenceGenerator(
    allocationSize = 1,
    initialValue = 1,
    name = "UserSeq",
    sequenceName = "UserSeq"
)
public class UserEntity {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "UserSeq"
    )
    private Long id;

    private String name;
    private String pass;

    private String socialName;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    private String email;
    private String phoneNumber;

    private LocalDate birthDate;

    private String imageUrl;
    private String thumbnailUrl;

    private LocalDateTime created;

    @Transient
    private Map<String, Object> attr;

    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PetEntity> pets = new ArrayList<>();

    public UserDto toDto() {
        return new UserDto(id, name, pass, socialName, getRoles().stream().map(String::toString).collect(Collectors.toList()), email, phoneNumber, birthDate, imageUrl, thumbnailUrl, created, attr, pets);
    }

    public UserDtoWithoutPass toDtoWithoutPass(){
        return new UserDtoWithoutPass(
            id, name, socialName, roles.stream().map(String::toString).collect(Collectors.toList()),
            email,
            phoneNumber,
            birthDate,
            created,
            attr
        );

    }


}
