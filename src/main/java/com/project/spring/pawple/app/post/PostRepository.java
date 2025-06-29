package com.project.spring.pawple.app.post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.spring.pawple.app.user.UserEntity;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    // 모든 게시글 수 조회
    long count();

    // 모든 게시글 페이징 조회
    Page<PostEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 특정 사용자의 게시글 조회
    Page<PostEntity> findByUserOrderByCreatedAtDesc(UserEntity user, Pageable pageable);

    // 제목으로 게시글 검색
    Page<PostEntity> findByTitleContainingOrderByCreatedAtDesc(String title, Pageable pageable);

    // 내용으로 게시글 검색
    Page<PostEntity> findByContentContainingOrderByCreatedAtDesc(String content, Pageable pageable);

    // 제목 또는 내용으로 게시글 검색
    Page<PostEntity> findByTitleContainingOrContentContainingOrderByCreatedAtDesc(String title, String content,
            Pageable pageable);

    // 페이징 없이, 유저의 글 전체를 최신순으로 조회
    List<PostEntity> findByUserOrderByCreatedAtDesc(UserEntity user);

    Optional<PostEntity> findByTitleAndUser(String postKey, UserEntity user);

    // Optional<PostEntity> findByTitle(String title);

    // 카테고리별 게시글 조회 (최신순)
    Page<PostEntity> findByCategoryOrderByCreatedAtDesc(String category, Pageable pageable);

    // 특정 사용자의 카테고리별 게시글 조회 (최신순)
    Page<PostEntity> findByUserAndCategoryOrderByCreatedAtDesc(UserEntity user, String category, Pageable pageable);

    // 카테고리 내에서 제목 또는 내용으로 검색 (최신순)
    Page<PostEntity> findByTitleContainingOrContentContainingAndCategoryOrderByCreatedAtDesc(
            String title, String content, String category, Pageable pageable);

    // 세부 카테고리
    Page<PostEntity> findByCategoryAndSubCategoryOrderByCreatedAtDesc(String category, String subCategory,
            Pageable pageable);

    Page<PostEntity> findByUserAndCategoryAndSubCategoryOrderByCreatedAtDesc(UserEntity user, String category,
            String subCategory, Pageable pageable);

    Page<PostEntity> findByTitleContainingOrContentContainingAndCategoryAndSubCategoryOrderByCreatedAtDesc(
            String title, String content, String category, String subCategory, Pageable pageable);

    // 조회수 증가
    @Modifying
    @Query("UPDATE PostEntity p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void increaseViewCount(@Param("id") Long id);

    // 인기글 조회 (조회수 기준, 조회수 같으면 최신순)
    Page<PostEntity> findAllByOrderByViewCountDescCreatedAtDesc(Pageable pageable);

    // 카테고리별 인기글 조회 (조회수 기준, 조회수 같으면 최신순)
    Page<PostEntity> findByCategoryOrderByViewCountDescCreatedAtDesc(String category, Pageable pageable);

    // 댓글 수 관련 메서드 추가
    @Modifying
    @Query("UPDATE PostEntity p SET p.commentCount = p.commentCount + 1 WHERE p.id = :id")
    void increaseCommentCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE PostEntity p SET p.commentCount = p.commentCount - 1 WHERE p.id = :id AND p.commentCount > 0")
    void decreaseCommentCount(@Param("id") Long id);

    // 댓글 수 기준 인기글 조회
    Page<PostEntity> findAllByOrderByCommentCountDescCreatedAtDesc(Pageable pageable);

    // 카테고리별 댓글 수 기준 인기글 조회
    Page<PostEntity> findByCategoryOrderByCommentCountDescCreatedAtDesc(String category, Pageable pageable);

    // 회원 삭제
    void deleteByUser_Id(Long userId);

    // 게시글 수정
    List<PostEntity> findByIsPublicTrue();

    // 좋아요 수 증가
    @Modifying
    @Query("UPDATE PostEntity p SET p.likeCount = p.likeCount + 1 WHERE p.id = :id")
    void increaseLikeCount(@Param("id") Long id);

    // 좋아요 수 감소
    @Modifying
    @Query("UPDATE PostEntity p SET p.likeCount = p.likeCount - 1 WHERE p.id = :id AND p.likeCount > 0")
    void decreaseLikeCount(@Param("id") Long id);

    // 좋아요 수 기준 인기글 조회
    Page<PostEntity> findAllByOrderByLikeCountDescCreatedAtDesc(Pageable pageable);

    // 카테고리별 좋아요 수 기준 인기글 조회
    Page<PostEntity> findByCategoryOrderByLikeCountDescCreatedAtDesc(String category, Pageable pageable);

    // 제목으로 조회
    List<PostEntity> findByTitleContainingIgnoreCase(String title);

    List<PostEntity> findByUser_NameContainingIgnoreCase(String username);

    // 게시글 ID보다 작은 가장 최근 글 (이전 글)
    Optional<PostEntity> findTopByIdLessThanAndIsPublicTrueOrderByIdDesc(Long id);

    // 게시글 ID보다 큰 가장 오래된 글 (다음 글)
    Optional<PostEntity> findTopByIdGreaterThanAndIsPublicTrueOrderByIdAsc(Long id);

    Page<PostEntity> findByUserId(Long userId, Pageable pageable);

    Page<PostEntity> findByIsPublicTrueOrderByViewCountDescCreatedAtDesc(Pageable pageable);


}