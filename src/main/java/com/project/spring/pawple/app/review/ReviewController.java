package com.project.spring.pawple.app.review;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/store/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public List<ReviewDto> getPublicReviewsByProduct(@PathVariable Long productId) {
        return reviewService.findPublicByProductId(productId)
                .stream().map(ReviewDto::fromEntity).toList();
    }

    @PostMapping
    public ReviewDto createReview(@RequestBody ReviewDto dto) {
        ReviewEntity saved = reviewService.save(dto.toEntity());
        return ReviewDto.fromEntity(saved);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.delete(id);
    }

    @GetMapping("/product/{productId}/rating")
    public Double getAverageRating(@PathVariable Long productId) {
        return reviewService.calculateAverageRating(productId);
    }

    @GetMapping("/product/{productId}/rating-summary")
    public Map<String, Object> getRatingSummary(@PathVariable Long productId) {
        return reviewService.getRatingSummary(productId);
    }
}

