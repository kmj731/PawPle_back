package com.project.spring.pawple.app.point;

import java.util.List;

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
        @RequestParam List<MissionType> missions
    ) {
        return pointService.completeMission(userId, missions);
    }

    @PostMapping("/attendance")
    public ResponseEntity<?> dailyAttendance(@RequestParam Long userId) {
    return pointService.giveDailyAttendancePoint(userId);
}

}
