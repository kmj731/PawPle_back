package com.project.spring.pawple.app.animal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/animal")
public class AnimalController {

    private final AnimalApiService service;
    private final RestTemplate restTemplate = new RestTemplate();

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

    @GetMapping("/image")
    public ResponseEntity<byte[]> proxyImage(@RequestParam String url) {
        try {
            URI targetUri = URI.create(url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0"); // 일부 서버는 User-Agent 필요

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                targetUri, HttpMethod.GET, requestEntity, byte[].class
            );

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.IMAGE_JPEG); // 또는 IMAGE_PNG

            return new ResponseEntity<>(response.getBody(), responseHeaders, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build(); // 실패 시 빈 응답
        }
    }
}
