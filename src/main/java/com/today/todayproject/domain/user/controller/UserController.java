package com.today.todayproject.domain.user.controller;

import com.today.todayproject.domain.user.dto.UserNicknameUpdateRequestDto;
import com.today.todayproject.domain.user.service.UserService;
import com.today.todayproject.global.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 닉네임 수정 API
     */
    @PostMapping("/user/update-nickname")
    public BaseResponse<String> nicknameUpdate(
            @RequestBody UserNicknameUpdateRequestDto userNicknameUpdateRequestDto) throws Exception {

        userService.updateNickname(userNicknameUpdateRequestDto);
        return new BaseResponse<String>("닉네임 수정 성공");
    }

}
