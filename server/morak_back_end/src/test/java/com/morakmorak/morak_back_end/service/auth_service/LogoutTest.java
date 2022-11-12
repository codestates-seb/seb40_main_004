package com.morakmorak.morak_back_end.service.auth_service;

import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.BEARER_REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LogoutTest extends AuthServiceTest {
    @Test
    @DisplayName("유저 로그아웃 / 요청값으로 들어온 리프레시 토큰이 기존에 존재하지 않는다면 BusinessLogicEcxeption이 발생한다.")
    public void test6() {
        //given when then
        assertThatThrownBy(() -> authService.logoutUser(BEARER_REFRESH_TOKEN))
                .isInstanceOf(BusinessLogicException.class);
    }
}
