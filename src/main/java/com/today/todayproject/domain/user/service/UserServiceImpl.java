package com.today.todayproject.domain.user.service;

import com.today.todayproject.domain.user.User;
import com.today.todayproject.domain.user.dto.UserUpdateRequestDto;
import com.today.todayproject.domain.user.dto.UserWithdrawRequestDto;
import com.today.todayproject.domain.user.repository.UserRepository;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import com.today.todayproject.global.s3.service.S3UploadService;
import com.today.todayproject.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3UploadService s3UploadService;

    /**
     * 회원 정보 수정 로직
     */
    @Override
    public void updateUser(UserUpdateRequestDto userUpdateRequestDto, MultipartFile profileImg) throws Exception {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));

        String currentNickname = loginUser.getNickname();

        if(userUpdateRequestDto.getChangeNickname() != null) {
            // 기존 닉네임과 변경할 닉네임이 같을 때 예외 처리
            if (currentNickname.equals(userUpdateRequestDto.getChangeNickname())) {
                throw new BaseException(BaseResponseStatus.SAME_NICKNAME);
            }
        }


        if(userUpdateRequestDto.getChangePassword() != null) {
            // 기존 비밀번호와 변경할 비밀번호가 같을 때 예외 처리
            if (loginUser.matchPassword(passwordEncoder, userUpdateRequestDto.getChangePassword())) {
                throw new BaseException(BaseResponseStatus.SAME_CURRENT_CHANGE_PASSWORD);
            }
        }

        if(profileImg != null) {
            // 현재 프로필 사진이 기본이라면 S3 삭제 X, 있을 때만 S3에서 삭제
            if(loginUser.getProfileImgUrl() != null) {
                s3UploadService.deleteOriginalFile(loginUser.getProfileImgUrl());
            }
            String changeProfileImgUrl = s3UploadService.uploadFile(profileImg);
            loginUser.updateProfileImgUrl(changeProfileImgUrl);
        }

        loginUser.updateNickname(userUpdateRequestDto.getChangeNickname());
        loginUser.updatePassword(passwordEncoder, userUpdateRequestDto.getChangePassword());
    }


    /**
     * 회원 탈퇴 로직
     */
    @Override
    public void withdraw(UserWithdrawRequestDto userWithdrawRequestDto) throws Exception {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));

        boolean isSamePassword = loginUser
                .matchPassword(passwordEncoder, userWithdrawRequestDto.getCurrentPassword());
        if(!isSamePassword) {
            throw new BaseException(BaseResponseStatus.WRONG_CURRENT_PASSWORD);
        }
        String deleteProfileImgUrl = loginUser.getProfileImgUrl();
        s3UploadService.deleteOriginalFile(deleteProfileImgUrl);
        userRepository.delete(loginUser);
    }
}
