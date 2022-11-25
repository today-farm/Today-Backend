package com.today.todayproject.domain.user.service;

import com.today.todayproject.domain.user.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    void updateUser(UserUpdateRequestDto userUpdateRequestDto, MultipartFile profileImg) throws Exception;

    void withdraw(UserWithdrawRequestDto userWithdrawRequestDto) throws Exception;

    UserGetPagingDto searchUsers(Pageable pageable, UserSearchDto userSearchDto);
}
