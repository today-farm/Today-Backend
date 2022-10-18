package com.today.todayproject.domain.user.controller;

import com.today.todayproject.domain.user.dto.UserUpdateRequestDto;
import com.today.todayproject.domain.user.dto.UserWithdrawRequestDto;
import com.today.todayproject.domain.user.service.UserService;
import com.today.todayproject.global.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원 정보 수정 API
     */
    @PatchMapping("/user/update")
    public BaseResponse<String> update(
            @RequestPart(required = false) UserUpdateRequestDto userUpdateRequestDto,
            @RequestPart(required = false) MultipartFile profileImg) throws Exception {
        userService.updateUser(userUpdateRequestDto, profileImg);
        return new BaseResponse<>("회원 수정 성공");
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
