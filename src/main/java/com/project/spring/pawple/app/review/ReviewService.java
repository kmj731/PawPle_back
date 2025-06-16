package com.project.spring.pawple.app.review;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    // 리뷰 저장
    public ReviewEntity save(ReviewEntity review) {
        return reviewRepository.save(review);
    }

    // 리뷰 삭제
    public void delete(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    // 특정 상품의 모든 리뷰 조회
    public List<ReviewEntity> findByProductId(Long productId) {
        return reviewRepository.findByProduct_Id(productId);
    }

    // 리뷰 ID로 단일 조회
    public ReviewEntity findById(Long id) {
        return reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
    }

    // 특정 상품의 리뷰 평점 평균 조회
    public Double calculateAverageRating(Long productId) {
        List<ReviewEntity> reviews = reviewRepository.findByProduct_Id(productId);
        return reviews.stream()
                    .mapToInt(ReviewEntity::getRating)
                    .average()
                    .orElse(0.0); // 리뷰가 없으면 0.0 반환
    }

    // 특정 상품의 리뷰 개수 조회
    public Map<String, Object> getRatingSummary(Long productId) {
        List<ReviewEntity> reviews = reviewRepository.findByProduct_IdAndIsPublic(productId, "Y");

        int reviewCount = reviews.size();
        double avgRating = reviews.stream()
                                .mapToInt(ReviewEntity::getRating)
                                .average()
                                .orElse(0.0);

        Map<String, Object> result = new HashMap<>();
        result.put("reviewCount", reviewCount);
        result.put("averageRating", avgRating);

        return result;
    }

    // 공개된 리뷰만 조회
    public List<ReviewEntity> findPublicByProductId(Long productId) {
        return reviewRepository.findByProduct_IdAndIsPublic(productId, "Y");
    }

    // 리뷰 공개 여부 설정
    public ReviewEntity updateVisibility(Long reviewId, String isPublic) {
    ReviewEntity review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
    
    review.setIsPublic(isPublic);
    return reviewRepository.save(review);
    }


}