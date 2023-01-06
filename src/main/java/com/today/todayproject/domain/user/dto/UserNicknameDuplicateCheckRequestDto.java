package com.today.todayproject.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNicknameDuplicateCheckRequestDto {

    private String nickname;
}
