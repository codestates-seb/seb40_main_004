package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorResponseEntity;
import com.morakmorak.morak_back_end.security.exception.InvalidJwtTokenException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponseEntity> handleBusinessLogicException(BusinessLogicException e) {
        return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseEntity> handleValidationExceptions(
            MethodArgumentNotValidException e) {
        return ErrorResponseEntity.toResponseEntity(e);
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<ErrorResponseEntity> handleValidationExceptions(InvalidJwtTokenException e) {
        return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
    }
}
