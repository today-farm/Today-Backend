package com.today.todayproject.domain.user.service;

import com.today.todayproject.domain.user.dto.UserUpdateRequestDto;
import com.today.todayproject.domain.user.dto.UserWithdrawRequestDto;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    void updateUser(UserUpdateRequestDto userUpdateRequestDto, MultipartFile profileImg) throws Exception;

    void withdraw(UserWithdrawRequestDto userWithdrawRequestDto) throws Exception;
}
