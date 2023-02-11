package com.spring.board.controller;

import com.spring.board.exception.Exception;
import com.spring.board.response.error.BindErrorResponse;
import com.spring.board.response.error.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception(Exception e) {
        int statusCode = e.getStatus();

        ErrorResponse body = ErrorResponse.builder()
                .code(String.valueOf(statusCode))
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(statusCode)
                .body(body);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(BAD_REQUEST)
    public BindErrorResponse BindException(BindException bindException) {
        List<FieldError> errors = bindException.getFieldErrors();

        return BindErrorResponse.builder()
                .message(errors.stream()
                        .map(e -> e.getDefaultMessage())
                        .collect(Collectors.toList()))
                .build();
    }
}
