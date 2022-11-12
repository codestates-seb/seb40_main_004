package com.morakmorak.morak_back_end.service.auth_service;

import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.EMAIL1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

public class SendUserPasswordMailTest extends AuthServiceTest {
    @Test
    @DisplayName("패스워드 찾기 요청 시, 요청받은 이메일을 가진 유저가 존재하지 않는다면 BusinessLogicException이 발생한다.")
    public void sendUserPasswordMail_failed() {
        //given
        given(userRepository.findUserByEmail(EMAIL1)).willReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> authService.sendUserPasswordEmail(EMAIL1))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("패스워드 찾기 요청 시, 요청받은 이메일을 가진 유저가 존재한다면 비밀번호를 변경한다.")
    public void sendUserPasswordMail_success() {
        //given
        User dbUser = User.builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        given(userRepository.findUserByEmail(EMAIL1)).willReturn(Optional.of(dbUser));
        given(userPasswordManager.encryptUserPassword(dbUser)).willReturn(PASSWORD2);
        given(authMailSenderImpl.sendMail(anyString(), anyString(), anyString())).willReturn(Boolean.TRUE);

        //when
        authService.sendUserPasswordEmail(EMAIL1);

        //then
        assertThat(dbUser.getPassword()).isNotEqualTo(PASSWORD2);
    }

    @Test
    @DisplayName("패스워드 찾기 요청 시, 요청받은 이메일을 가진 유저가 존재한다면 true를 반환한다.")
    public void sendUserPasswordMail_success2() {
        //given
        User dbUser = User.builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        given(userRepository.findUserByEmail(EMAIL1)).willReturn(Optional.of(dbUser));
        given(userPasswordManager.encryptUserPassword(dbUser)).willReturn(PASSWORD2);
        given(authMailSenderImpl.sendMail(anyString(), anyString(), anyString())).willReturn(Boolean.TRUE);

        //when
        Boolean result = authService.sendUserPasswordEmail(EMAIL1);

        //then
        assertThat(result).isTrue();
    }
}
