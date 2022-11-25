package com.today.todayproject.domain.user.dto;

import com.today.todayproject.domain.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;

@Data
@NoArgsConstructor
public class UserGetUserInfoDto {

    private List<UserSearchInfoDto> userInfos;
    private Boolean hasNext;

    public UserGetUserInfoDto(List<UserSearchInfoDto> userInfos, Slice<User> searchUsers) {
        this.userInfos = userInfos;
        this.hasNext = searchUsers.hasNext();
    }

    public UserGetUserInfoDto(List<UserSearchInfoDto> empty) {
        this.userInfos = empty;
    }
}
