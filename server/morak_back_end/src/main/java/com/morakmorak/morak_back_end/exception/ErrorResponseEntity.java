package com.morakmorak.morak_back_end.exception;

import lombok.Getter;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
public class ErrorResponseEntity {
    private int status;
    private String code;
    private String message;
    private Map<String, String> validation;

    private ErrorResponseEntity(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    private ErrorResponseEntity(int status, String code, String message, Map<String, String> validation) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.validation = validation;
    }

    private ErrorResponseEntity(int status, String code, Map<String, String> validation) {
        this.status = status;
        this.code = code;
        this.validation = validation;
    }

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ErrorCode e) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(new ErrorResponseEntity(e.getHttpStatus().value(), e.name(), e.getMessage()));
    }

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getAllErrors()
                .forEach(error ->
                        errors.put(((FieldError) error).getField(), error.getDefaultMessage())
                );

        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new ErrorResponseEntity(BAD_REQUEST.value(), BAD_REQUEST.toString(), errors));
    }

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(MissingServletRequestParameterException e) {
        Map<String, String> errors = new HashMap<>();
        errors.put(e.getParameterType(), e.getParameterName());

        ErrorResponseEntity responseException = new ErrorResponseEntity(400, "BadRequest", e.getMessage(), errors);
        return new ResponseEntity<ErrorResponseEntity>(responseException, BAD_REQUEST);
    }

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(
            HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException
    ) {
        Map<String, String> errorData = new HashMap<>();
        String message = httpRequestMethodNotSupportedException.getMessage();

        httpRequestMethodNotSupportedException.getSupportedHttpMethods().forEach(error ->
                errorData.put(error.name(), "사용가능"));


        ErrorResponseEntity responseException = new ErrorResponseEntity(400, "BadRequest", message, errorData);

        return new ResponseEntity<ErrorResponseEntity>(responseException, BAD_REQUEST);
    }

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ConstraintViolationException constraintViolationException) {
        Map<String, String> errorData = new HashMap<>();
        String message = "Please check out parameters";
        constraintViolationException.getConstraintViolations()
                .forEach(error -> {
                    String errorPath = String.valueOf(error.getPropertyPath());
                    int index = errorPath.lastIndexOf(".") + 1;
                    String errorName = errorPath.substring(index);
                    errorData.put(errorName, error.getMessage());
                });
        ErrorResponseEntity responseException = new ErrorResponseEntity(400, "BadRequest", message, errorData);

        return new ResponseEntity<ErrorResponseEntity>(responseException, BAD_REQUEST);
    }

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(Exception e) {
        String message = "Please tell this Error to Server";
        return new ResponseEntity<ErrorResponseEntity>(new ErrorResponseEntity(500, "ServerError", message), INTERNAL_SERVER_ERROR);
    }
}
