package com.morakmorak.morak_back_end.exception;

import io.jsonwebtoken.io.IOException;

public class EmbeddedRedisServerException extends IOException {
    public EmbeddedRedisServerException(String msg) {
        super(msg);
    }

    public EmbeddedRedisServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
