package com.today.todayproject.domain.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateRequestDto {

    private String changeNickname;
    private String changePassword;
}
