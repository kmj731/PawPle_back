package com.project.spring.pawple.app.banner;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface RevenueRepository extends JpaRepository<Revenue, Long> {
    Optional<Revenue> findTopByOrderByIdAsc();  
}
