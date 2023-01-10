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
    SAME_CURRENT_CHANGE_PASSWORD(false, 2006, BAD_REQUEST, "바꿀 비밀번호를 현재 비밀번호와 다르게 설정해주세요."),
    WRONG_FILE_EXTENSION(false, 2007, BAD_REQUEST, "잘못된 형식의 파일입니다."),
    NOT_FOUND_POST(false, 2008, NOT_FOUND, "해당 하루 게시물을 찾을 수 없습니다."),
    NOT_FOUND_POST_QUESTION(false, 2009, NOT_FOUND, "해당 하루 게시물의 질문을 찾을 수 없습니다."),
    NOT_FOUND_FRIEND(false, 2010, NOT_FOUND, "해당 친구를 찾을 수 없습니다."),
    NOT_FOUND_USER(false, 2011, NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    NOT_FOUND_CROP(false, 2012,NOT_FOUND, "해당 작물을 찾을 수 없습니다."),
    POST_CAN_WRITE_ONLY_ONCE_A_DAY(false, 2013, BAD_REQUEST, "하루 작성은 하루에 1번만 가능합니다."),
    NOT_FOUND_IMG(false, 2014, NOT_FOUND, "해당 이미지를 찾을 수 없습니다."),
    NOT_FOUND_VIDEO(false, 2015, NOT_FOUND, "해당 비디오를 찾을 수 없습니다."),
    NOT_ACCESS_FRIEND_LIST(false, 2016, BAD_REQUEST, "친구의 친구 목록은 조회할 수 없습니다."),
    NOT_DUPLICATE_FRIEND_REQUEST(false, 2017, BAD_REQUEST, "친구 요청을 중복해서 보낼 수 없습니다."),
    CANNOT_SEE_POST_NOT_FRIEND_USER(false, 2018, BAD_REQUEST, "친구가 아닌 유저는 해당 하루를 볼 수 없습니다."),
    CANNOT_SEE_POST_NOT_LOGIN_USER(false, 2019, BAD_REQUEST, "자기 자신만 해당 하루를 볼 수 있습니다."),
    NOT_FOUND_USER_FIND_PASSWORD_EMAIL(false, 2020, BAD_REQUEST, "해당 이메일을 가진 유저가 존재하지 않습니다."),
    NOT_EMAIL_AUTHENTICATION_USER_LOGIN(false, 2021, UNAUTHORIZED, "이메일 인증이 필요한 유저입니다."),
    NOT_VALID_AUTH_CODE(false, 2022, BAD_REQUEST, "인증 시간이 만료된 인증 코드이거나 올바르지 않은 인증 코드입니다."),
    NOT_FOUND_UPDATE_USER_INFO(false, 2023, BAD_REQUEST, "유저의 변경할 닉네임을 입력해주세요.");

    private final boolean isSuccess;
    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

}
