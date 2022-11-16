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

class CommentDtoTest {
    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    public static void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    @Test
    @DisplayName("댓글이 공백으로 작성되는 경우 예외가 발생한다.")
    void commentDto_failed_1() {
        //given 댓글 준비
        CommentDto.RequestPost request = CommentDto.RequestPost.builder()
                .content(null).build();
        //when 공백으로 작성된 댓글이 들어와 유효성 검사를 함
        Set<ConstraintViolation<CommentDto.RequestPost>> validate = validator.validate(request);
        //then 예외가 존재
        Assertions.assertThat(validate).isNotEmpty();
    }

    @Test
    @DisplayName("댓글이 유효성 검사를 통과한 경우 예외가 존재하지 않는다.")
    void commentDto_success_1() {
        //given 댓글 준비
        String VALID_CONTENT = "blahblah";
        CommentDto.RequestPost request = CommentDto.RequestPost.builder()
                .content(VALID_CONTENT).build();
        //when 공백으로 작성된 댓글이 들어와 유효성 검사를 함
        Set<ConstraintViolation<CommentDto.RequestPost>> validate = validator.validate(request);
        //then 예외가 존재
        Assertions.assertThat(validate).isEmpty();
    }
}