package com.project.spring.skillstack.dto;

import java.time.LocalDate;
import java.util.List;

import com.project.spring.skillstack.entity.HealthCheckRecord;
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
    private String imageUrl;
    private String thumbnailUrl;
    private List<HealthCheckRecord> healthRecords;

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
        this.imageUrl = pet.getImageUrl();
        this.thumbnailUrl = pet.getThumbnailUrl();
        this.healthRecords = pet.getHealthRecords();
        
    }

    // 정적 변환 메서드
    public static PetDto fromEntity(PetEntity pet) {
        if (pet == null) return null;
        PetDto dto = new PetDto();
        dto.id = pet.getId();
        dto.petName = pet.getPetName();
        dto.petType = pet.getPetType();
        dto.petAge = pet.getPetAge();
        dto.petGender = pet.getPetGender();
        dto.petBreed = pet.getPetBreed();
        dto.weight = pet.getWeight();
        dto.registrationDate = pet.getRegistrationDate();
        dto.imageUrl = pet.getImageUrl();
        dto.thumbnailUrl = pet.getThumbnailUrl();
        dto.healthRecords = pet.getHealthRecords();
        return dto;
    }

    public Long getId() { return id; }
    public String getPetName() { return petName; }
    public String getPetType() { return petType; }
    public Integer getPetAge() { return petAge; }
    public String getPetGender() { return petGender; }
    public String getPetBreed() { return petBreed; }
    public Double getWeight() { return weight; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public String getImageUrl() { return imageUrl; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public List<HealthCheckRecord> getHealthRecords() { return healthRecords; }

    public void setPetType(String petType) { this.petType = petType; }
    public void setWeight(Double weight) { this.weight = weight; }
    public void setPetName(String petName) { this.petName = petName; }
    public void setPetAge(Integer petAge) { this.petAge = petAge; }
    public void setPetGender(String petGender) { this.petGender = petGender; }
    public void setPetBreed(String petBreed) { this.petBreed = petBreed; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public void setHealthRecords(List<HealthCheckRecord> healthRecords) { this.healthRecords = healthRecords; }

}
