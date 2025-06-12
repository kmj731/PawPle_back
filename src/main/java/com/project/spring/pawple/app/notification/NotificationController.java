package com.project.spring.pawple.app.notification;

import com.project.spring.pawple.app.notification.NotificationDto;
import com.project.spring.pawple.app.user.UserEntity;
import com.project.spring.pawple.app.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    // 읽지 않은 알림 목록 조회 (DTO로 반환)
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(Authentication authentication) {
        String username = authentication.getName();
        UserEntity user = userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        List<NotificationDto> notifications = notificationService.getUnreadNotifications(user);
        return ResponseEntity.ok(notifications);
    }

    // 알림 읽음 처리
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    // 알림 모두 읽음 처리
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        String username = authentication.getName();
        UserEntity user = userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        notificationService.markAllAsRead(user);
        return ResponseEntity.ok().build();
    }

}
