package com.today.todayproject.domain.user.dto;

import com.today.todayproject.global.validation.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.today.todayproject.global.validation.ValidationGroups.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto {

    @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$", message = "수정할 닉네임은 숫자, 한글, 영어만 가능합니다.")
    @Size(min = 2, max = 8, message = "수정할 닉네임은 2자 이상 8자 이하여야합니다.")
    private String changeNickname;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^+\\-=])(?=\\S+$).{8,}$",
            message = "비밀번호는 숫자, 영어, 특수문자가 1개 이상 포함된 8자 이상이어야합니다.", groups = PatternGroup.class)
    private String changePassword;
}
