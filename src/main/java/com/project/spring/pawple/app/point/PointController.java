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
    @RequestParam String missions
) {
    List<MissionType> parsed = List.of(missions.split(","))
        .stream()
        .map(String::trim)
        .map(MissionType::valueOf)
        .collect(Collectors.toList());

    return pointService.completeMission(userId, parsed);
}



    @PostMapping("/attendance")
    public ResponseEntity<?> dailyAttendance(@RequestParam Long userId) {
    return pointService.giveDailyAttendancePoint(userId);
}

}
