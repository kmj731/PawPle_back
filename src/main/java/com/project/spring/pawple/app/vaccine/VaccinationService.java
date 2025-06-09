package com.project.spring.pawple.app.vaccine;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class VaccinationService {

    public LocalDate calculateNextVaccinationDate(int step, LocalDate vaccinatedAt) {
        return switch (step) {
        case 7 -> vaccinatedAt.plusYears(1);     // 1세~7세
        case 8 -> vaccinatedAt.plusMonths(6);    // 8세~12세
        case 9 -> vaccinatedAt.plusMonths(3);    // 13세 이상
        default -> vaccinatedAt.plusWeeks(2);    // 기본: 1~6차 접종
    };
}

    public String getVaccineName(int step) {
        return switch (step) {
            case 1 -> "1차접종(종합백신+코로나 장염)";
            case 2 -> "2차접종(종합백신+코로나 장염)";
            case 3 -> "3차접종(종합백신+켄넬코프)";
            case 4 -> "4차접종(종합백신+켄넬코프)";
            case 5 -> "5차접종(종합백신+인플루엔자)";
            case 6 -> "6차접종(광견병+인플루엔자)";
            case 7 -> "1세~7세";
            case 8 -> "8세~12세";
            case 9 -> "13세 이상";
            default -> "알 수 없는 단계입니다.";
        };
    }

    public String getVaccineGuideMessage(int step) {
    if (step >= 1 && step <= 6) {
        return "1세 미만은 2주 간격으로 건강검진이 필요해요.";
    }

    return switch (step) {
        case 7 -> "1세~7세: 1년에 한 번 종합 백신 접종을 추천합니다.";
        case 8 -> "8세~12세: 6개월마다 접종을 권장합니다.";
        case 9 -> "13세 이상: 면역력이 약해지므로 3개월 간격으로 접종하세요.";
        default -> "알 수 없는 단계입니다.";
    };
}

    private final VaccinationRecordRepository vaccinationRecordRepository;

    public boolean isVaccinationMissed(Long petId) {
    List<VaccinationRecord> records = vaccinationRecordRepository.findByPetIdOrderByVaccinatedAtDesc(petId);
    if (records.isEmpty()) return false;

    VaccinationRecord latest = records.get(0);
    LocalDate today = LocalDate.now();

    // 예정일이 오늘보다 과거인데, 이후 기록이 없으면 누락
    return latest.getNextVaccinationDate().isBefore(today);
}

    public boolean isVaccinationMissed(LocalDate nextDate) {
    return nextDate.isBefore(LocalDate.now());
}


}
