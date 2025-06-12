package com.project.spring.pawple.app.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    // 사용자별 주문 조회
    List<OrderEntity> findByUserId(Long userId);

    // ✅ 월별 매출 (Native Query)
    @Query("""
        SELECT 
            TO_CHAR(o.orderDate, 'YYYY-MM') AS month,
            SUM(o.totalAmount)
        FROM OrderEntity o
        WHERE o.status IN ('결제완료', '배송중', '배송완료')
        GROUP BY TO_CHAR(o.orderDate, 'YYYY-MM')
        ORDER BY month
    """)
    List<Object[]> findMonthlySales();


    // ✅ 전체 매출 합계 (JPQL)
    @Query("""
        SELECT SUM(o.totalAmount)
        FROM OrderEntity o
        WHERE o.status IN ('결제완료', '배송중', '배송완료')
    """)
    Long findTotalSales();
} 
