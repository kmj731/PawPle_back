package com.project.spring.skillstack.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.spring.skillstack.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    
}
