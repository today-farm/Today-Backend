package com.today.todayproject.domain.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendRequestFriendInfoDto {

    private Long userId;
    private String nickname;
    private String profileImgUrl;
}
