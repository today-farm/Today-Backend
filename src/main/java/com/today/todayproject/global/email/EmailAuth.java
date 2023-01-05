package com.today.todayproject.global.email;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class EmailAuth {

    private static final Long MAX_EXPIRE_TIME = 5L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private int authCode;
    private Boolean expired;
    private LocalDateTime expireDate;

    @Builder
    public EmailAuth(String email, int authCode, Boolean expired) {
        this.email = email;
        this.authCode = authCode;
        this.expired = expired;
        this.expireDate = LocalDateTime.now().plusMinutes(MAX_EXPIRE_TIME);
    }
}
