package com.today.todayproject.domain.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetFriendsResponseDto {

    private List<SendRequestFriendInfoDto> sendRequestFriendInfos = new ArrayList<>();
    private List<FriendWithEachOtherInfoDto> friendWithEachOtherInfos = new ArrayList<>();
}
