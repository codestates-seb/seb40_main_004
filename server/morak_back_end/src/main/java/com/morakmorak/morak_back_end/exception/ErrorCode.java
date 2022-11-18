package com.morakmorak.morak_back_end.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(NOT_FOUND, "USER_NOT_FOUND"),
    FILE_NOT_FOUND(NOT_FOUND, "FILE_NOT_FOUND"),
    TAG_NOT_FOUND(NOT_FOUND, "TAG_NOT_FOUND"),
    ARTICLE_NOT_FOUND(NOT_FOUND, "ARTICLE_NOT_FOUND"),
    CATEGORY_NOT_FOUND(NOT_FOUND, "CATEGORY_NOT_FOUND"),
    INVALID_USER(UNAUTHORIZED, "INVALID_USER"),
    EMAIL_EXISTS(CONFLICT, "EMAIL_EXISTS"),

    EMBEDDED_REDIS_EXCEPTION(INTERNAL_SERVER_ERROR, "redis server error"),
    CAN_NOT_EXECUTE_GREP(INTERNAL_SERVER_ERROR, "can not execute grep process command"),
    CAN_NOT_EXECUTE_REDIS_SERVER(INTERNAL_SERVER_ERROR, "can not execute redis server"),
    NOT_FOUND_AVAILABLE_PORT(INTERNAL_SERVER_ERROR, "not found available port"),

    UNABLE_TO_GENERATE_TOKEN(INTERNAL_SERVER_ERROR, "Unable to generate token. Contact your administrator."),
    AUTH_KEY_ALREADY_EXISTS(CONFLICT, "auth key already exists, you can only request once every 5 minutes"),
    INVALID_AUTH_KEY(NOT_FOUND, "invalid auth key, check your email"),

    TOKEN_NOT_FOUND(NOT_FOUND, "TOKEN_NOT_FOUND"),

    NICKNAME_EXISTS(CONFLICT, "nickname exists"),
    MISMATCHED_PASSWORD(CONFLICT, "mismatched password, check your original password"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "This request requires admin confirmation."),
    CANNOT_ACCESS_COMMENT(CONFLICT,"unable to access comment"),
    UNABLE_TO_ANSWER(CONFLICT,"unable to post answer, article is already closed"),
    COMMENT_NOT_FOUND(NOT_FOUND,"comment not found"),
    ANSWER_NOT_FOUND(NOT_FOUND,"answer not found"),

    /*
    * The error code expresses the dto validation exception of spring framework in web mvc test.
    * @author : YangEunChan
    * */
    ONLY_TEST_CODE(HttpStatus.BAD_REQUEST, "BAD_REQUEST"),

    /*
    * It is a constant for JWT Exception only.
    * The constants below should not be used in other exceptional situations.
    * @author : YangEunChan
    * @see : JwtParser, JwtTokenUtil
    * */
    SIGNATURE_EXCEPTION(UNAUTHORIZED, "signature key is different"),
    EXPIRED_EXCEPTION(UNAUTHORIZED, "expired token"),
    MALFORMED_EXCEPTION(UNAUTHORIZED, "malformed token"),
    ILLEGAL_ARGUMENTS_EXCEPTION(UNAUTHORIZED, "using illegal argument like null"),
    UNSUPPORTED_EXCEPTION(UNAUTHORIZED, "unsupported token");


    private final HttpStatus httpStatus;
    private final String message;
}
