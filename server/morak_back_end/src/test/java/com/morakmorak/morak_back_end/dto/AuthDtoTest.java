package com.morakmorak.morak_back_end.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.AUTH_KEY;
import static com.morakmorak.morak_back_end.util.TestConstants.*;

class AuthDtoTest {
    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    public static void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("AuthDTO.Request에 Email이 null일 때 예외가 발생한다.")
    void test1() {
        //given
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .email(null)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("AuthDTO.Request에 Password가 null일 때 예외가 발생한다.")
    void test2() {
        //given
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(null)
                .email(EMAIL1)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("AuthDto.Request에 Nickname이 null일 때 예외가 발생한다.")
    void test3() {
        //given
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(null)
                .password(PASSWORD1)
                .email(EMAIL1)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("AuthDto.Request에 Email이 공백일 때 예외가 발생한다.")
    void test4() {
        //given
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .email(" ")
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("AuthDto.Request에 nickname이 공백일 때 예외가 발생한다.")
    void test5() {
        //given
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(" ")
                .password(PASSWORD1)
                .email(EMAIL1)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("AuthDto.Request에 Password가 공백일 때 예외가 발생한다.")
    void test6() {
        //given
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(" ")
                .email(EMAIL1)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("AuthDto.Request의 이메일이 이메일 형식이 아닐 때 예외가 발생한다.")
    void test7() {
        //given
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .email(INVALID_EMAIL)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("AuthDto.Request의 이메일이 이메일 형식이 맞다면 예외가 발생하지 않는다.")
    void test8() {
        //given
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .email(EMAIL1)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("AuthDto.Request의 패스워드가 모두 영문이라면 예외가 발생한다.")
    void test9() {
        //given
        String invalid_password = "abcdeffasdqwe";
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(invalid_password)
                .email(INVALID_EMAIL)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("AuthDto.Request의 패스워드가 모두 숫자라면 예외가 발생한다.")
    void test10() {
        //given
        String invalid_password = "12345678910";
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(invalid_password)
                .email(INVALID_EMAIL)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("AuthDto.Request의 패스워드가 모두 특수문자라면 예외가 발생한다.")
    void test11() {
        //given
        String invalid_password = "!!!!!@@@@@@!!!!";
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(invalid_password)
                .email(INVALID_EMAIL)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("AuthDto.Request의 패스워드가 8자리 미만이라면 예외가 발생한다.")
    void test12() {
        //given
        String invalid_password = "abcd!!1";
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(invalid_password)
                .email(INVALID_EMAIL)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("AuthDto.Request의 패스워드가 20자를 초과한다면 예외가 발생한다.")
    void test13() {
        //given
        String invalid_password = "123456789012345678a!9";
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(invalid_password)
                .email(INVALID_EMAIL)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("AuthDto.Request의 패스워드가 20자를 초과한다면 예외가 발생한다.")
    void test14() {
        //given
        String invalid_password = "123456789012345678a!9";
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(invalid_password)
                .email(INVALID_EMAIL)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("AuthDto.Request의 닉네임이 1자리라면 예외가 발생한다.")
    void test15() {
        //given
        String invalid_nickname = "힝";
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(invalid_nickname)
                .password(PASSWORD1)
                .email(INVALID_EMAIL)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("AuthDto.Request의 닉네임이 16자를 초과한다면 예외가 발생한다.")
    void test16() {
        //given
        String invalid_nickname = "123456789012345aa";
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(invalid_nickname)
                .password(PASSWORD1)
                .email(INVALID_EMAIL)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("AuthDto.Request의 닉네임 중간에 공백이 들어간다면 예외가 발생한다.")
    void test17() {
        //given
        String invalid_nickname = "안녕 하세요";
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(invalid_nickname)
                .password(PASSWORD1)
                .email(INVALID_EMAIL)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("AuthDto.Request의 닉네임에 초성이 들어간다면 예외가 발생한다.")
    void test18() {
        //given
        String invalid_nickname = "안녕ㅇ하세요";
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(invalid_nickname)
                .password(PASSWORD1)
                .email(INVALID_EMAIL)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("AuthDto.Request의 패스워드가 8자리 이상 20자리 미만, 영문 + 숫자 + 특수문자로 주어질 경우 예외가 발생하지 않는다.")
    void test19() {
        //given
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .email(INVALID_EMAIL)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("AuthDto.Request의 닉네임이 2자 이상 16자 이하 영문 + 숫자 + 한글로 공백/초성 없이 주어질 경우 예외가 발생하지 않는다.")
    void test20() {
        //given
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .email(EMAIL1)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("auth key 값이 존재하지 않으면 예외가 발생한다")
    void test21() {
        //given
        AuthDto.RequestJoin dto = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .email(INVALID_EMAIL)
                .build();

        //when
        Set<ConstraintViolation<AuthDto.RequestJoin>> violations = validator.validate(dto);

        //then
        Assertions.assertThat(violations).isNotEmpty();
    }

}