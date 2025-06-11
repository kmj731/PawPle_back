package com.project.spring.pawple.app.banner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class RevenueService {

    @Autowired
    private RevenueRepository revenueRepository;

    @Transactional
    public void addRevenue(int amount) {
        Revenue revenue = revenueRepository.findTopByOrderByIdAsc().orElseGet(() -> new Revenue(0));
        revenue.setAmount(revenue.getAmount() + amount);
        revenueRepository.save(revenue);
    }

    public int getRevenue() {
    return revenueRepository.findTopByOrderByIdAsc()
            .map(Revenue::getAmount)
            .orElse(0);
}
}
