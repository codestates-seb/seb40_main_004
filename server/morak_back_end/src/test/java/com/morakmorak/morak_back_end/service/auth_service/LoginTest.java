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
        given(userPasswordManager.compareUserPassword(dbUser, requestUser)).willReturn(Boolean.FALSE);

        //when then
        assertThatThrownBy(() -> authService.loginUser(requestUser)).isInstanceOf(BusinessLogicException.class);
    }

}
