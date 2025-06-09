package com.project.spring.pawple.application.consult;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultRepository extends JpaRepository<ConsultEntity, Long> {

    // 최신순 페이징 조회
    Page<ConsultEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 상태 기준 필터링 (예: "PENDING", "ANSWERED" 등)
    Page<ConsultEntity> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
    Page<ConsultEntity> findBySubCategoryOrderByCreatedAtDesc(String category, Pageable pageable);
    Page<ConsultEntity> findByStatusAndSubCategoryOrderByCreatedAtDesc(String status, String category, Pageable pageable);

    // 사용자 이름 기준 필터링
    Page<ConsultEntity> findByUser_NameOrderByCreatedAtDesc(String username, Pageable pageable);

    // 사용자 이름 + 상태 기준 필터링
    Page<ConsultEntity> findByUser_NameAndStatusOrderByCreatedAtDesc(String username, String status, Pageable pageable);
    Page<ConsultEntity> findByUserName(String name, Pageable pageable);
}
