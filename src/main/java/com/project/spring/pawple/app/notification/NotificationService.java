package com.project.spring.pawple.app.notification;

import com.project.spring.pawple.app.notification.NotificationDto;
import com.project.spring.pawple.app.post.PostEntity;
import com.project.spring.pawple.app.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // 알림 생성
    public void notifyPostAuthor(UserEntity receiver, PostEntity post, String message) {
        if (receiver == null || receiver.getId() == null) return;

        NotificationEntity notification = NotificationEntity.builder()
                .receiver(receiver)
                .post(post)
                .message(message)
                .build();

        notificationRepository.save(notification);
    }

    // 읽지 않은 알림 리스트 조회 (DTO로 변환)
    public List<NotificationDto> getUnreadNotifications(UserEntity user) {
        List<NotificationEntity> notifications = notificationRepository.findByReceiverAndIsReadFalse(user);

        return notifications.stream()
                .map(n -> NotificationDto.builder()
                        .id(n.getId())
                        .message(n.getMessage())
                        .isRead(n.isRead())
                        .createdAt(n.getCreatedAt())
                        .postId(n.getPost() != null ? n.getPost().getId() : null)
                        .postTitle(n.getPost() != null ? n.getPost().getTitle() : null)
                        .build())
                .toList();
    }

    // 알림 읽음 처리
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
    // 알림 모두 읽음 처리
    public void markAllAsRead(UserEntity user) {
    List<NotificationEntity> unread = notificationRepository.findByReceiverAndIsReadFalse(user);
    for (NotificationEntity n : unread) {
        n.setRead(true);
    }
    notificationRepository.saveAll(unread);
}

}
