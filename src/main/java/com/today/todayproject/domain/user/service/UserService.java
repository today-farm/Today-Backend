package com.today.todayproject.domain.user.service;

import com.today.todayproject.domain.user.dto.*;
import com.today.todayproject.global.email.dto.AuthenticationCodeEmailResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    Long signUp(UserSignUpRequestDto userSignUpRequestDto, MultipartFile profileImg) throws Exception;

    AuthenticationCodeEmailResponseDto confirmEmailAuthCode(UserEmailAuthCodeDto userEmailAuthCodeDto) throws Exception;

    void updateUser(UserUpdateRequestDto userUpdateRequestDto, MultipartFile profileImg) throws Exception;

    void withdraw(UserWithdrawRequestDto userWithdrawRequestDto) throws Exception;

    UserGetPagingDto searchUsers(Pageable pageable, UserSearchDto userSearchDto);

    void sendTempPasswordEmail(UserFindPasswordDto userFindPasswordDto) throws Exception;

    void initPostWriteCount();

    void initThisMonthHarvestCount();
}
