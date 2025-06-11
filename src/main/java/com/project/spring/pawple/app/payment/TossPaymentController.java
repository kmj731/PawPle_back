package com.project.spring.pawple.app.payment;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/store/toss")
@RequiredArgsConstructor
public class TossPaymentController {

    @Value("${toss.secret-key}")
    private String tossSecretKey;
    @Value("${spring.security.cors.site}")
    String corsOrigin;

    // @PostMapping("/pay")
    // public ResponseEntity<String> requestPayment(@RequestBody TossPaymentRequestDto dto) {
    //     String basicAuth = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes());

    //     RestTemplate restTemplate = new RestTemplate();

    //     HttpHeaders headers = new HttpHeaders();
    //     headers.set("Authorization", "Basic " + basicAuth);
    //     headers.setContentType(MediaType.APPLICATION_JSON);

    //     Map<String, Object> request = new HashMap<>();
    //     request.put("amount", dto.getAmount());
    //     request.put("orderId", dto.getOrderId());
    //     request.put("orderName", dto.getOrderName());
    //     request.put("successUrl", corsOrigin + "/store/success");
    //     // request.put("failUrl", "http://localhost:9999/store/toss/fail");

    //     HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(request, headers);

    //     String url = "https://api.tosspayments.com/v1/payments";

    //     ResponseEntity<String> response = restTemplate.postForEntity(url, httpEntity, String.class);
    //     return ResponseEntity.ok(response.getBody());
    // }

    @GetMapping("/success")
    public String success(@RequestParam String paymentKey,
                            @RequestParam String orderId,
                            @RequestParam int amount) {
        return "결제 성공! paymentKey=" + paymentKey;
    }

    @GetMapping("/fail")
    public String fail(@RequestParam String code,
                        @RequestParam String message,
                        @RequestParam String orderId) {
        return "결제 실패: " + message;
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmPayment(
        @RequestParam String paymentKey,
        @RequestParam String orderId,
        @RequestParam int amount
    ) {
        String basicAuth = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes());

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + basicAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("orderId", orderId);
        body.put("amount", amount);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey;

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        System.out.println("✅ Toss 승인 응답: " + response.getBody()); // 🔍 이거 꼭 찍어주세요

        return ResponseEntity.ok(response.getBody());
    }
}

