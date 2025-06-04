package com.project.spring.skillstack.controller.health;

import com.project.spring.skillstack.dao.PetRepository;
import com.project.spring.skillstack.dto.VaccinationHistoryDto;
import com.project.spring.skillstack.entity.PetEntity;
import com.project.spring.skillstack.entity.VaccinationRecord;
import com.project.spring.skillstack.repository.VaccinationRecordRepository;
import com.project.spring.skillstack.service.VaccinationService;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/vaccine")
public class VaccinationController {

    private final VaccinationService vaccinationService;
    private final PetRepository petRepository;
    private final VaccinationRecordRepository vaccinationRecordRepository;

    @PostMapping("/calculate")
    public Map<String, Object> calculateDday(
            @RequestParam("petId") Long petId,
            @RequestParam("step") int step,
            @RequestParam("selectedDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate selectedDate
    ) {
        PetEntity pet = petRepository.findById(petId).orElseThrow();

        // 백신 이름 & 안내 메시지 가져오기
        String vaccineName = vaccinationService.getVaccineName(step);
        String guideMessage = vaccinationService.getVaccineGuideMessage(step);

        LocalDate baseDate = selectedDate;  // 선택한 날짜를 기준으로
        LocalDate nextVaccineDate = vaccinationService.calculateNextVaccinationDate(step, baseDate);
        long dday = ChronoUnit.DAYS.between(LocalDate.now(), nextVaccineDate);

        String ddayMessage = dday == 0 ? "D-Day" : (dday > 0 ? "D-" + dday : "D+" + Math.abs(dday));
        

        VaccinationRecord record = VaccinationRecord.builder()
                .pet(pet)
                .step(step)
                .vaccineName(vaccinationService.getVaccineName(step))
                .vaccinatedAt(selectedDate)
                .nextVaccinationDate(nextVaccineDate)
                .build();
        vaccinationRecordRepository.save(record);


       // 반환할 데이터 구성
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("vaccine", record.getVaccineName());
        result.put("guide", guideMessage);
        result.put("dday", ddayMessage);
        result.put("nextDate", nextVaccineDate.toString());
        result.put("basedOn", selectedDate.toString());


    boolean missed = vaccinationService.isVaccinationMissed(nextVaccineDate);
    // 누락된 경우에만 경고 메시지 추가
    if (missed) {
        result.put("warn", "❗ 예방접종이 누락되었을 수 있어요!");
    }

    return result;
    }

    @GetMapping("/history")
    public List<VaccinationHistoryDto> getVaccinationHistory(@RequestParam("petId") Long petId) {
    List<VaccinationRecord> records = vaccinationRecordRepository.findByPetIdOrderByVaccinatedAtDesc(petId);

    return records.stream().map(record -> {
        long ddayValue = ChronoUnit.DAYS.between(LocalDate.now(), record.getNextVaccinationDate());
        String dday = (ddayValue == 0) ? "D-Day" :
                      (ddayValue > 0) ? "D-" + ddayValue : "D+" + Math.abs(ddayValue);
        return new VaccinationHistoryDto(
                record.getStep(),
                record.getVaccineName(),
                record.getVaccinatedAt(),
                record.getNextVaccinationDate(),
                dday
        );
    }).toList();
}

}
