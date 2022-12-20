package com.today.todayproject.domain.notification.dto;

import com.today.todayproject.domain.notification.Notification;
import com.today.todayproject.domain.notification.NotificationType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationResponseDto {

    private Long id; // 알림 ID
    private NotificationType notificationType; // 알림 타입
    private String content; // 알림 내용
    private Boolean isRead; // 읽었는지 여부

    @Builder
    public NotificationResponseDto(Long id, NotificationType notificationType, String content, Boolean isRead) {
        this.id = id;
        this.notificationType = notificationType;
        this.content = content;
        this.isRead = isRead;
    }


    public static NotificationResponseDto generate(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .notificationType(notification.getNotificationType())
                .content(notification.getContent())
                .isRead(notification.getIsRead())
                .build();
    }
}
