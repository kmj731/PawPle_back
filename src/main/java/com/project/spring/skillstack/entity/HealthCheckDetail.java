
package com.project.spring.skillstack.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Id;
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
// import net.minidev.json.annotate.JsonIgnore;

@Entity
@Table(name = "HEALTH_CHECK_DETAIL")
@Getter @Setter
@NoArgsConstructor
public class HealthCheckDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    @ElementCollection
    @CollectionTable(name = "health_check_answers", joinColumns = @JoinColumn(name = "detail_id"))
    @Column(name = "answer")
    private List<String> selectedAnswers;

    private int score;

   
    @ManyToOne
    @JoinColumn(name = "record_id")
    @JsonIgnore
    private HealthCheckRecord record;
}
