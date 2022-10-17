package com.today.todayproject.domain.user.controller;

import com.today.todayproject.domain.user.dto.UserNicknameUpdateRequestDto;
import com.today.todayproject.domain.user.dto.UserPasswordUpdateRequestDto;
import com.today.todayproject.domain.user.dto.UserWithdrawRequestDto;
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
        return new BaseResponse<>("닉네임 수정 성공");
    }

    /**
     * 비밀번호 수정 API
     */
    @PostMapping("/user/update-password")
    public BaseResponse<String> passwordUpdate(
            @RequestBody UserPasswordUpdateRequestDto userPasswordUpdateRequestDto) throws Exception {

        userService.updatePassword(userPasswordUpdateRequestDto);
        return new BaseResponse<>("비밀번호 수정 성공");
    }

    /**
     * 회원 탈퇴 API
     */
    @PostMapping("/user/withdraw")
    public BaseResponse<String> withdraw(
            @RequestBody UserWithdrawRequestDto userWithdrawRequestDto) throws Exception {
        userService.withdraw(userWithdrawRequestDto);
        return new BaseResponse<>("회원 탈퇴 성공");
    }
}
