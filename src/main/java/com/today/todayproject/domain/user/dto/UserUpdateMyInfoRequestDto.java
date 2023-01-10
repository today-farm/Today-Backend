package com.today.todayproject.domain.user.dto;

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
public class UserUpdateMyInfoRequestDto {

    @NotBlank(message = "변경할 닉네임을 입력해주세요.", groups = NotBlankGroup.class)
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$", message = "수정할 닉네임은 숫자, 한글, 영어만 가능합니다.")
    @Size(min = 2, max = 8, message = "수정할 닉네임은 2자 이상 8자 이하여야합니다.")
    private String changeNickname;
}
