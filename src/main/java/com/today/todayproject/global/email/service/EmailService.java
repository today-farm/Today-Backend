package com.today.todayproject.global.email.service;

import com.today.todayproject.domain.user.User;
import com.today.todayproject.global.email.dto.EmailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    private static final int TEMP_PASSWORD_SIZE = 10;

    @Value("${spring.mail.username}")
    private String fromAddress;

    public EmailDto generateEmailDtoAndChangePassword(User user, PasswordEncoder passwordEncoder) {
        String tempPassword = getTempPassword();
        changePassword(user, passwordEncoder, tempPassword);
        String title = user.getNickname() + "님의 하루 농장 임시비밀번호 발급 메일입니다.";
        String content = user.getNickname() + "님의 하루 농장 임시 비밀번호는 " + tempPassword + "입니다";
        String htmlContent = "<img src='cid:haru-img'> <p>" + content + "</p>";
        return new EmailDto(user.getEmail(), title, htmlContent);
    }

    private String getTempPassword() {
        return RandomStringUtils.randomAlphanumeric(TEMP_PASSWORD_SIZE);
    }

    private void changePassword(User user, PasswordEncoder passwordEncoder, String tempPassword) {
        user.updatePassword(passwordEncoder, tempPassword);
    }

    public void sendIssueTempPasswordEmail(EmailDto emailDto) throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setFrom(fromAddress);
        mimeMessageHelper.setTo(emailDto.getUserEmail());
        mimeMessageHelper.setSubject(emailDto.getTitle());
        mimeMessageHelper.setText(emailDto.getContent(), true);
        mimeMessageHelper.addInline("haru-img", new ClassPathResource("email/findpassword/no-think.jpeg"));

        mailSender.send(mimeMessage);
        log.info("임시비밀번호 발급 이메일 전송 완료");
    }
}