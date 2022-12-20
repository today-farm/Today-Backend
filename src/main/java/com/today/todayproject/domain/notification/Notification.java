package com.today.todayproject.domain.notification;

import com.today.todayproject.domain.BaseTimeEntity;
import com.today.todayproject.domain.friend.Friend;
import com.today.todayproject.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
public class Notification extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User receiver;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private Boolean isRead;

    private String content;
}
