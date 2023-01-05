package com.today.todayproject.global;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseResponse<T> {

    private final Boolean isSuccess;
    private final String message;
    private final int code;
    private T result;

    // 요청 성공 시 (생성자 파라미터에 데이터 넘어옴)
    public BaseResponse(T result) {
        this.isSuccess = BaseResponseStatus.SUCCESS.isSuccess();
        this.message = BaseResponseStatus.SUCCESS.getMessage();
        this.code = BaseResponseStatus.SUCCESS.getCode();
        this.result = result;
    }

    // 요청 실패 시는 예외가 바로 발생해서 ExceptionAdvice에서 처리
}
