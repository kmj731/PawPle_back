package com.project.spring.pawple.application.consult;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.spring.pawple.application.pet.PetEntity;
import com.project.spring.pawple.application.user.UserEntity;

@Entity
@Table(name = "CONSULT_POST_TABLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ConsultPostSeq")
    @SequenceGenerator(name = "ConsultPostSeq", sequenceName = "ConsultPostSeq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "CLOB")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "CLOB")
    private String replyContent;

    @Column(length = 50)
    private String replyAuthor;

    @Column
    private LocalDateTime replyCreatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"pets", "password", "email", "roles"})
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    @JsonIgnoreProperties({"owner", "healthRecords", "vaccineRecords"})
    private PetEntity pet;

    @Column(length = 50, name = "sub_category")
    private String subCategory;

    @Column(nullable = false, length = 20)
    private String status;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
