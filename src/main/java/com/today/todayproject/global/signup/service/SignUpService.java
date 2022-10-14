package com.today.todayproject.global.signup.service;

import com.today.todayproject.domain.user.Role;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.domain.user.repository.UserRepository;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import com.today.todayproject.global.signup.dto.SignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long signUp(SignUpRequestDto signUpRequestDto) throws Exception {
        User user = User.builder()
                .email(signUpRequestDto.getEmail())
                .password(signUpRequestDto.getPassword())
                .nickname(signUpRequestDto.getNickname())
                .profileImgUrl(signUpRequestDto.getProfileImgUrl())
                .role(Role.USER)
                .build();

        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BaseException(BaseResponseStatus.EXIST_EMAIL);
        }

        if(userRepository.findByNickname(user.getNickname()).isPresent()) {
            throw new BaseException(BaseResponseStatus.EXIST_NICKNAME);
        }

        user.encodePassword(passwordEncoder);

        User saveUser = userRepository.save(user);
        return saveUser.getId();
    }
}
