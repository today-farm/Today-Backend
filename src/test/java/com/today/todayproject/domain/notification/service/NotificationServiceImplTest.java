package com.today.todayproject.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.today.todayproject.domain.notification.NotificationType;
import com.today.todayproject.domain.user.Role;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NotificationServiceImplTest {

    @Autowired
    NotificationService notificationService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    private User user;

    @BeforeEach
    void init() {
        PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        User user = User.builder()
                .email("email1@naver.com")
                .password(delegatingPasswordEncoder.encode("password1"))
                .nickname("user1")
                .role(Role.USER)
                .build();
        userRepository.save(user);
        this.user = user;
        saveAuthentication(user);
    }

    private void saveAuthentication(com.today.todayproject.domain.user.User myUser) {
        UserDetails user = org.springframework.security.core.userdetails.User.builder()
                .username(myUser.getEmail())
                .password(myUser.getPassword())
                .roles(myUser.getRole().name())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authoritiesMapper.mapAuthorities(user.getAuthorities()));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void 친구_요청_알림_진행() throws Exception {
        //given
        String lastEventId = "";

        //when, then
        Assertions.assertDoesNotThrow(() -> notificationService.friendRequest(lastEventId));
    }

    @Test
    void 친구_요청_알림_메시지_클라이언트로_전송() throws Exception {
        //given
        String lastEventId = "";
        notificationService.friendRequest(lastEventId); // 성공하면, sse 연결이 됨

        //when, then
        Assertions.assertDoesNotThrow(() -> notificationService.send(user, NotificationType.FRIEND_REQUEST,
                "친구 요청이 도착했습니다."));
    }
}