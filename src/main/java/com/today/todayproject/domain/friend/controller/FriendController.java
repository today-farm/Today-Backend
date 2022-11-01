package com.today.todayproject.domain.friend.controller;

import com.today.todayproject.domain.friend.dto.FriendGetFriendsResponseDto;
import com.today.todayproject.domain.friend.dto.FriendInfoDto;
import com.today.todayproject.domain.friend.service.FriendService;
import com.today.todayproject.global.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friend")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/add/{friendId}")
    public BaseResponse<String> add(@PathVariable("friendId") Long friendId) throws Exception {
        friendService.add(friendId);
        return new BaseResponse<>("친구 추가에 성공했습니다.");
    }

    @PostMapping("/delete/{deleteFriendUserId}")
    public BaseResponse<String> delete(@PathVariable("deleteFriendUserId") Long deleteFriendUserId) throws Exception {
        friendService.delete(deleteFriendUserId);
        return new BaseResponse<>("친구 삭제에 성공했습니다.");
    }

    @GetMapping("/friends/{friendOwnerId}")
    public BaseResponse<FriendGetFriendsResponseDto> getFriends(@PathVariable("friendOwnerId") Long friendOwnerId) {
        List<FriendInfoDto> friendInfoDtos = friendService.getFriends(friendOwnerId);
        return new BaseResponse<>(new FriendGetFriendsResponseDto(friendInfoDtos));
    }
}
