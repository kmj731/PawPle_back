package com.project.spring.pawple.app.report;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Transactional
    public void updateStatus(Long id, String status) {
        ReportEntity report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다. id=" + id));

        // 상태 유효성 검사 예시 (필요시 추가)
        if (!status.equals("처리중") && !status.equals("처리완료") && !status.equals("대기중")) {
            throw new IllegalArgumentException("유효하지 않은 상태값입니다: " + status);
        }

        report.setStatus(status);
        // JPA 변경 감지에 의해 자동 반영됨 (save 호출 안해도 됨)
    }
}
