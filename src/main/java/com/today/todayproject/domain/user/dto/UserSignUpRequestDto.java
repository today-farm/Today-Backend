package com.today.todayproject.domain.user.dto;

import com.today.todayproject.global.validation.ValidationGroups;
import com.today.todayproject.global.validation.ValidationGroups.NotBlankGroup;
import com.today.todayproject.global.validation.ValidationGroups.PatternGroup;
import com.today.todayproject.global.validation.ValidationGroups.SizeGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpRequestDto {

    @NotBlank(message = "이메일을 입력해주세요.", groups = NotBlankGroup.class)
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]+$",
            message = "이메일 형식에 맞게 입력해주세요.", groups = PatternGroup.class)
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.", groups = NotBlankGroup.class)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^+\\-=])(?=\\S+$).*$",
            message = "비밀번호는 숫자, 영어, 특수문자가 1개 이상 포함된 8자 이상이어야합니다.", groups = PatternGroup.class)
    private String password;

    @NotBlank(message = "닉네임을 입력해주세요.", groups = NotBlankGroup.class)
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$", message = "닉네임은 숫자, 한글, 영어만 가능합니다.",
    groups = PatternGroup.class)
    @Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하여야합니다.",groups = SizeGroup.class)
    private String nickname;
}
