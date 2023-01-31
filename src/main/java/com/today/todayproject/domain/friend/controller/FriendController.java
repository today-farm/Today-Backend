package com.today.todayproject.domain.friend.controller;

import com.today.todayproject.domain.friend.dto.FriendFindRequestResponseDto;
import com.today.todayproject.domain.friend.dto.GetFriendsResponseDto;
import com.today.todayproject.domain.friend.dto.SendRequestFriendInfoDto;
import com.today.todayproject.domain.friend.dto.FriendRequestInfoDto;
import com.today.todayproject.domain.friend.service.FriendService;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friend")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/add/{toUserId}")
    public BaseResponse<String> add(@PathVariable("toUserId") Long toUserId) throws Exception {
        friendService.add(toUserId);
        return new BaseResponse<>("친구 요청을 보냈습니다");
    }

    @PostMapping("/delete/{deleteToUserId}")
    public BaseResponse<String> delete(@PathVariable("deleteToUserId") Long deleteToUserId) throws Exception {
        friendService.delete(deleteToUserId);
        return new BaseResponse<>("친구 삭제에 성공했습니다.");
    }

    @GetMapping("/friends/{fromUserId}")
    public BaseResponse<GetFriendsResponseDto> getFriends(@PathVariable("fromUserId") Long fromUserId) throws BaseException {
        GetFriendsResponseDto getFriendsResponseDto = friendService.getFriends(fromUserId);
        return new BaseResponse<>(getFriendsResponseDto);
    }

    @PostMapping("/accept-request/{toUserId}")
    public BaseResponse<String> acceptRequest(@PathVariable("toUserId") Long toUserId) throws BaseException {
        friendService.acceptFriendRequest(toUserId);
        return new BaseResponse<>("친구 요청을 수락하였습니다.");
    }

    @DeleteMapping("/refuse-request/{toUserId}")
    public BaseResponse<String> refuseRequest(@PathVariable("toUserId") Long toUserId) throws BaseException {
        friendService.refuseFriendRequest(toUserId);
        return new BaseResponse<>("친구 요청을 거절하였습니다.");
    }

    @GetMapping("/find-requested-users")
    public BaseResponse<FriendFindRequestResponseDto> getRequestedFriendUsers() throws BaseException {
        List<FriendRequestInfoDto> requestedFriendUsers = friendService.getRequestedFriendUsers();
        return new BaseResponse<>(new FriendFindRequestResponseDto(requestedFriendUsers));
    }
}
