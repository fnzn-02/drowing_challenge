package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 모든 컨트롤러에서 발생하는 예외를 한 곳에서 처리
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 서비스 레이어에서 throw new IllegalArgumentException("...")으로 던지는 예외를 처리.
     * 유효하지 않은 요청(중복 제출, 미존재 데이터, 권한 없음 등)에서 발생하며
     * 400 Bad Request 응답으로 에러 메시지를 반환한다.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
