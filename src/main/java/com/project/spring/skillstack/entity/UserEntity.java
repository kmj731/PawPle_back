package com.project.spring.skillstack.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.project.spring.skillstack.dto.UserDto;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @ElementCollection
    private List<String> roles;

    private String email;
    private String gender;
    private String phoneNumber;


    private LocalDate birthDate;
    private LocalDateTime created;

    @Transient
    private Map<String, Object> attr;

    public UserDto toDto() {
        return new UserDto(id, name, pass, socialName, getRoles().stream().map(String::toString).collect(Collectors.toList()), created, attr);
    }
}
