package com.project.spring.pawple.app.store;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductService {

    @Value("${upload.path:/uploads}")
    private String uploadDir;
    
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public String storeImage(MultipartFile file) {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File dest = new File(System.getProperty("user.dir") + uploadDir + "/product/" + filename);

        dest.getParentFile().mkdirs(); // 폴더 없으면 생성
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }
System.out.println("저장된 이미지 URL: " + filename);
        return "/uploads/product/" + filename;
    }

    public List<ProductEntity> findAll() {
        return productRepository.findAll();
    }

    public List<ProductEntity> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public ProductEntity save(ProductEntity product) {
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public ProductEntity findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }
    
    public ProductEntity update(Long id, ProductEntity updated) {
        ProductEntity product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("상품 없음"));

        product.setName(updated.getName());
        product.setBrand(updated.getBrand());
        product.setRating(updated.getRating());
        product.setReviews(updated.getReviews());
        product.setDiscount(updated.getDiscount());
        product.setPrice(updated.getPrice());
        product.setOriginalPrice(updated.getOriginalPrice());
        product.setTags(updated.getTags());
        product.setImage(updated.getImage());
        product.setCategory(updated.getCategory());
        product.setQuantity(updated.getQuantity());

        return productRepository.save(product);
    }
}

