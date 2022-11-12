package com.morakmorak.morak_back_end.service.auth_service;

import com.morakmorak.morak_back_end.entity.RefreshToken;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.security.exception.InvalidJwtTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.BEARER_REFRESH_TOKEN;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.INVALID_BEARER_REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

public class ReissueTest extends AuthServiceTest {
    @Test
    @DisplayName("토큰 재발행/ 데이터베이스에 존재하지 않는 RefreshToken을 전달 받은 경우 BusinessLogicException이 발생한다.")
    public void test9() {
        assertThatThrownBy(() -> authService.reissueToken(BEARER_REFRESH_TOKEN))
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
}
