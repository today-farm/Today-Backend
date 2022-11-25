package com.today.todayproject.domain.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSearchDto {

    private Long loginUserId;
    private Long lastFriendUserId;
    private Long lastUserId;
    private String searchUserNickname;
}
