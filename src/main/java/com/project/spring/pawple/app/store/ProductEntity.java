package com.project.spring.pawple.app.store;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PRODUCT_TABLE")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(
    allocationSize = 1,
    initialValue = 1,
    name = "ProductSeq",
    sequenceName = "ProductSeq"
)
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ProductSeq")
    private Long id;

    private String name;
    private String brand;
    private Double rating;           // 평점 (null 허용)
    private Integer reviews;         // 리뷰 수
    private Integer discount;        // 할인율 (%)
    private Integer price;           // 할인가
    private Integer originalPrice;   // 원가

    @ElementCollection
    private List<String> tags = new ArrayList<>();

    private String image;
    private String category;
    private Integer quantity;        // 수량
    
}