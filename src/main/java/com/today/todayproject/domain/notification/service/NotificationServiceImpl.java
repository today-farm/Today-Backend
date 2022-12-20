package com.today.todayproject.domain.notification.service;

import com.today.todayproject.domain.notification.Notification;
import com.today.todayproject.domain.notification.NotificationType;
import com.today.todayproject.domain.notification.dto.NotificationResponseDto;
import com.today.todayproject.domain.notification.repository.EmitterRepository;
import com.today.todayproject.domain.notification.repository.NotificationRepository;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.domain.user.repository.UserRepository;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import com.today.todayproject.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final UserRepository userRepository;
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    private static final String DELIMITER = "_";
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    @Override
    public SseEmitter friendRequest(String lastEventId) throws BaseException {
        User findUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        Long userId = findUser.getId();
        String emitterId = makeTimeIncludeId(userId);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 503 에러 방지 -> 더미 이벤트 전송
        String eventId = makeTimeIncludeId(userId);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId = " + userId + "]");

        // 클라이언트가 미수신한 Event 목록이 존재하는 경우 전송하여 Event 유실 예방
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, userId, emitterId, emitter);
        }

        return emitter;
    }

    private String makeTimeIncludeId(Long userId) {
        return userId + DELIMITER + System.currentTimeMillis();
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void sendLostData(String lastEventId, Long userId, String emitterID, SseEmitter sseEmitter) {
        Map<String, Object> eventCaches =
                emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId));
        eventCaches.entrySet().stream()
                // Last-Event-Id 기준 뒤의 데이터 추출
                // compareTo로 생성한 시간을 비교하는데(userId는 같으므로), entry.getKey() 값이 lastEventId보다 크면(< 0)
                // lastEventId 뒤의 데이터임.
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(sseEmitter, entry.getKey(), emitterID, entry.getValue()));
    }

    @Override
    public void send(User receiver, NotificationType notificationType, String content) {
        Notification notification =
                notificationRepository.save(generateNotification(receiver, notificationType, content));

        String eventId = makeTimeIncludeId(receiver.getId());
        Map<String, SseEmitter> emitters =
                emitterRepository.findAllEmitterStartWithByUserId(String.valueOf(receiver.getId()));
        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendNotification(emitter, eventId, key, NotificationResponseDto.generate(notification));
                }
        );
    }

    private Notification generateNotification(User receiver, NotificationType notificationType, String content) {
        return Notification.builder()
                .receiver(receiver)
                .notificationType(notificationType)
                .content(content)
                .isRead(false)
                .build();
    }
}
