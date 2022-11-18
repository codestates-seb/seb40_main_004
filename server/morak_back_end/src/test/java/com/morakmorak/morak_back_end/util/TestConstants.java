package com.morakmorak.morak_back_end.util;

import com.morakmorak.morak_back_end.entity.Avatar;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.Grade;

import java.time.LocalDateTime;

public class TestConstants {
    public final static String EMAIL1 = "user@test.com";
    public final static String EMAIL2 = "user2@test.com";

    public final static String PASSWORD1 = "passWORD123!";
    public final static String PASSWORD2 = "passWORD321!";
    public final static Long ID1 = 1L;
    public final static Long ID2 = 2L;
    public final static String NICKNAME1 = "testUser1";
    public final static String NICKNAME2 = "testUser2";

    public final static String NAME1 = "김민수";
    public final static String NAME2 = "임지민";

    public final static String INVALID_EMAIL = "user@testtest";
    public final static String INVALID_PASSWORD = "PASSWORD123";
    public final static String INVALID_NICKNAME = "ㅋㅋㅋㅋ";

    public final static String ROLE_USER = "ROLE_USER";
    public final static String ROLE_MANAGER = "ROLE_MANAGER";
    public final static String ROLE_ADMIN = "ROLE_ADMIN";

    public final static String TITLE1 = "아무 제목이나 지어야지~";
    public final static String TITLE2 = "아무 제목이나 지어야지~2";

    public final static String CONTENT1 = "아무말이나 적어야지~";
    public final static String CONTENT2 = "아무말이나 적어야지~2";

    public final static LocalDateTime NOW_TIME = LocalDateTime.now();

    public final static String GITHUB_URL = "https://github.com/";
    public final static String TISTORY_URL = "https://7357.tistory.com/";

    public final static String QNA = "qna";
    public final static String INFO = "info";

    public final static Avatar AVATAR = Avatar.builder().id(1L).originalFilename("randomfilename").remotePath("randomremotepath").build();
    public final static User USER1 = User.builder().id(ID1).email(EMAIL1).name(NAME1).nickname(NICKNAME1).grade(Grade.GOLD).avatar(AVATAR).build();


}
