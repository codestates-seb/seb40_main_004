//package com.morakmorak.morak_back_end.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;


//class BookmarkDtoTest {
//    private static ValidatorFactory factory;
//    private static Validator validator;
//
//    @BeforeAll
//    public static void init() {
//        factory = Validation.buildDefaultValidatorFactory();
//        validator = factory.getValidator();
//    }
//
//    @Test
//    @DisplayName("RequestPostBookmark의 memo가 20자 이상일 경우 예외가 발생한다.")
//    void bookmarkdto_RequestPost_fail_1() {
//
//
//        //when
//        Set<ConstraintViolation<BookmarkDto.RequestPostBookmark>> validate = validator.validate(request);
//
//        //then
//        assertThat(validate).isNotEmpty();
//    }

//}