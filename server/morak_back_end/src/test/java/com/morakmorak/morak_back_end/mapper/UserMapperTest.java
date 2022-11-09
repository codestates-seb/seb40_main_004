package com.morakmorak.morak_back_end.mapper;

import com.example.boilerplate.dto.AuthDto;
import com.example.boilerplate.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.example.boilerplate.util.TestConstants.*;

class UserMapperTest {
    UserMapper userMapper;

    @BeforeEach
    public void init() {
        userMapper = new UserMapperImpl();
    }

    @Test
    @DisplayName("RequestLoginDto to UserEntity Test")
    public void test1() {
        //given
        AuthDto.RequestLogin dto = AuthDto.RequestLogin.builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        //when
        User user = userMapper.toLoginEntity(dto);

        //then
        assertThat(user.getEmail()).isEqualTo(EMAIL1);
        assertThat(user.getPassword()).isEqualTo(PASSWORD1);
    }

    @Test
    @DisplayName("RequestJoinDto to UserEntity Test")
    public void test2() {
        //given
        AuthDto.RequestJoin dto = AuthDto.RequestJoin.builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .nickname(NICKNAME1)
                .build();

        //when
        User user = userMapper.toJoinEntity(dto);

        //then
        assertThat(user.getEmail()).isEqualTo(EMAIL1);
        assertThat(user.getPassword()).isEqualTo(PASSWORD1);
        assertThat(user.getNickname()).isEqualTo(NICKNAME1);
    }
}