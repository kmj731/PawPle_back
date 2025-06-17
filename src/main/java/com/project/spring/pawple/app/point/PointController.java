package com.project.spring.pawple.app.point;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @PostMapping("/mission/complete")
    public ResponseEntity<?> completeMission(
    @RequestParam Long userId,
    @RequestParam List<String> missions
) {
    List<MissionType> parsed;
    try {
        parsed = missions.stream()
            .map(m -> MissionType.valueOf(m.toUpperCase()))  // 대소문자 허용
            .collect(Collectors.toList());
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body("❗ 유효하지 않은 미션 항목이 포함되어 있어요!");
    }

    return pointService.completeMission(userId, parsed);
}




    @PostMapping("/attendance")
    public ResponseEntity<?> dailyAttendance(@RequestParam Long userId) {
    return pointService.giveDailyAttendancePoint(userId);
}

}
