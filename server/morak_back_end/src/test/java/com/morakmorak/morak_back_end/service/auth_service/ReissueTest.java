package com.morakmorak.morak_back_end.service.auth_service;

import com.morakmorak.morak_back_end.dto.AuthDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.RefreshToken;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.security.exception.InvalidJwtTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.EMAIL1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class ReissueTest extends AuthServiceTest {
    @Test
    @DisplayName("토큰 재발행/ 데이터베이스에 존재하지 않는 RefreshToken을 전달 받은 경우 BusinessLogicException이 발생한다.")
    public void test9() {
        assertThatThrownBy(() -> authService.reissueToken(BEARER_REDIS_TOKEN_EMPTY))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("토큰 재발행/ 유효하지 않은 RefreshToken을 전달 받은 경우 InvalidTokenException이 발생한다.")
    public void test10() {
        //given
        given(tokenGenerator.tokenValidation(INVALID_BEARER_REFRESH_TOKEN)).willThrow(InvalidJwtTokenException.class);

        //when then
        assertThatThrownBy(() -> authService.reissueToken(INVALID_BEARER_REFRESH_TOKEN))
                .isInstanceOf(InvalidJwtTokenException.class);
    }

    @Test
    @DisplayName("토큰 재발행/ 유효한 RefreshToken을 전달 받은 경우 새로운 AccessToken과 RefreshToken을 발행한다.")
    public void test11() {
        //given
        User user = User.builder().build();
        given(tokenGenerator.tokenValidation(BEARER_REDIS_TOKEN_NOT_EMPTY)).willReturn(Boolean.TRUE);
        given(userMapper.redisUserToUser(any(UserDto.Redis.class))).willReturn(user);
        given(tokenGenerator.generateRefreshToken(any(User.class))).willReturn(BEARER_REFRESH_TOKEN);
        given(tokenGenerator.generateAccessToken(any(User.class))).willReturn(BEARER_ACCESS_TOKEN);

        //when
        AuthDto.ResponseToken responseToken = authService.reissueToken(BEARER_REDIS_TOKEN_NOT_EMPTY);

        //then
        assertThat(responseToken.getAccessToken()).isEqualTo(BEARER_ACCESS_TOKEN);
        assertThat(responseToken.getRefreshToken()).isEqualTo(BEARER_REFRESH_TOKEN);
    }
}
