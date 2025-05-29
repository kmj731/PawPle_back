package com.project.spring.skillstack.dto;

import java.time.LocalDate;

import com.project.spring.skillstack.entity.PetEntity;

public class PetDto {

    private Long id;
    private String petName;
    private String petType;
    private Integer petAge;
    private String petGender;
    private String petBreed;
    private Double weight;
    private LocalDate registrationDate;
    

    public PetDto() {}
    
    public PetDto(PetEntity pet) {
        this.id = pet.getId();
        this.petName = pet.getPetName();
        this.petType = pet.getPetType();
        this.petAge = pet.getPetAge();
        this.petGender = pet.getPetGender();
        this.petBreed = pet.getPetBreed();
        this.weight = pet.getWeight();
        this.registrationDate = pet.getRegistrationDate();
        
    }

    public Long getId() { return id; }
    public String getPetName() { return petName; }
    public String getPetType() { return petType; }
    public Integer getPetAge() { return petAge; }
    public String getPetGender() { return petGender; }
    public String getPetBreed() { return petBreed; }
    public Double getWeight() { return weight; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    

    public void setPetType(String petType) { this.petType = petType; }
    public void setWeight(Double weight) { this.weight = weight; }
    public void setPetName(String petName) { this.petName = petName; }
    public void setPetAge(Integer petAge) { this.petAge = petAge; }
    public void setPetGender(String petGender) { this.petGender = petGender; }
    public void setPetBreed(String petBreed) { this.petBreed = petBreed; }
    
}
