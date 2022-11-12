package com.morakmorak.morak_back_end.service.auth_service;

import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.morakmorak.morak_back_end.util.TestConstants.ID1;
import static com.morakmorak.morak_back_end.util.TestConstants.PASSWORD1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

public class DeleteAccountTest extends AuthServiceTest {
    @Test
    @DisplayName("회원 탈퇴 요청 시, 입력받은 패스워드가 데이터베이스의 패스워드와 일치하지 않는다면 BusinessLogicException이 발생한다")
    public void deleteAccount_failed() {
        //given
        User user = User.builder()
                .password(PASSWORD1)
                .build();

        given(userRepository.findById(ID1)).willReturn(Optional.of(user));
        given(userPasswordManager.compareUserPassword(any(User.class), any(User.class))).willThrow(new BusinessLogicException(ErrorCode.MISMATCHED_PASSWORD));

        //when then
        assertThatThrownBy(() -> authService.deleteAccount(anyString(), ID1))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("회원 탈퇴 요청 시, 입력받은 패스워드가 데이터베이스의 패스워드와 일치한다면 회원을 삭제하고 true를 반환한다.")
    public void deleteAccount_success() {
        //given
        User user = User.builder()
                .password(PASSWORD1)
                .build();

        given(userRepository.findById(ID1)).willReturn(Optional.of(user));
        given(userPasswordManager.compareUserPassword(any(User.class), any(User.class))).willReturn(Boolean.TRUE);

        //when
        boolean result =authService.deleteAccount(anyString(), ID1);

        //then
        assertThat(result).isTrue();
    }
}
