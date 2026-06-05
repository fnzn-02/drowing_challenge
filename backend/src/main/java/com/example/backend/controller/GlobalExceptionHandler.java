package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 모든 컨트롤러에서 발생하는 예외를 한 곳에서 처리
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalAccessException e){
        return ResponseEntity.badRequest().body(e.getMessage()); // 에러 메시지를 응답 본문에 담아서 반환
    }
}
