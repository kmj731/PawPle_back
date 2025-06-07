package com.project.spring.skillstack.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.spring.skillstack.dao.ProductRepository;
import com.project.spring.skillstack.entity.ProductEntity;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductEntity> findAll() {
        return productRepository.findAll();
    }

    public ProductEntity save(ProductEntity product) {
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public ProductEntity update(Long id, ProductEntity updated) {
        ProductEntity product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("상품 없음"));

        // 필요한 필드만 수정
        product.setName(updated.getName());
        product.setBrand(updated.getBrand());
        product.setRating(updated.getRating());
        product.setReviews(updated.getReviews());
        product.setDiscount(updated.getDiscount());
        product.setPrice(updated.getPrice());
        product.setOriginalPrice(updated.getOriginalPrice());
        product.setTags(updated.getTags());
        product.setImage(updated.getImage());

        return productRepository.save(product);
    }
}

