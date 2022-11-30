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

class AnswerDtoTest {
    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    public static void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("답변 내용의 길이가 10자 이하이면 400 예외가 발생한다.")
    void contentLength_failed_1() {
        //유효하지 않은 requestAnswer
        AnswerDto.RequestPostAnswer request = AnswerDto.RequestPostAnswer.builder().content("10자이하").build();
        //when 유효성 검사 진행 시
        Set<ConstraintViolation<AnswerDto.RequestPostAnswer>> validate = validator.validate(request);
        //then 예외가 존재
        Assertions.assertThat(validate).isNotEmpty();
    }
    @Test
    @DisplayName("답변 내용의 길이가 15자 이상이면 예외가 발생하지 않는다.")
    void contentLength_success_1() {
        //유효한 requestAnswer
        AnswerDto.RequestPostAnswer request = AnswerDto.RequestPostAnswer.builder().content("10자 이상의 유효한 본문일 때 예외가 발생하지 않습니다.").build();
        //when 유효성 검사 진행 시
        Set<ConstraintViolation<AnswerDto.RequestPostAnswer>> validate = validator.validate(request);
        //then 예외가 존재
        Assertions.assertThat(validate).isEmpty();
    }
}