package com.today.todayproject.domain.user.service;

import com.today.todayproject.domain.user.User;
import com.today.todayproject.domain.user.dto.UserNicknameUpdateRequestDto;
import com.today.todayproject.domain.user.repository.UserRepository;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import com.today.todayproject.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * 닉네임 수정 로직
     */
    @Override
    public void updateNickname(UserNicknameUpdateRequestDto userNicknameUpdateRequestDto) throws Exception {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));

        String originalNickname = loginUser.getNickname();
        String changeNickname = userNicknameUpdateRequestDto.getChangeNickname();

        // 기존 닉네임과 변경할 닉네임이 같을 때 예외 처리
        if(originalNickname == changeNickname) {
            throw new BaseException(BaseResponseStatus.SAME_NICKNAME);
        }

        loginUser.updateNickname(userNicknameUpdateRequestDto.getChangeNickname());
    }


}
