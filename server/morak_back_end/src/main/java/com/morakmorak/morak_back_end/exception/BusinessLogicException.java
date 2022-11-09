package com.morakmorak.morak_back_end.exception;

import lombok.Getter;

@Getter
public class BusinessLogicException extends RuntimeException {
    private ErrorCode errorCode;

    public BusinessLogicException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
