package com.today.todayproject.global.email.service;

import com.today.todayproject.domain.user.Role;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.global.email.dto.AuthenticationCodeEmailSendDto;
import com.today.todayproject.global.email.dto.IssueTempPasswordEmailDto;
import com.today.todayproject.global.util.GenerateDummy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EmailServiceTest {

    @Autowired
    EmailService emailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EntityManager em;

    @Test
    void 임시비밀번호_재발급_메일_보내기_기능_테스트() throws MessagingException {
        //given
        User user = GenerateDummy.generateDummyUser("ohk9134@naver.com", "1234", "KSH1",
                "s3://imgUrl1", Role.USER);
        em.persist(user);
        IssueTempPasswordEmailDto issueTempPasswordEmailDto =
                emailService.generateIssueTempPasswordEmailDtoAndChangePassword(user, passwordEncoder);

        //when, then
        assertDoesNotThrow(() -> emailService.sendIssueTempPasswordEmail(issueTempPasswordEmailDto));
    }

    @Test
    void 임시비밀번호_재발급_시_유저_비밀번호_임시_비밀번호로_업데이트_테스트() {
        //given
        User user = GenerateDummy.generateDummyUser("ohk9134@naver.com", passwordEncoder.encode("1234"), "KSH1",
                "s3://imgUrl1", Role.USER);
        em.persist(user);
        boolean beforeUpdateUserPasswordMatchResult = user.matchPassword(passwordEncoder, "1234");

        //when
        emailService.generateIssueTempPasswordEmailDtoAndChangePassword(user, passwordEncoder);
        boolean afterUpdateUserPasswordMatchResult = user.matchPassword(passwordEncoder, "1234");

        //then
        assertThat(beforeUpdateUserPasswordMatchResult).isTrue();
        assertThat(afterUpdateUserPasswordMatchResult).isFalse();
    }

    @Test
    void 회원가입_인증_코드_발급_메일_보내기_기능_테스트() {
        //given
        User user = GenerateDummy.generateDummyUser("ohk9134@naver.com", "1234", "KSH1",
                "s3://imgUrl1", Role.USER);
        em.persist(user);
        AuthenticationCodeEmailSendDto authenticationCodeEmailSendDto =
                emailService.generateAuthenticationCodeEmailDto(user.getEmail(), 123456);

        //when, then
        assertDoesNotThrow(() -> emailService.sendAuthenticationCodeEmail(authenticationCodeEmailSendDto));
    }
}