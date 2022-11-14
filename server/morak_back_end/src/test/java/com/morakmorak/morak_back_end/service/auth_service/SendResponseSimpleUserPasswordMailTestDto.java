package com.morakmorak.morak_back_end.service.auth_service;

import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.service.MailSenderImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

public class SendResponseSimpleUserPasswordMailTestDto extends AuthServiceTest {
    @Test
    @DisplayName("패스워드 찾기 요청 시, 요청받은 이메일을 가진 유저가 존재하지 않는다면 BusinessLogicException이 발생한다.")
    public void sendUserPasswordMail_failed() {
        //given
        given(userRepository.findUserByEmail(REDIS_EMAIL_NOT_EMPTY)).willReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> authService.sendUserPasswordEmail(REDIS_EMAIL_NOT_EMPTY, AUTH_KEY))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("패스워드 찾기 요청 시, 요청받은 이메일을 가진 유저가 존재하고 인증키가 일치한다면 비밀번호를 변경한다.")
    public void sendUserPasswordMail_success() {
        //given
        User dbUser = User.builder()
                .email(REDIS_EMAIL_NOT_EMPTY)
                .password(PASSWORD1)
                .build();

        given(userRepository.findUserByEmail(REDIS_EMAIL_NOT_EMPTY)).willReturn(Optional.of(dbUser));
        given(userPasswordManager.encryptUserPassword(dbUser)).willReturn(PASSWORD2);
        given(authMailSenderImpl.sendMail(REDIS_EMAIL_NOT_EMPTY, null, MailSenderImpl.BASIC_PASSWORD_SUBJECT)).willReturn(Boolean.TRUE);

        //when
        authService.sendUserPasswordEmail(REDIS_EMAIL_NOT_EMPTY, AUTH_KEY);

        //then
        assertThat(dbUser.getPassword()).isNotEqualTo(PASSWORD2);
    }

    @Test
    @DisplayName("패스워드 찾기 요청 시, 해당 이메일에 대한 인증키가 존재하지 않는다면 BusinessLogicException이 발생한다.")
    public void sendUserPasswordMail_success2() {
        //given when then
        assertThatThrownBy(()->authService.sendUserPasswordEmail(REDIS_EMAIL_EMPTY, AUTH_KEY))
                .isInstanceOf(BusinessLogicException.class);
    }
}
