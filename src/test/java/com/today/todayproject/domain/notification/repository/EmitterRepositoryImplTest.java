package com.today.todayproject.domain.notification.repository;

import com.today.todayproject.domain.notification.Notification;
import com.today.todayproject.domain.notification.NotificationType;
import com.today.todayproject.domain.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EmitterRepositoryImplTest {

    private EmitterRepository emitterRepository = new EmitterRepositoryImpl();
    private static final Long DEFAULT_TIMEOUT = 60L * 1000L * 60L;

    @Test
    void 새로운_Emitter_추가() {
        //given
        Long userId = 1L;
        String emitterId = userId + "_" + System.currentTimeMillis();
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        //when, then
        assertDoesNotThrow(() -> emitterRepository.save(emitterId, sseEmitter));
    }

    @Test
    void 어떤_회원이_접속한_모든_Emitter_찾기() throws InterruptedException {
        //given
        Long userId = 1L;
        String emitterId1 = userId + "_" + System.currentTimeMillis();
        emitterRepository.save(emitterId1, new SseEmitter(DEFAULT_TIMEOUT));

        Thread.sleep(100);
        String emitterId2 = userId + "_" + System.currentTimeMillis();
        emitterRepository.save(emitterId2, new SseEmitter(DEFAULT_TIMEOUT));

        Thread.sleep(100);
        String emitterId3 = userId + "_" + System.currentTimeMillis();
        emitterRepository.save(emitterId3, new SseEmitter(DEFAULT_TIMEOUT));

        //when
        Map<String, SseEmitter> actualResult =
                emitterRepository.findAllEmitterStartWithByUserId(String.valueOf(userId));

        //then
        assertThat(actualResult.size()).isEqualTo(3);
    }

    @Test
    void 어떤_회원에게_수신된_이벤트를_캐시에서_모두_찾기() throws InterruptedException {
        //given
        Long userId = 1L;
        String eventCacheId1 = userId + "_" + System.currentTimeMillis();
        Notification notification1 = Notification.builder()
                .receiver(User.builder().id(1L).build())
                .notificationType(NotificationType.FRIEND_REQUEST)
                .content("친구 요청이 도착했습니다.")
                .isRead(false)
                .build();
        emitterRepository.saveEventCache(eventCacheId1, notification1);

        Thread.sleep(100);
        String eventCacheId2 = userId + "_" + System.currentTimeMillis();
        Notification notification2 = Notification.builder()
                .receiver(User.builder().id(1L).build())
                .notificationType(NotificationType.FRIEND_ACCEPT)
                .content("친구 요청이 수락되었습니다.")
                .isRead(false)
                .build();
        emitterRepository.saveEventCache(eventCacheId2, notification2);

        Thread.sleep(100);
        String eventCacheId3 = userId + "_" + System.currentTimeMillis();
        Notification notification3 = Notification.builder()
                .receiver(User.builder().id(1L).build())
                .notificationType(NotificationType.FRIEND_DENY)
                .content("친구 요청이 거절되었습니다.")
                .isRead(false)
                .build();
        emitterRepository.saveEventCache(eventCacheId3, notification3);

        //when
        Map<String, Object> actualResult =
                emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId));

        //then
        assertThat(actualResult.size()).isEqualTo(3);
    }

    @Test
    void ID로_Emitter를_Repository에서_제거() {
        //given
        Long userId = 1L;
        String emitterId = userId + "_" + System.currentTimeMillis();
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        //when
        emitterRepository.save(emitterId, sseEmitter);
        emitterRepository.deleteById(emitterId);

        //then
        assertThat(emitterRepository.findAllEmitterStartWithByUserId(String.valueOf(userId)).size()).isEqualTo(0);
    }

    @Test
    void 저장된_모든_Emitter를_제거() throws InterruptedException {
        //given
        Long userId = 1L;
        String emitterId1 = userId + "_" + System.currentTimeMillis();
        emitterRepository.save(emitterId1, new SseEmitter(DEFAULT_TIMEOUT));

        Thread.sleep(100);
        String emitterId2 = userId + "_" + System.currentTimeMillis();
        emitterRepository.save(emitterId2, new SseEmitter(DEFAULT_TIMEOUT));

        //when
        emitterRepository.deleteAllEmitterStartWithUserId(String.valueOf(userId));

        //then
        assertThat(emitterRepository.findAllEmitterStartWithByUserId(String.valueOf(userId)).size()).isEqualTo(0);
    }

    @Test
    void 수신한_이벤트를_모두_삭제() throws InterruptedException {
        //given
        Long userId = 1L;
        String eventCacheId1 = userId + "_" + System.currentTimeMillis();
        Notification notification1 = Notification.builder()
                .receiver(User.builder().id(1L).build())
                .notificationType(NotificationType.FRIEND_REQUEST)
                .content("친구 요청이 도착했습니다.")
                .isRead(false)
                .build();
        emitterRepository.saveEventCache(eventCacheId1, notification1);

        Thread.sleep(100);
        String eventCacheId2 = userId + "_" + System.currentTimeMillis();
        Notification notification2 = Notification.builder()
                .receiver(User.builder().id(1L).build())
                .notificationType(NotificationType.FRIEND_ACCEPT)
                .content("친구 요청이 수락되었습니다.")
                .isRead(false)
                .build();
        emitterRepository.saveEventCache(eventCacheId2, notification2);

        Thread.sleep(100);
        String eventCacheId3 = userId + "_" + System.currentTimeMillis();
        Notification notification3 = Notification.builder()
                .receiver(User.builder().id(1L).build())
                .notificationType(NotificationType.FRIEND_DENY)
                .content("친구 요청이 거절되었습니다.")
                .isRead(false)
                .build();
        emitterRepository.saveEventCache(eventCacheId3, notification3);

        //when
        emitterRepository.deleteAllEventCacheStartWithUserId(String.valueOf(userId));

        //then
        assertThat(emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId)).size()).isEqualTo(0);
    }
}