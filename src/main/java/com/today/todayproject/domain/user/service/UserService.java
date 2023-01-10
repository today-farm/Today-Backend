package com.today.todayproject.domain.user.service;

import com.today.todayproject.domain.user.dto.*;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.email.dto.AuthenticationCodeEmailConfirmResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    Long signUp(UserSignUpRequestDto userSignUpRequestDto, MultipartFile profileImg) throws Exception;

    UserNicknameDuplicateCheckResponseDto nicknameDuplicateCheck(
            UserNicknameDuplicateCheckRequestDto userNicknameDuplicateCheckRequestDto);

    void sendAuthenticationCodeEmail(UserEmailAuthCodeSendDto userEmailAuthCodeSendDto) throws Exception;

    AuthenticationCodeEmailConfirmResponseDto confirmEmailAuthCode(UserEmailAuthCodeConfirmDto userEmailAuthCodeConfirmDto) throws Exception;

    void updateUser(UserUpdateMyInfoRequestDto userUpdateMyInfoRequestDto, MultipartFile profileImg) throws Exception;

    void withdraw(UserWithdrawRequestDto userWithdrawRequestDto) throws Exception;

    UserGetPagingDto searchUsers(Pageable pageable, UserSearchDto userSearchDto);

    UserGetMyInfoDto getMyInfo() throws BaseException;

    void sendTempPasswordEmail(UserFindPasswordDto userFindPasswordDto) throws Exception;

    void initPostWriteCount();

    void initThisMonthHarvestCount();
}
