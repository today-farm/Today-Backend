package com.today.todayproject.domain.user.dto;

import com.today.todayproject.domain.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;

@Data
@NoArgsConstructor
public class UserGetFriendUserInfoDto {

    private List<UserSearchInfoDto> friendUserInfos;
    private Boolean hasNext;

    public UserGetFriendUserInfoDto(List<UserSearchInfoDto> friendUserInfos, Slice<User> searchFriendUsers) {
        this.friendUserInfos = friendUserInfos;
        this.hasNext = searchFriendUsers.hasNext();
    }
}
