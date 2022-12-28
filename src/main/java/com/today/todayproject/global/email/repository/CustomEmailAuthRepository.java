package com.today.todayproject.global.email.repository;

import com.today.todayproject.global.email.EmailAuth;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CustomEmailAuthRepository {
    Optional<EmailAuth> findValidAuthByEmail(String email, int authCode, LocalDateTime currentTime);
}
