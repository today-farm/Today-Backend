package com.today.todayproject.domain.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendFindRequestResponseDto {

    private List<ReceiveRequestFriendInfoDto> receiveRequestFriendInfos = new ArrayList<>();
}
