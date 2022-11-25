package com.morakmorak.morak_back_end.exception;

import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

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

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(MethodArgumentNotValidException e){
        Map<String, String> errors = new HashMap<>();

        e.getAllErrors()
                .forEach(error ->
                    errors.put(((FieldError)error).getField(), error.getDefaultMessage())
                );

        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new ErrorResponseEntity(BAD_REQUEST.value(), BAD_REQUEST.toString(), errors));
    }
}
