package com.today.todayproject.domain.notification.repository;

import com.today.todayproject.domain.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
