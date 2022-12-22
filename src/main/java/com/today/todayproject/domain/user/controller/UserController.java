package com.today.todayproject.domain.user.controller;

import com.today.todayproject.domain.crop.dto.ThisMonthUserCropDto;
import com.today.todayproject.domain.user.dto.*;
import com.today.todayproject.domain.user.service.UserService;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponse;
import com.today.todayproject.global.validation.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원 가입 API
     */
    @PostMapping("/sign-up")
    public BaseResponse<UserSignUpResponseDto> signUp(
            @RequestPart(required = false) MultipartFile profileImg,
            @Validated(ValidationSequence.class) @RequestPart UserSignUpRequestDto userSignUpRequestDto) throws Exception {
        Long createUserId = userService.signUp(userSignUpRequestDto, profileImg);
        UserSignUpResponseDto userSignUpResponseDto = new UserSignUpResponseDto(createUserId);
        return new BaseResponse<>(userSignUpResponseDto);
    }

    /**
     * 회원 정보 수정 API
     */
    @PatchMapping("/user/update")
    public BaseResponse<String> update(
            @Validated(ValidationSequence.class) @RequestPart(required = false) UserUpdateRequestDto userUpdateRequestDto,
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

    /**
     * 유저 검색 API
     */
    @GetMapping("/user/search")
    public BaseResponse<UserGetPagingDto> searchUsers(@RequestParam(name = "loginUserId") Long loginUserId,
                                                      @RequestParam(required = false, name = "lastFriendUserId") Long lastFriendUserId,
                                                      @RequestParam(required = false, name = "lastUserId") Long lastUserId,
                                                      @RequestParam("searchUserNickname") String searchUserNickname,
                                                      Pageable pageable) {
        UserSearchDto userSearchDto = new UserSearchDto(loginUserId, lastFriendUserId, lastUserId, searchUserNickname);
        UserGetPagingDto userGetPagingDto = userService.searchUsers(pageable, userSearchDto);
        return new BaseResponse<>(userGetPagingDto);
    }
}
