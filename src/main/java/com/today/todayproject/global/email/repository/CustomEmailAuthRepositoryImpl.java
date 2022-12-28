package com.today.todayproject.global.email.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.today.todayproject.global.email.EmailAuth;
import com.today.todayproject.global.email.QEmailAuth;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class CustomEmailAuthRepositoryImpl implements CustomEmailAuthRepository {

    private final JPAQueryFactory query;

    public CustomEmailAuthRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public Optional<EmailAuth> findValidAuthByEmail(String email, int authCode, LocalDateTime currentTime) {
        EmailAuth emailAuth = query.selectFrom(QEmailAuth.emailAuth)
                .where(QEmailAuth.emailAuth.email.eq(email),
                        QEmailAuth.emailAuth.authCode.eq(authCode),
                        QEmailAuth.emailAuth.expireDate.goe(currentTime),
                        QEmailAuth.emailAuth.expired.eq(false))
                .fetchFirst();

        return Optional.ofNullable(emailAuth);
    }
}
