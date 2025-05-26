package com.project.spring.skillstack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;

@NoArgsConstructor
@Entity
@Table(name = "PetTable")
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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

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

    public String getPetName() {
        return petName;
    }
    public Long getId() { return id; }
    public String getPetType() { return petType; }
    public Double getWeight() { return weight; }
    public Integer getPetAge() { return petAge; }
    public String getPetGender() { return petGender; }
    public String getPetBreed() { return petBreed; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public UserEntity getOwner() { return owner; }
    
    public void setPetType(String petType) { this.petType = petType; }
    public void setWeight(Double weight) { this.weight = weight; }
    public void setPetName(String petName) { this.petName = petName; }
    public void setPetAge(Integer petAge) { this.petAge = petAge; }
    public void setPetGender(String petGender) { this.petGender = petGender; }
    public void setPetBreed(String petBreed) { this.petBreed = petBreed; }

}
