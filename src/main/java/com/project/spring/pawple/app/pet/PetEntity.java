package com.project.spring.pawple.app.pet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.spring.pawple.app.health.HealthCheckRecord;
import com.project.spring.pawple.app.post.PostEntity;
import com.project.spring.pawple.app.user.UserEntity;

@Entity
@Table(name = "PET_TABLE")
@NoArgsConstructor
public class PetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String petType;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private String petName;

    @Column(nullable = false)
    private Integer petAge;

    @Column(nullable = false)
    private String petGender;

    @Column(nullable = false)
    private String petBreed;

    @Column(nullable = false)
    private LocalDate registrationDate;

    @Column
    private String imageUrl;

    @Column
    private String thumbnailUrl;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "owner_id", foreignKey = @ForeignKey(name = "FK_pet_owner"))
    private UserEntity owner;

    // @JsonIgnore
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HealthCheckRecord> healthRecords = new ArrayList<>();

    // @OneToMany(mappedBy = "pet")
    // private List<PostEntity> posts;

    public PetEntity(String petType, double weight, String petName, int petAge, String petGender, String petBreed, LocalDate registrationDate, UserEntity owner) {
        this.petType = petType;
        this.weight = weight;
        this.petName = petName;
        this.petAge = petAge;
        this.petGender = petGender;
        this.petBreed = petBreed;
        this.registrationDate = registrationDate;
        this.owner = owner;
    }

    public String getPetName() { return petName; }
    public Long getId() { return id; }
    public String getPetType() { return petType; }
    public Double getWeight() { return weight; }
    public Integer getPetAge() { return petAge; }
    public String getPetGender() { return petGender; }
    public String getPetBreed() { return petBreed; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public UserEntity getOwner() { return owner; }
    public List<HealthCheckRecord> getHealthRecords() { return healthRecords; }
    public String getImageUrl() { return imageUrl; }
    public String getThumbnailUrl() { return thumbnailUrl; }

    public void setPetType(String petType) { this.petType = petType; }
    public void setWeight(Double weight) { this.weight = weight; }
    public void setPetName(String petName) { this.petName = petName; }
    public void setPetAge(Integer petAge) { this.petAge = petAge; }
    public void setPetGender(String petGender) { this.petGender = petGender; }
    public void setPetBreed(String petBreed) { this.petBreed = petBreed; }
    public void setHealthRecords(List<HealthCheckRecord> healthRecords) { this.healthRecords = healthRecords; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
}
