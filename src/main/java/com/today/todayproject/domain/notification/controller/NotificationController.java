package com.today.todayproject.domain.notification.controller;

import com.today.todayproject.domain.notification.FriendNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FriendNotificationController {

    private final FriendNotificationService friendNotificationService;

    @GetMapping


}
