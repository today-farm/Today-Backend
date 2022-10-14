package com.today.todayproject.domain.user.service;

import com.today.todayproject.domain.user.dto.UserNicknameUpdateRequestDto;

public interface UserService {

    void updateNickname(UserNicknameUpdateRequestDto userNicknameUpdateRequestDto) throws Exception;
}
