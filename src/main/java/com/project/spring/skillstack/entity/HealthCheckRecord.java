package com.project.spring.skillstack.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Id;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private int totalScore;
    private String resultStatus;
    private LocalDateTime checkedAt;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "pet_id", foreignKey = @ForeignKey(name = " FK_health_pet") ,referencedColumnName = "id", nullable = false)
    private PetEntity pet;

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HealthCheckDetail> details = new ArrayList<>();
}