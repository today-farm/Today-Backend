package com.today.todayproject.global.signup.service;

import com.today.todayproject.domain.user.Role;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.domain.user.repository.UserRepository;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import com.today.todayproject.global.s3.service.S3UploadService;
import com.today.todayproject.global.signup.dto.SignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3UploadService s3UploadService;

    public Long signUp(SignUpRequestDto signUpRequestDto, MultipartFile profileImg) throws Exception {

        if(userRepository.findByEmail(signUpRequestDto.getEmail()).isPresent()) {
            throw new BaseException(BaseResponseStatus.EXIST_EMAIL);
        }

        if(userRepository.findByNickname(signUpRequestDto.getNickname()).isPresent()) {
            throw new BaseException(BaseResponseStatus.EXIST_NICKNAME);
        }

        // profile 사진이 있다면, User build 시 profile도 추가
        if(!profileImg.isEmpty()) {
            String profileImgUrl = s3UploadService.uploadFile(profileImg);

            User user = User.builder()
                    .email(signUpRequestDto.getEmail())
                    .password(signUpRequestDto.getPassword())
                    .nickname(signUpRequestDto.getNickname())
                    .profileImgUrl(profileImgUrl)
                    .role(Role.USER)
                    .build();

            user.encodePassword(passwordEncoder);
            User saveUser = userRepository.save(user);
            return saveUser.getId();
        } else {
        // profile 사진이 없다면, User build 시 profile null로 추가
            User user = User.builder()
                    .email(signUpRequestDto.getEmail())
                    .password(signUpRequestDto.getPassword())
                    .nickname(signUpRequestDto.getNickname())
                    .profileImgUrl(null)
                    .role(Role.USER)
                    .build();

            user.encodePassword(passwordEncoder);
            User saveUser = userRepository.save(user);
            return saveUser.getId();
        }
    }
}
