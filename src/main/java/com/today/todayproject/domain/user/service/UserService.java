package com.today.todayproject.domain.user.service;

import com.today.todayproject.domain.user.dto.UserNicknameUpdateRequestDto;
import com.today.todayproject.domain.user.dto.UserPasswordUpdateRequestDto;
import com.today.todayproject.domain.user.dto.UserWithdrawRequestDto;

public interface UserService {

    void updateNickname(UserNicknameUpdateRequestDto userNicknameUpdateRequestDto) throws Exception;

    void updatePassword(UserPasswordUpdateRequestDto userPasswordUpdateRequestDto) throws Exception;

    void withdraw(UserWithdrawRequestDto userWithdrawRequestDto) throws Exception;
}
