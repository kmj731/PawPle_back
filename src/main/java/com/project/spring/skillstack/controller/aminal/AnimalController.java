package com.project.spring.skillstack.controller.aminal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/animal")
public class AnimalController {

    private final AnimalApiService service;

    public AnimalController(AnimalApiService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<String> getAnimals() {
        System.out.println("🐾 AnimalController 진입");
        return ResponseEntity.ok(service.getAnimalsJson());
    }

}
