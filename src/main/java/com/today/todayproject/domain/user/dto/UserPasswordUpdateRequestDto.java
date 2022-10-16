package com.today.todayproject.domain.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserPasswordUpdateRequestDto {

    private String currentPassword;
    private String changePassword;
}
