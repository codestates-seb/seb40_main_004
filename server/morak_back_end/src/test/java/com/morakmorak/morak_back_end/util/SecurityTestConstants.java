package com.morakmorak.morak_back_end.util;

import java.util.List;

import static com.morakmorak.morak_back_end.util.TestConstants.*;

public class SecurityTestConstants {
    public final static String JWT_HEADER = "Authorization";
    public final static String INVALID_JWT_PREFIX = "fake ";
    public final static String JWT_PREFIX = "Bearer ";
    public final static String BEARER_ACCESS_TOKEN = "Bearer AccessToken";
    public final static String INVALID_BEARER_ACCESS_TOKEN = "Bearer FakeToken";
    public final static String BEARER_REFRESH_TOKEN = "Bearer RefreshToken";
    public final static String INVALID_BEARER_REFRESH_TOKEN = "Bearer FakeToken";
    public final static String ACCESS_TOKEN = "AccessToken";
    public final static String REFRESH_TOKEN = "RefreshToken";
    public final static String INVALID_REFRESH_TOKEN = "Bearer FakeToken";

    public final static String INVALID_SECRET_KEY = "dlrjtdmswkfahtehlstlzmfltzldlqslek";
    public final static String SECRET_KEY = "dlrjtdmstlzmfltzlekrmfjsepTmfakfdldjqtek";
    public final static String REFRESH_KEY = "dlrjtdmsflvmfptlzlekrmfjsepTmfakfdldjqtek";

    public final static int ZERO = 0;
    public final static int ONE = 1;
    public final static int TWO = 2;
    public final static int THREE = 3;
    public final static int FOUR = 4;

    public final static String ROLES = "roles";
    public final static String ID = "id";

    public final static List<String> ROLE_USER_LIST = List.of(ROLE_USER);
    public final static List<String> ROLE_MANAGER_LIST = List.of(ROLE_MANAGER);
    public final static List<String> ROLE_ADMIN_LIST = List.of(ROLE_ADMIN);
    public final static List<String> ROLE_USER_ADMIN_LIST = List.of(ROLE_USER, ROLE_ADMIN);
    public final static List<String> ROLE_USER_MANAGER_LIST = List.of(ROLE_USER, ROLE_MANAGER);
    public final static List<String> ROLE_USER_MANAGER_ADMIN_LIST = List.of(ROLE_USER,ROLE_MANAGER,ROLE_ADMIN);

    public final static String AUTH_KEY = "11111111";
    public final static String INVALID_AUTH_KEY = "22222222";

    public final static String REDIS_AUTH_KEY_EMPTY = "emtpy key";
    public final static String REDIS_AUTH_KEY_NOT_EMPTY = "not empty key";

    public final static String REDIS_EMAIL_EMPTY = "empty email";
    public final static String REDIS_EMAIL_NOT_EMPTY = "not empty email";

    public final static String BEARER_REDIS_TOKEN_EMPTY = "Bearer emptyToken";
    public final static String BEARER_REDIS_TOKEN_NOT_EMPTY = "Bearer notEmptyToken";

    public final static String REDIS_TOKEN_EMPTY = "emptyToken";
    public final static String REDIS_TOKEN_NOT_EMPTY = "notEmptyToken";
}
