package com.today.todayproject.global.signup.controller;

import com.today.todayproject.global.BaseResponse;
import com.today.todayproject.global.signup.dto.SignUpRequestDto;
import com.today.todayproject.global.signup.dto.SignUpResponseDto;
import com.today.todayproject.global.signup.service.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpService signUpService;

    @PostMapping("/sign-up")
    public BaseResponse<SignUpResponseDto> signUp(
            @RequestPart(required = false) List<MultipartFile> profileImg,
            @RequestPart SignUpRequestDto signUpRequestDto) throws Exception {
        Long createUserId = signUpService.signUp(signUpRequestDto, profileImg);
        SignUpResponseDto signUpResponseDto = new SignUpResponseDto(createUserId);
        return new BaseResponse<>(signUpResponseDto);
    }
}
