package com.today.todayproject.global.email.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationCodeEmailConfirmResponseDto {

    private Boolean authSuccess;
}
