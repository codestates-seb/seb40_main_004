package com.morakmorak.morak_back_end.service.auth_service;

import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.morakmorak.morak_back_end.service.mail_service.MailSenderImpl.*;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.EMAIL1;
import static com.morakmorak.morak_back_end.util.TestConstants.PASSWORD1;
import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    @DisplayName("레디스에 이메일 인증 전송을 요청받은 내역이 이미 존재한다면 BusinessLogicException이 발생한다.")
    public void test13() {
        // given
        // when then
        assertThatThrownBy(() -> authService.sendAuthenticationMail(REDIS_EMAIL_NOT_EMPTY))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("레디스에 이메일 인증 전송을 요청받은 내역이 존재하지 않는다면 실행을 완료하고 true를 반환한다.")
    public void test14() {
        // given
        given(authMailSenderImpl.sendMail(REDIS_EMAIL_EMPTY, null, BASIC_AUTH_SUBJECT)).willReturn(Boolean.TRUE);

        // when
        boolean result = authService.sendAuthenticationMail(REDIS_EMAIL_EMPTY);

        // then
        assertThat(result).isTrue();
    }
}
