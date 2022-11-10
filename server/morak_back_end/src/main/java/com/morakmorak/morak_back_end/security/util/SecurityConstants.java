package com.morakmorak.morak_back_end.security.util;

public class SecurityConstants {
    public static final String JWT_HEADER = "Authorization";
    public static final String REFRESH_HEADER = "RefreshToken";
    public static final String JWT_PREFIX = "Bearer ";
    public final static Long ACCESS_TOKEN_EXPIRE_COUNT = 60000L*10*3; // 30 minutes
    public final static Long REFRESH_TOKEN_EXPIRE_COUNT = 7 * 24 * 60 * 60 * 1000L; // 7 days
    public final static String ROLES = "roles";
    public final static String SEQUENCE = "userId";
    public final static String ID = "id";
    public final static String EMAIL = "email";

    public final static String SIGNATURE_EXCEPTION = "signature key is different";
    public final static String EXPIRED_EXCEPTION = "expired token";
    public final static String MALFORMED_EXCEPTION = "malformed token";
    public final static String ILLEGAL_ARGUMENTS_EXCEPTION = "using illegal argument like null";
    public final static String UNSUPPORTED_EXCEPTION = "token not support";

    public final static String INVALID_TOKEN = "INVALID_TOKEN";
    public final static String EXCEPTION = "exception";

    public final static String REDIRECT_URL_OAUTH2 = "https://localhost:8080/login/oauth2/code/google";
    public final static String ACCESS_TOKEN = "AccessToken";

    public final static Long AUTH_KEY_EXPIRATION_PERIOD = 300000L;
    public final static Long VALIDITY_PERIOD_OF_THE_AUTHENTICATION_KEY = 3_600_000L; // 1시간
}
