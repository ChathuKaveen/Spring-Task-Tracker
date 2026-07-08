package com.task_management.Task.Management.controllers;

import com.task_management.Task.Management.dtos.ErrorResponseDto;
import com.task_management.Task.Management.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExisist.class)
    public ResponseEntity<Map<String , String>> UserAlreadyExisistExceptionHandler(){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error" , "User already exist"));
    }

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<Map<String , String>> userNotFoundExceptionHandler(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error" , "User Not Found"));
    }

    @ExceptionHandler(TaskDueDayCantBeforeTodayException.class)
    public ResponseEntity<ErrorResponseDto> TaskDueDayCantBeforeTodayExceptionHandler(TaskDueDayCantBeforeTodayException ex){
        ErrorResponseDto error = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value() , ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> TaskNotFoundExceptionHandler(TaskNotFoundException ex){
        ErrorResponseDto error = new ErrorResponseDto(HttpStatus.NOT_FOUND.value() , ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(NotEnoughPrevilagesException.class)
    public ResponseEntity<ErrorResponseDto> TaskNotFoundExceptionHandler(NotEnoughPrevilagesException ex){
        ErrorResponseDto error = new ErrorResponseDto(HttpStatus.FORBIDDEN.value() , ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }
}
