package com.today.todayproject.global.exception;

import com.today.todayproject.global.BaseException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

        return new ResponseEntity(new ExceptionDto(false, exception.getStatus().getCode(),
                exception.getStatus().getMessage())
                , exception.getStatus().getHttpStatus());
    }

    // HTTP Method 잘못 요청 시 발생
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity handleHttpRequestMethodEx(HttpRequestMethodNotSupportedException exception) {
        log.error("HTTP Method 매핑 오류 발생! {}", exception.getMessage());
        return new ResponseEntity(new ExceptionDto(false,
                5000, exception.getMessage()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    // Validation 예외 발생 시
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error("Controller Validation 오류 발생");
        return new ResponseEntity(new ExceptionDto(false,
                5001,
                exception.getBindingResult().getAllErrors().get(0).getDefaultMessage()), HttpStatus.BAD_REQUEST);
    }

    // 위에서 처리하지 않은 모든 예외 발생 시
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleMemberEx(Exception exception) {

        exception.printStackTrace();
        return new ResponseEntity(new ExceptionDto(false,
                10000, exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class ExceptionDto {
        private Boolean isSuccess;
        private int errorCode;
        private String message;
    }
}
