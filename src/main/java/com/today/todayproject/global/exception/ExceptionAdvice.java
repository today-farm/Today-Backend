package com.today.todayproject.global.exception;

import com.today.todayproject.global.BaseException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    // 커스텀 예외 발생
    @ExceptionHandler(BaseException.class)
    public ResponseEntity handleBaseEx(BaseException exception){
        log.error("BaseException errorMessage(): {}", exception.getStatus().getMessage());
        log.error("BaseException errorCode(): {}", exception.getStatus().getCode());

        return new ResponseEntity(new ExceptionDto(exception.getStatus().getCode(), exception.getStatus().getMessage())
                , exception.getStatus().getHttpStatus());
    }

    // HTTP Method 잘못 요청 시 발생
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity handleHttpRequestMethodEx(HttpRequestMethodNotSupportedException exception) {
        log.error("HTTP Method 매핑 오류 발생! {}", exception.getMessage());
        return new ResponseEntity(new ExceptionDto(5000, exception.getMessage()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    // 위에서 처리하지 않은 모든 예외 발생 시
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleMemberEx(Exception exception) {

        exception.printStackTrace();
        return new ResponseEntity(new ExceptionDto(10000, exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class ExceptionDto {
        private int errorCode;
        private String message;
    }
}
