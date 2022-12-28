package com.today.todayproject.global.email.repository;

import com.today.todayproject.global.email.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long>, CustomEmailAuthRepository {
}
