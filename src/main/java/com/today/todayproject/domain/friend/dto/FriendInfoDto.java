package com.today.todayproject.domain.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendInfoDto {

    private Long userId;
    private String nickname;
    private String profileImgUrl;
    private String recentFeeling;

    private Boolean isFriend;
}
