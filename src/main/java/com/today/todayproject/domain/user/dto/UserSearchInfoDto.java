package com.today.todayproject.domain.user.dto;

import com.today.todayproject.domain.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSearchInfoDto {

    private Long userId;
    private String nickname;
    private String profileImgUrl;
    private int cropCount;

    public UserSearchInfoDto(User user, int cropCount) {
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.profileImgUrl = user.getProfileImgUrl();
        this.cropCount = cropCount;
    }
}
