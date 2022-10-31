package com.today.todayproject.domain.friend.controller;

import com.today.todayproject.domain.friend.service.FriendService;
import com.today.todayproject.global.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/delete/{friendId}")
    public BaseResponse<String> delete(@PathVariable("friendId") Long friendId) throws Exception {
        friendService.delete(friendId);
        return new BaseResponse<>("친구 추가에 성공했습니다.");
    }
}
