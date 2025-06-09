package com.project.spring.pawple.application.health;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

import com.project.spring.pawple.application.pet.PetEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccinationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 반려동물인지 연결
    @ManyToOne
    @JoinColumn(name = "pet_id")
    private PetEntity pet;


    // 백신 단계 (1~6 or 7)
    private int step;

    // 백신 이름
    private String vaccineName;

    // 실제 접종한 날짜
    private LocalDate vaccinatedAt;

    // 다음 접종 예정일 (자동 계산 가능)
    private LocalDate nextVaccinationDate;
}
