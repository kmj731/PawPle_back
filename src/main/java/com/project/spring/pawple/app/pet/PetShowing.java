package com.project.spring.pawple.app.pet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetShowing {
    private Long id;
    private String petName;
    private String petBreed;
    private Integer petAge;
    private String imageUrl;
    private String thumbnailUrl;

    public PetShowing(PetEntity pet){
        this.id = pet.getId();
        this.petName = pet.getPetName();
        this.petBreed = pet.getPetBreed();
        this.petAge = pet.getPetAge();
        this.imageUrl = pet.getImageUrl();
        this.thumbnailUrl = pet.getThumbnailUrl();
    }
}
