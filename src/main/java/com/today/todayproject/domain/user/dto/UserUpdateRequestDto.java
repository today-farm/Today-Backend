package com.today.todayproject.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto {

    @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$", message = "수정할 닉네임은 숫자, 한글, 영어만 가능합니다.")
    @Size(min = 2, max = 10, message = "수정할 닉네임은 2자 이상 10자 이하여야합니다.")
    private String changeNickname;

    @Size(min = 4, max = 12, message = "수정할 비밀번호는 4자 이상 12자 이하여야합니다.")
    private String changePassword;
}
