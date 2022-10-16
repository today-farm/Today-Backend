package com.today.todayproject.global;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

/**
 * 에러 코드 관리
 */
@Getter
@AllArgsConstructor
public enum BaseResponseStatus {

    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, OK, "요청에 성공하였습니다."),

    /**
     * 2000 : Request 오류
     */
    EXIST_EMAIL(false, 2000, BAD_REQUEST, "이미 존재하는 이메일입니다."),
    EXIST_NICKNAME(false, 2001, BAD_REQUEST, "이미 존재하는 닉네임입니다."),
    NOT_FOUND_LOGIN_USER(false, 2002, NOT_FOUND, "로그인한 유저가 존재하지 않습니다."),
    NOT_FOUND_EMAIL(false, 2003, NOT_FOUND,"해당 이메일이 존재하지 않습니다."),
    SAME_NICKNAME(false, 2004, BAD_REQUEST, "기존 닉네임과 같은 닉네임입니다."),
    WRONG_CURRENT_PASSWORD(false, 2005, BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다."),
    SAME_CURRENT_CHANGE_PASSWORD(false, 2006, BAD_REQUEST, "바꿀 비밀번호를 현재 비밀번호와 다르게 설정해주세요.");

    private final boolean isSuccess;
    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

}
