package com.today.todayproject.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGetMyInfoDto {

    private String email;
    private String nickname;
    private String profileImgUrl;
}
