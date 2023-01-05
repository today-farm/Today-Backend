package com.today.todayproject.domain.user.service;

import com.today.todayproject.domain.user.User;
import com.today.todayproject.domain.user.dto.*;
import com.today.todayproject.global.email.dto.AuthenticationCodeEmailConfirmResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    Long signUp(UserSignUpRequestDto userSignUpRequestDto, MultipartFile profileImg) throws Exception;

    void sendAuthenticationCodeEmail(UserEmailAuthCodeDto userEmailAuthCodeDto) throws Exception;

    AuthenticationCodeEmailConfirmResponseDto confirmEmailAuthCode(UserEmailAuthCodeDto userEmailAuthCodeDto) throws Exception;

    void updateUser(UserUpdateRequestDto userUpdateRequestDto, MultipartFile profileImg) throws Exception;

    void withdraw(UserWithdrawRequestDto userWithdrawRequestDto) throws Exception;

    UserGetPagingDto searchUsers(Pageable pageable, UserSearchDto userSearchDto);

    void sendTempPasswordEmail(UserFindPasswordDto userFindPasswordDto) throws Exception;

    void initPostWriteCount();

    void initThisMonthHarvestCount();
}
