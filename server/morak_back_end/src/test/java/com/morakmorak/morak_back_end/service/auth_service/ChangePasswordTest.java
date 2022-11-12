package com.morakmorak.morak_back_end.service.auth_service;

import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class ChangePasswordTest extends AuthServiceTest {
    @Test
    @DisplayName("패스워드 변경 요청 시, 해당 유저 DB 패스워드와 입력받은 originalPassword가 일치하지 않는다면 BusinessLogicException 발생")
    public void changePassword_failed() {
        //given
        User dbUser = User.builder()
                .id(ID1)
                .password(PASSWORD1)
                .build();

        given(userRepository.findById(ID1)).willReturn(Optional.of(dbUser));
        given(userPasswordManager.compareUserPassword(any(User.class), any(User.class))).willReturn(Boolean.FALSE);

        //when then
        assertThatThrownBy(() -> authService.changePassword(INVALID_PASSWORD, PASSWORD2, ID1))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("패스워드 변경 요청 시, 해당 유저 DB 패스워드와 입력받은 originalPassword가 일치한다면 유저의 패스워드를 newPassword로 변경")
    public void changePassword_success() {
        //given
        User dbUser = User.builder()
                .id(ID1)
                .password(PASSWORD1)
                .build();

        given(userRepository.findById(ID1)).willReturn(Optional.of(dbUser));
        given(userPasswordManager.compareUserPassword(any(User.class), any(User.class))).willReturn(Boolean.TRUE);

        //when
        Boolean result = authService.changePassword(PASSWORD1, PASSWORD2, ID1);

        //then
        assertThat(result).isTrue();
    }
}
