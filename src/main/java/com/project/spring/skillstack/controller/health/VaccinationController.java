package com.project.spring.skillstack.controller.health;

import com.project.spring.skillstack.dao.PetRepository;
import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.dto.VaccinationHistoryDto;
import com.project.spring.skillstack.entity.PetEntity;
import com.project.spring.skillstack.entity.VaccinationRecord;
import com.project.spring.skillstack.repository.VaccinationRecordRepository;
import com.project.spring.skillstack.service.VaccinationService;


import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UserRepository userRepository;


    

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

    @PutMapping("/update")
    public ResponseEntity<String> updateVaccinationRecord(
    @RequestParam("petId") Long petId,
    @RequestParam("vaccinatedAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate oldDate,
    @RequestParam("step") int step,
    @RequestParam("selectedDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newDate,
    @AuthenticationPrincipal UserDetails userDetails
) {
    // 로그인한 사용자 정보로 사용자 ID 가져오기
    String username = userDetails.getUsername();
    Long loginUserId = userRepository.findByName(username)
    .orElseThrow(() -> new RuntimeException("로그인한 유저 정보를 찾을 수 없습니다."))
    .getId();


    // pet 조회 + 주인 검증
    PetEntity pet = petRepository.findById(petId)
        .filter(p -> p.getOwner().getId().equals(loginUserId))
        .orElseThrow(() -> new RuntimeException("해당 펫이 존재하지 않거나 권한이 없습니다."));

    // 기존 기록 조회
    VaccinationRecord record = vaccinationRecordRepository
        .findByPetIdAndVaccinatedAt(petId, oldDate)
        .orElseThrow(() -> new RuntimeException("접종 기록을 찾을 수 없습니다."));

    // 정보 업데이트
    record.setStep(step);
    record.setVaccineName(vaccinationService.getVaccineName(step));
    record.setVaccinatedAt(newDate);
    record.setNextVaccinationDate(vaccinationService.calculateNextVaccinationDate(step, newDate));

    vaccinationRecordRepository.save(record);
    return ResponseEntity.ok("접종 기록이 성공적으로 수정되었습니다!");
}
    

    @DeleteMapping("/vaccine/delete")
    public ResponseEntity<String> deleteVaccinationRecord(
    @RequestParam("petId") Long petId,
    @RequestParam("vaccinatedAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vaccinatedAt,
    @AuthenticationPrincipal UserDetails userDetails
) {
    String username = userDetails.getUsername();
    Long loginUserId = userRepository.findByName(username)
    .orElseThrow(() -> new RuntimeException("로그인한 유저 정보를 찾을 수 없습니다."))
    .getId();


    PetEntity pet = petRepository.findById(petId)
        .filter(p -> p.getOwner().getId().equals(loginUserId))
        .orElseThrow(() -> new RuntimeException("해당 펫이 존재하지 않거나 권한이 없습니다."));

    VaccinationRecord record = vaccinationRecordRepository
        .findByPetIdAndVaccinatedAt(petId, vaccinatedAt)
        .orElseThrow(() -> new RuntimeException("해당 접종 기록을 찾을 수 없습니다."));

    vaccinationRecordRepository.delete(record);
    return ResponseEntity.ok("삭제 완료!");
}


    }
