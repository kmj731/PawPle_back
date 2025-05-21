package com.project.spring.skillstack.dto;

import java.time.LocalDate;

import com.project.spring.skillstack.entity.PetEntity;

public class PetDto {
    public Long id;
    public String name;
    public String type;
    public Integer age;
    public String gender;
    public String breed;
    public Double weight;
    public LocalDate registrationDate;

    public PetDto(PetEntity pet) {
        this.id = pet.getId();
        this.name = pet.getPetName();
        this.type = pet.getPetType();
        this.age = pet.getPetAge();
        this.gender = pet.getPetGender();
        this.breed = pet.getPetBreed();
        this.weight = pet.getWeight();
        this.registrationDate = pet.getRegistrationDate();
    }
}
