package com.today.todayproject.domain.user.controller;

import com.today.todayproject.domain.user.dto.*;
import com.today.todayproject.domain.user.service.UserService;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponse;
import com.today.todayproject.global.email.dto.AuthenticationCodeEmailConfirmResponseDto;
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
     * 닉네임 중복 체크 API
     */
    @PostMapping("/sign-up/nickname-duplicate-check")
    public BaseResponse<UserNicknameDuplicateCheckResponseDto> nicknameDuplicateCheck(
            @ModelAttribute UserNicknameDuplicateCheckRequestDto userNicknameDuplicateCheckRequestDto
    ) {
        UserNicknameDuplicateCheckResponseDto userNicknameDuplicateCheckResponseDto =
                userService.nicknameDuplicateCheck(userNicknameDuplicateCheckRequestDto);
        return new BaseResponse<>(userNicknameDuplicateCheckResponseDto);
    }

    /**
     * 이메일 인증 코드 전송 API
     */
    @PostMapping("/send-email-auth-code")
    public BaseResponse<String> sendEmailAuthCode(
            @ModelAttribute UserEmailAuthCodeSendDto userEmailAuthCodeSendDto) throws Exception {
        userService.sendAuthenticationCodeEmail(userEmailAuthCodeSendDto);
        return new BaseResponse<>("인증 코드를 담은 메일이 전송되었습니다.");
    }

    /**
     * 이메일 인증 코드 확인 API
     */
    @PostMapping("/confirm-email-auth-code")
    public BaseResponse<AuthenticationCodeEmailConfirmResponseDto> confirmEmailAuthCode(
            @ModelAttribute UserEmailAuthCodeConfirmDto userEmailAuthCodeConfirmDto) throws Exception {
        AuthenticationCodeEmailConfirmResponseDto authenticationCodeEmailConfirmResponseDto =
                userService.confirmEmailAuthCode(userEmailAuthCodeConfirmDto);
        return new BaseResponse<>(authenticationCodeEmailConfirmResponseDto);
    }

    /**
     * 내 정보 수정 API
     */
    @PatchMapping("/user/update-my-info")
    public BaseResponse<String> updateMyInfo(
            @Validated(ValidationSequence.class) @RequestPart(required = false) UserUpdateMyInfoRequestDto userUpdateMyInfoRequestDto,
            @RequestPart(required = false) MultipartFile profileImg) throws Exception {
        userService.updateUser(userUpdateMyInfoRequestDto, profileImg);
        return new BaseResponse<>("내 정보 수정 성공");
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

    /**
     * 내 정보 조회 API
     */
    @GetMapping("/user/find-my-info")
    public BaseResponse<UserGetMyInfoDto> getMyInfo() throws BaseException {
        UserGetMyInfoDto myInfoDto = userService.getMyInfo();
        return new BaseResponse<>(myInfoDto);
    }

    /**
     * 비밀번호 찾기 API
     */
    @PostMapping("/find-password")
    public BaseResponse<String> findPassword(UserFindPasswordDto userFindPasswordDto) throws Exception {
        userService.sendTempPasswordEmail(userFindPasswordDto);
        return new BaseResponse<>("임시 비밀번호를 담은 인증 메일이 전송되었습니다.");
    }
}
