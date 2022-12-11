package com.morakmorak.morak_back_end.security.util;

public class SecurityConstants {
    public static final String JWT_HEADER = "Authorization";
    public static final String REFRESH_HEADER = "RefreshToken";
    public static final String JWT_PREFIX = "Bearer ";

    public final static Long ACCESS_TOKEN_EXPIRE_COUNT = 60000L*10*3; // 30 minutes
    public final static Long REFRESH_TOKEN_EXPIRE_COUNT = 7 * 24 * 60 * 60 * 1000L; // 7 days

    public final static String ROLES = "roles";
    public final static String ID = "id";
    public final static String EMAIL = "email";
    public static final String NICKNAME = "nickname";

    public final static String REDIRECT_URL_OAUTH2 = "https://seb40-main-004.vercel.app/oauth/login";
    public final static String ACCESS_TOKEN = "AccessToken";

    public final static Long AUTH_KEY_EXPIRATION_PERIOD = 300000L;
    public final static Long VALIDITY_PERIOD_OF_THE_AUTHENTICATION_KEY = 3_600_000L; // 1시간

    public final static Integer TEMPORARY_PASSWORD_LENGTH = 15;
    public final static Integer EMAIL_AUTH_KEY_LENGTH = 8;
    public final static Integer TEMPORARY_NICKNAME_CODE_LENGTH = 5;

    public final static String DO_NOT_CRAWL = "I am a Teapot, do not crawl";
}
