package com.today.todayproject.global.email.service;

import com.today.todayproject.domain.user.User;
import com.today.todayproject.global.email.dto.AuthenticationCodeEmailDto;
import com.today.todayproject.global.email.dto.IssueTempPasswordEmailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    private static final int TEMP_PASSWORD_SIZE = 10;

    @Value("${spring.mail.username}")
    private String fromAddress;

    public IssueTempPasswordEmailDto generateIssueTempPasswordEmailDtoAndChangePassword(
            User user, PasswordEncoder passwordEncoder) {
        String tempPassword = getTempPassword();
        changePassword(user, passwordEncoder, tempPassword);
        String title = user.getNickname() + "님의 하루 농장 임시비밀번호 발급 메일입니다.";
        String content = user.getNickname() + "님의 하루 농장 임시 비밀번호는 " + tempPassword + "입니다.";
        String htmlContent = "<img src='cid:haru-img'> <p>" + content + "</p>";
        return new IssueTempPasswordEmailDto(user.getEmail(), title, htmlContent);
    }

    private String getTempPassword() {
        return RandomStringUtils.randomAlphanumeric(TEMP_PASSWORD_SIZE);
    }

    private void changePassword(User user, PasswordEncoder passwordEncoder, String tempPassword) {
        user.updatePassword(passwordEncoder, tempPassword);
    }

    public void sendIssueTempPasswordEmail(IssueTempPasswordEmailDto issueTempPasswordEmailDto) throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setFrom(fromAddress);
        mimeMessageHelper.setTo(issueTempPasswordEmailDto.getUserEmail());
        mimeMessageHelper.setSubject(issueTempPasswordEmailDto.getTitle());
        mimeMessageHelper.setText(issueTempPasswordEmailDto.getContent(), true);
        mimeMessageHelper.addInline("haru-img", new ClassPathResource("email/findpassword/no-think.jpeg"));

        mailSender.send(mimeMessage);
        log.info("임시비밀번호 발급 이메일 전송 완료");
    }

    public AuthenticationCodeEmailDto generateAuthenticationCodeEmailDto(String userEmail, int authCode) {
        String title = "하루 농장 회원가입 인증 코드 발급 메일입니다.";
        String content = "하루 농장 회원가입 인증 코드는 " + authCode + "입니다.";
        String htmlContent = "<img src='cid:haru-img'> <p>" + content + "</p>";

        return new AuthenticationCodeEmailDto(userEmail, title, htmlContent);
    }

    public void sendAuthenticationCodeEmail(
            AuthenticationCodeEmailDto authenticationCodeEmailDto) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setFrom(fromAddress);
        mimeMessageHelper.setTo(authenticationCodeEmailDto.getUserEmail());
        mimeMessageHelper.setSubject(authenticationCodeEmailDto.getTitle());
        mimeMessageHelper.setText(authenticationCodeEmailDto.getContent(), true);
        mimeMessageHelper.addInline("haru-img", new ClassPathResource("email/findpassword/no-think.jpeg"));

        mailSender.send(mimeMessage);
        log.info("회원가입 인증 코드 발급 이메일 전송 완료");
    }

}