package com.today.todayproject.global;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
@AllArgsConstructor
public enum BaseResponseStatus {

    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),

    /**
     * 2000 : Request 오류
     */
    //sign-up
    EXIST_EMAIL(false, 2000, "이미 존재하는 이메일입니다.");


    private final boolean isSuccess;
    private final int code;
    private final String message;

}
