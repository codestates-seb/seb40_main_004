package com.morakmorak.morak_back_end.service.auth_service;

import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.morakmorak.morak_back_end.util.TestConstants.EMAIL1;
import static com.morakmorak.morak_back_end.util.TestConstants.PASSWORD1;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

public class SendAuthenticationEmailForJoinTest extends AuthServiceTest {
    @Test
    @DisplayName("이메일 인증 요청 시, 해당 이메일을 가진 유저가 이미 존재한다면 BusinessLogicException이 발생한다")
    public void sendAuthenticationEmailForJoin_failed() {
        //given
        given(userRepository.findUserByEmail(EMAIL1)).willReturn(Optional.of(new User(PASSWORD1)));

        //when then
        assertThatThrownBy(() -> authService.sendAuthenticationMailForJoin(EMAIL1)).isInstanceOf(BusinessLogicException.class);
    }
}
