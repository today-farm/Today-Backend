package com.today.todayproject.domain.friend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FriendSaveDto {

    private String nickname;
    private String profileImgUrl;
    private String recentFeeling;
}
