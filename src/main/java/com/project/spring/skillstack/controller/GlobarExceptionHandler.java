package com.project.spring.skillstack.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobarExceptionHandler {
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<String> handleOptimisticLockingException(ObjectOptimisticLockingFailureException ex) {
        // 예외 발생 시 공통 응답 처리
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Data was updated or deleted by another transaction.");
    }
}
