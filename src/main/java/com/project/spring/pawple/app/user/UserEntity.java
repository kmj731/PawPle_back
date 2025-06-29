package com.project.spring.pawple.app.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.project.spring.pawple.app.pet.PetEntity;
import com.project.spring.pawple.app.pet.PetShowing;
import com.project.spring.pawple.app.pet.PetUserDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "USER_TABLE")
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
    
    @ManyToMany
    @JoinTable(
        name = "user_following",
        joinColumns = @JoinColumn(name = "follower_id"),
        inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    private List<UserEntity> following = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "user_blocked",
        joinColumns = @JoinColumn(name = "blocker_id"),
        inverseJoinColumns = @JoinColumn(name = "blocked_id")
    )
    private List<UserEntity> blockedUsers = new ArrayList<>();

    private LocalDateTime created;

    @Transient
    private Map<String, Object> attr;

    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PetEntity> pets = new ArrayList<>();

    @Column(name= "\"POINT\"")
    private Integer point=0;

    
    public void addPoint(Integer point){
        if(this.point == null){
            this.point = 0;
        }
        this.point += point;
    }


    public UserDto toDto() {
        return new UserDto(
            id,
            name,
            pass,
            socialName,
            roles.stream().map(String::toString).collect(Collectors.toList()),
            email,
            phoneNumber,
            birthDate,
            imageUrl,
            thumbnailUrl,
            following.stream().map(UserEntity::getId).collect(Collectors.toList()),
            blockedUsers.stream().map(UserEntity::getId).collect(Collectors.toList()),
            created,
            attr,
            pets,
            point
        );
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

    public PetUserDto toDTO() {
        List<PetShowing> petShowings = pets.stream()
            .map(PetShowing::new) // PetShowing(PetEntity pet) 생성자 필요
            .collect(Collectors.toList());

        return new PetUserDto(
            id, name, pass, socialName,
            roles, email, phoneNumber,
            birthDate, imageUrl, thumbnailUrl,
            created, attr, petShowings, point
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity user = (UserEntity) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
