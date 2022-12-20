package com.today.todayproject.domain.notification.service;

import com.today.todayproject.domain.notification.NotificationType;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.global.BaseException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {

    SseEmitter friendRequest(String lastEventId) throws BaseException;

    void send(User receiver, NotificationType notificationType, String content);
}
