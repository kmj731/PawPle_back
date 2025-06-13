package com.project.spring.pawple.app.animal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/animal")
public class AnimalController {

    private final AnimalApiService service;

    public AnimalController(AnimalApiService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<String> getAnimals() {
        return ResponseEntity.ok(service.getAnimalsJson());
    }

    @GetMapping("/all")
    public ResponseEntity<String> getAllRecentAnimals() {
        return ResponseEntity.ok(service.getAllRecentAnimalsJson());
    }

    @GetMapping("/{desertionNo}")
    public ResponseEntity<JsonNode> getAnimalDetail(@PathVariable    String desertionNo) {
        JsonNode animal = service.getAnimalDetailByDesertionNo(desertionNo);
        return ResponseEntity.ok(animal);
    }

}
