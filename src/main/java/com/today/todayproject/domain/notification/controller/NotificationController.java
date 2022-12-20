package com.today.todayproject.domain.notification.controller;

import com.today.todayproject.domain.notification.service.NotificationService;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.global.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/friend-request", produces = "text/event-stream")
    public SseEmitter friendRequest(
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId)
            throws BaseException {
            return notificationService.friendRequest(lastEventId);
    }
}
