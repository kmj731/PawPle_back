package com.project.spring.skillstack.controller.auth;

import com.project.spring.skillstack.dao.PetRepository;
import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.entity.PetEntity;
import com.project.spring.skillstack.entity.UserEntity;
import com.project.spring.skillstack.service.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    private PetRepository petRepository;
    @Autowired
    private UserRepository userRepository;
    @Value("${spring.security.cors.site}")
    String corsOrigin;

    @PostMapping("/register")
    public String registerPet(
            @RequestParam("petType") String petType,
            @RequestParam("weight") double weight,
            @RequestParam("petName") String petName,
            @RequestParam("petAge") int petAge,
            @RequestParam("petGender") String petGender,
            @RequestParam("petBreed") String petBreed,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return "redirect:" + corsOrigin + "/mypage?error=not_logged_in";
        }

        String username = userDetails.getUsername();
        Optional<UserEntity> optionalUser = userRepository.findByName(username);
        if (optionalUser.isEmpty()) {
            return "redirect:" + corsOrigin + "/mypage?error=user_not_found";
        }

        UserEntity owner = optionalUser.get();

        Optional<PetEntity> existingPet = petRepository.findByOwnerAndPetName(owner, petName);
        if (existingPet.isPresent()) {
            return "redirect:" + corsOrigin + "/mypage?error=duplicate_pet";
        }


        try {
            PetEntity pet = new PetEntity(
                    petType,
                    weight,
                    petName,
                    petAge,
                    petGender,
                    petBreed,
                    LocalDate.now(),
                    owner
            );

            petRepository.save(pet);
            return "redirect:" + corsOrigin + "/mypage?message=register_success";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:" + corsOrigin + "/mypage?error=registration_failed";
        }

    }





}