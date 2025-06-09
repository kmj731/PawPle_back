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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;         // 상품명
    private String brand;        // 브랜드
    private double rating;       // 평점
    private int reviews;         // 리뷰 수
    private int discount;        // 할인율 (%)
    private int price;           // 할인가
    private int originalPrice;   // 원가

    @ElementCollection
    private List<String> tags = new ArrayList<>(); // 태그들

    private String image;        // 이미지 URL

    // 생성자, Getter/Setter 등 생략 가능 (Lombok 사용 시)
}

