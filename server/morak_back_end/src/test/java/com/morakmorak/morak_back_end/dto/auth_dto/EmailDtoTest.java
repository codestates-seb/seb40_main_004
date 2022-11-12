package com.morakmorak.morak_back_end.dto.auth_dto;

import com.morakmorak.morak_back_end.dto.EmailDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.AUTH_KEY;
import static com.morakmorak.morak_back_end.util.TestConstants.EMAIL1;
import static com.morakmorak.morak_back_end.util.TestConstants.INVALID_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;

public class EmailDtoTest {
    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    public static void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("RequestSendMail의 email이 이메일 형식이 아닐 경우 예외가 발생한다.")
    public static void test1() {
        //given
        EmailDto.RequestSendMail request = EmailDto.RequestSendMail
                .builder()
                .email(INVALID_EMAIL)
                .build();

        //when
        Set<ConstraintViolation<EmailDto.RequestSendMail>> validate = validator.validate(request);

        //then
        assertThat(validate).isNotEmpty();
    }

    @Test
    @DisplayName("RequestSendMail의 email이 이메일 형식일 경우 예외가 발생하지 않는다.")
    public static void test2() {
        //given
        EmailDto.RequestSendMail request = EmailDto.RequestSendMail
                .builder()
                .email(EMAIL1)
                .build();

        //when
        Set<ConstraintViolation<EmailDto.RequestSendMail>> validate = validator.validate(request);

        //then
        assertThat(validate).isEmpty();
    }

    @Test
    @DisplayName("RequestSendMail의 email이 null일 경우 예외 발생한다.")
    public static void test3() {
        //given
        EmailDto.RequestSendMail request = EmailDto.RequestSendMail
                .builder()
                .email(null)
                .build();

        //when
        Set<ConstraintViolation<EmailDto.RequestSendMail>> validate = validator.validate(request);

        //then
        assertThat(validate).isNotEmpty();
    }

    @Test
    @DisplayName("RequestVerifyAuthKey의 email이 null일 경우 예외 발생한다.")
    public static void test4() {
        //given
        EmailDto.RequestVerifyAuthKey request = EmailDto.RequestVerifyAuthKey
                .builder()
                .email(null)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<EmailDto.RequestVerifyAuthKey>> validate = validator.validate(request);

        //then
        assertThat(validate).isNotEmpty();
    }

    @Test
    @DisplayName("RequestVerifyAuthKey의 authKey가 null일 경우 예외 발생한다.")
    public static void test5() {
        //given
        EmailDto.RequestVerifyAuthKey request = EmailDto.RequestVerifyAuthKey
                .builder()
                .email(EMAIL1)
                .authKey(null)
                .build();

        //when
        Set<ConstraintViolation<EmailDto.RequestVerifyAuthKey>> validate = validator.validate(request);

        //then
        assertThat(validate).isNotEmpty();
    }

    @Test
    @DisplayName("RequestVerifyAuthKey의 email이 이메일 형식이 아닐 경우 예외 발생한다.")
    public static void test6() {
        //given
        EmailDto.RequestVerifyAuthKey request = EmailDto.RequestVerifyAuthKey
                .builder()
                .email(INVALID_EMAIL)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<EmailDto.RequestVerifyAuthKey>> validate = validator.validate(request);

        //then
        assertThat(validate).isNotEmpty();
    }

    @Test
    @DisplayName("RequestVerifyAuthKey의 email이 이메일 형식이고, authKey와 email 모두 null이 아닐 경우 예외가 발생하지 않는다.")
    public static void test7() {
        //given
        EmailDto.RequestVerifyAuthKey request = EmailDto.RequestVerifyAuthKey
                .builder()
                .email(EMAIL1)
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<EmailDto.RequestVerifyAuthKey>> validate = validator.validate(request);

        //then
        assertThat(validate).isEmpty();
    }

    @Test
    @DisplayName("RequestVerifyAuthKey의 email이 이메일이 공백일 경우 예외가 발생한다.")
    public static void test8() {
        //given
        EmailDto.RequestVerifyAuthKey request = EmailDto.RequestVerifyAuthKey
                .builder()
                .email(" ")
                .authKey(AUTH_KEY)
                .build();

        //when
        Set<ConstraintViolation<EmailDto.RequestVerifyAuthKey>> validate = validator.validate(request);

        //then
        assertThat(validate).isEmpty();
    }

    @Test
    @DisplayName("RequestVerifyAuthKey의 authKey가 공백일 경우 예외가 발생한다.")
    public static void test9() {
        //given
        EmailDto.RequestVerifyAuthKey request = EmailDto.RequestVerifyAuthKey
                .builder()
                .email(EMAIL1)
                .authKey(" ")
                .build();

        //when
        Set<ConstraintViolation<EmailDto.RequestVerifyAuthKey>> validate = validator.validate(request);

        //then
        assertThat(validate).isEmpty();
    }
}
