package com.project.spring.pawple.app.notification;

import com.project.spring.pawple.app.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByReceiverAndIsReadFalse(UserEntity receiver);
}
