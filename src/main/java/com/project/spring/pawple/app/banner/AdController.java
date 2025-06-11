package com.project.spring.pawple.app.banner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ads")
public class AdController {

    @Autowired
    private RevenueService revenueService;

    @PostMapping("/click")
    public ResponseEntity<?> recordAdClick() {
        revenueService.addRevenue(10); // 클릭당 10원 증가
        return ResponseEntity.ok().build();
    }

    @GetMapping("/revenue")
public ResponseEntity<Integer> getTotalRevenue() {
    int total = revenueService.getRevenue();
    return ResponseEntity.ok(total);
}
}
