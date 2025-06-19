package com.project.spring.pawple.app.animal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

import net.coobird.thumbnailator.Thumbnails;

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
        
@GetMapping("/image/download")
public ResponseEntity<Map<String, String>> downloadImage(@RequestParam String url) {
    System.out.println("🟡 이미지 다운로드 요청 URL: " + url);

    try {
        // 확장자 및 파일명 생성
        String extension = url.substring(url.lastIndexOf('.') + 1);
        String uuid = UUID.randomUUID().toString();
        String filename = uuid + "." + extension;

        // 저장 경로 설정
        String basePath = System.getProperty("user.dir") + "/uploads/animal/";
        String thumbPath = System.getProperty("user.dir") + "/uploads/animal_thumb";
        File imageDest = Paths.get(basePath, filename).toFile();
        File thumbDest = Paths.get(thumbPath, "thumb_" + filename).toFile();

        // 디렉토리 생성
        imageDest.getParentFile().mkdirs();
        thumbDest.getParentFile().mkdirs();

        // 이미지 다운로드
        byte[] imageBytes = restTemplate.getForObject(url, byte[].class);
        if (imageBytes == null || imageBytes.length == 0) {
            System.out.println("⚠️ 이미지가 비어 있음");
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }

        // 저장
        Files.write(imageDest.toPath(), imageBytes);
        System.out.println("✅ 이미지 저장 성공: " + imageDest.getAbsolutePath());

        // 썸네일 생성
        Thumbnails.of(imageDest)
                .size(150, 150)
                .outputFormat("jpg")
                .toFile(thumbDest);
        System.out.println("✅ 썸네일 생성 성공: " + thumbDest.getAbsolutePath());

        // 결과 Map 반환
        Map<String, String> result = new HashMap<>();
        result.put("imageUrl", "/animal/" + filename);
        result.put("thumbnailUrl", "/animal_thumb/thumb_" + filename);

        // 🔵 로그: 실제 파일명 및 반환 JSON
        System.out.println("📦 반환 파일명: " + filename);
        System.out.println("📦 반환 imageUrl: " + result.get("imageUrl"));
        System.out.println("📦 반환 thumbnailUrl: " + result.get("thumbnailUrl"));

        return ResponseEntity.ok(result);

    } catch (Exception e) {
        System.out.println("❌ 이미지 저장 실패: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
}
