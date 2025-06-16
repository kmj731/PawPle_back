package com.project.spring.pawple.app.health;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HealthCheckRecordDto {
    private Long id;
    private LocalDateTime checkedAt;
    private int totalScore;
    private String resultStatus;
    private List<String> warnings;
    private List<HealthCheckDetailDto> details;

    public static HealthCheckRecordDto fromEntity(HealthCheckRecord record) {
        return HealthCheckRecordDto.builder()
            .id(record.getId())
            .checkedAt(record.getCheckedAt())
            .totalScore(record.getTotalScore())
            .resultStatus(record.getResultStatus())
            .warnings(record.getWarnings())
            .details(
                record.getDetails() != null
                    ? record.getDetails().stream().map(detail -> {
                        HealthCheckDetailDto dto = new HealthCheckDetailDto();
                        dto.setCategory(detail.getCategory());
                        dto.setScore(detail.getScore());
                        dto.setSelectedOptions(detail.getSelectedOptions());
                        return dto;
                    }).collect(Collectors.toList())
                    : null
            )
            .build();
    }
}