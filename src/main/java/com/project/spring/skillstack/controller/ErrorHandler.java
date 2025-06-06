package com.project.spring.skillstack.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandler {
    
    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception e) {
        return "Error";
    }

}
