package com.morakmorak.morak_back_end.service.auth_service;

import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.REDIS_EMAIL_EMPTY;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.mockito.BDDMockito.given;

public class SendAuthenticationEmailForFindPwdTest extends AuthServiceTest {
    @Test
    @DisplayName("패스워드 찾기 요청 시 DB에 해당 이메일이 존재하지 않는다면 BusinessLogicException 발생")
    public void failed() {
        //given
        given(userRepository.findUserByEmail(EMAIL1)).willReturn(Optional.empty());

        //when then
        Assertions.assertThatThrownBy(() -> authService.sendAuthenticationMailForFindPwd(EMAIL1))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("패스워드 찾기 요청 시 DB에 해당 이메일이 존재한다면 이메일 전송")
    public void success() {
        //given
        User dbUser = User.builder().email(REDIS_EMAIL_EMPTY).id(1L).build();

        given(userRepository.findUserByEmail(REDIS_EMAIL_EMPTY)).willReturn(Optional.of(dbUser));

        //when
        Boolean result = authService.sendAuthenticationMailForFindPwd(REDIS_EMAIL_EMPTY);

        //then
        Assertions.assertThat(result).isTrue();
    }
}
