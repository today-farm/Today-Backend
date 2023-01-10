package com.today.todayproject.domain.user.dto;

import com.today.todayproject.global.validation.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatePasswordRequestDto {

    @NotBlank(message = "변경할 비밀번호를 입력해주세요.", groups = ValidationGroups.NotBlankGroup.class)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^+\\-=])(?=\\S+$).{8,}$",
            message = "비밀번호는 숫자, 영어, 특수문자가 1개 이상 포함된 8자 이상이어야합니다.", groups = ValidationGroups.PatternGroup.class)
    private String changePassword;
}
