package com.morakmorak.morak_back_end.service.auth_service;

import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.BEARER_REDIS_TOKEN_EMPTY;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.BEARER_REDIS_TOKEN_NOT_EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LogoutTest extends AuthServiceTest {
    @Test
    @DisplayName("유저 로그아웃 / 요청값으로 들어온 리프레시 토큰이 기존에 존재하지 않는다면 BusinessLogicEcxeption이 발생한다.")
    public void test6() {
        //given when then
        assertThatThrownBy(() -> authService.logoutUser(BEARER_REDIS_TOKEN_EMPTY))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("유저 로그아웃 / 요청값으로 들어온 리프레시 토큰 데이터가 존재한다면 해당 데이터를 삭제하고 true를 반환한다.")
    public void test7() {
        //when
        boolean result = authService.logoutUser(BEARER_REDIS_TOKEN_NOT_EMPTY);

        //then
        assertThat(result).isTrue();
    }
}
