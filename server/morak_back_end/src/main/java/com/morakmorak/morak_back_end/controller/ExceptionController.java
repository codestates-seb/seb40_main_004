package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorResponseEntity;
import com.morakmorak.morak_back_end.exception.webHook.ErrorNotificationGenerator;
import com.morakmorak.morak_back_end.security.exception.InvalidJwtTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Arrays;


@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {

    private final ErrorNotificationGenerator errorNotificationGenerator;

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponseEntity> handleBusinessLogicException(BusinessLogicException e) {
        String errorMessage = LocalDateTime.now() + " \nBusinessLogicException : " + e.getErrorCode().getHttpStatus().value() + " " + e.getErrorCode().getMessage();
        errorNotificationGenerator.send(errorMessage);
        return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseEntity> handleValidationExceptions(
            MethodArgumentNotValidException e) {
        String errorMessage = LocalDateTime.now() + "\nMethodArgumentNotValidException : " + e.getMessage();
        errorNotificationGenerator.send(errorMessage);
        return ErrorResponseEntity.toResponseEntity(e);
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<ErrorResponseEntity> handleValidationExceptions(InvalidJwtTokenException e) {
        String errorMessage = LocalDateTime.now() + "\nInvalidJwtTokenException : " + e.getMessage();
        errorNotificationGenerator.send(errorMessage);
        return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponseEntity> missingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        String errorMessage = LocalDateTime.now() + "\nMissingServletRequestParameterException : " + e.getMessage();
        errorNotificationGenerator.send(errorMessage);
        return ErrorResponseEntity.toResponseEntity(e);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ErrorResponseEntity> httpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e
    ) {
        String errorMessage = LocalDateTime.now() + "\nHttpRequestMethodNotSupportedException : " + e.getMessage();
        errorNotificationGenerator.send(errorMessage);
        return ErrorResponseEntity.toResponseEntity(e);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ErrorResponseEntity> constraintViolationException(
            ConstraintViolationException e
    ) {
        String errorMessage = LocalDateTime.now() + "\nConstraintViolationException : " + e.getMessage();
        errorNotificationGenerator.send(errorMessage);
        return ErrorResponseEntity.toResponseEntity(e);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponseEntity> error(Exception e) {
        String errorMessage = LocalDateTime.now() + "\n500 Exception : " + Arrays.stream(e.getStackTrace()).findFirst();
        errorNotificationGenerator.send(errorMessage);
        return ErrorResponseEntity.toResponseEntity(e);
    }
}
