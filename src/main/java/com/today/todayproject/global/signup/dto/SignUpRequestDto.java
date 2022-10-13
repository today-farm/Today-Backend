package com.today.todayproject.global.signup.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignUpRequestDto {

    private String email;
    private String password;
}
