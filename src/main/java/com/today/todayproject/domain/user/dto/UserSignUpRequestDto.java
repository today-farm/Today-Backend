package com.today.todayproject.domain.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSignUpRequestDto {

    private String email;
    private String password;
    private String nickname;
}
