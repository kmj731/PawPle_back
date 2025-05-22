package com.project.spring.skillstack.entity;

import java.util.List;


import jakarta.persistence.Id; // ✅ JPA용 올바른 임포트
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "health_check_detail")
@Getter @Setter
@NoArgsConstructor
public class HealthCheckDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category; // 예: "심장", "피부/귀", ...


    @ElementCollection
    @CollectionTable(name = "health_check_answers", joinColumns = @JoinColumn(name = "detail_id"))
    @Column(name = "answer")
    private List<String> selectedAnswers; // ["없어요", "자주 그래요", ...]
    private int score; // 해당 항목 점수
    
    @ManyToOne
    @JoinColumn(name = "record_id")
    private HealthCheckRecord record;
}
