package com.project.spring.skillstack.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Id; // ✅ JPA용 올바른 임포트
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "health_check_record")
@Getter @Setter
@NoArgsConstructor

public class HealthCheckRecord {
    @Id @GeneratedValue
    private Long id;

    private Long userId;
    private int totalScore;
    private String resultStatus;

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HealthCheckDetail> details = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    private PetEntity pet;

    @Column(nullable = false)
    private LocalDateTime checkedAt;

}
