package com.project.spring.skillstack.dto;

import java.util.List;

import com.project.spring.skillstack.entity.ProductEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long id;
    private String name;
    private String brand;
    private double rating;
    private int reviews;
    private int discount;
    private int price;
    private int originalPrice;
    private List<String> tags;
    private String image;
    private String category;

    public static ProductDto fromEntity(ProductEntity entity) {
        return ProductDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .brand(entity.getBrand())
                .rating(entity.getRating())
                .reviews(entity.getReviews())
                .discount(entity.getDiscount())
                .price(entity.getPrice())
                .originalPrice(entity.getOriginalPrice())
                .tags(entity.getTags())
                .image(entity.getImage())
                .category(entity.getCategory())
                .build();
    }

    public ProductEntity toEntity() {
        return ProductEntity.builder()
                .id(this.id)
                .name(this.name)
                .brand(this.brand)
                .rating(this.rating)
                .reviews(this.reviews)
                .discount(this.discount)
                .price(this.price)
                .originalPrice(this.originalPrice)
                .tags(this.tags)
                .image(this.image)
                .category(this.category)
                .build();
    }
}
