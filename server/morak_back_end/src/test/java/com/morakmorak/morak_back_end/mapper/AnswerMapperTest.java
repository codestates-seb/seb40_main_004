package com.morakmorak.morak_back_end.mapper;

import com.morakmorak.morak_back_end.dto.AnswerDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional

class AnswerMapperTest {

    @Autowired
    AnswerMapper answerMapper;


    @Test
    @DisplayName("makingResponseAnswerLikeDto 메퍼 작동 테스트")
    void makingResponseAnswerLikeDto() {
        //given
        Long answerId = 1L;
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().id(1L).build();
        //when
        AnswerDto.ResponseAnswerLike responseAnswerLike =
                answerMapper.makingResponseAnswerLikeDto(answerId, userInfo.getId(), true, 1);
        //then
        assertThat(responseAnswerLike.getAnswerId()).isEqualTo(1L);
        assertThat(responseAnswerLike.getUserId()).isEqualTo(1L);
        assertThat(responseAnswerLike.getIsLiked()).isTrue();
        assertThat(responseAnswerLike.getLikeCount()).isEqualTo(1);
    }


}