package com.morakmorak.morak_back_end.service.auth_service;

import com.morakmorak.morak_back_end.dto.AuthDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.BEARER_ACCESS_TOKEN;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.BEARER_REFRESH_TOKEN;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

public class LoginTest extends AuthServiceTest{
    @Test
    @DisplayName("유저 로그인 / 조회한 유저의 패스워드를 복호화하여 인자와 비교했을 때 " +
            "값이 다르다면 BusinessLogicEception이 발생한다.")
    public void test2() {
        //given
        User dbUser = User
                .builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        User requestUser = User
                .builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        given(userRepository.findUserByEmail(requestUser.getEmail())).willReturn(Optional.of(dbUser));
        given(userPasswordManager.comparePasswordWithUser(dbUser, requestUser)).willReturn(Boolean.FALSE);

        //when then
        assertThatThrownBy(() -> authService.loginUser(requestUser)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("유저 로그인 / 조회한 유저의 패스워드를 복호화하여 인자와 비교했을 때 " +
            "값이 같다면 AuthDto.Token을 반환한다.")
    public void test3() {
        //given
        User dbUser = User
                .builder()
                .id(ID1)
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        User requestUser = User
                .builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        UserDto.Redis redisUser = UserDto.Redis.builder()
                .email(EMAIL1)
                .avatarPath("http://image/image.jpg")
                .userId(ID1)
                .nickname(NICKNAME1)
                .build();

        given(userRepository.findUserByEmail(requestUser.getEmail())).willReturn(Optional.of(dbUser));
        given(userPasswordManager.comparePasswordWithUser(dbUser, requestUser)).willReturn(Boolean.TRUE);
        given(tokenGenerator.generateAccessToken(dbUser)).willReturn(BEARER_ACCESS_TOKEN);
        given(tokenGenerator.generateRefreshToken(dbUser)).willReturn(BEARER_REFRESH_TOKEN);
        given(userMapper.userToRedisUser(dbUser)).willReturn(redisUser);

        //when
        AuthDto.ResponseToken responseToken = authService.loginUser(requestUser);

        //then
        assertThat(responseToken.getAccessToken()).isEqualTo(BEARER_ACCESS_TOKEN);
        assertThat(responseToken.getRefreshToken()).isEqualTo(BEARER_REFRESH_TOKEN);
    }
}
