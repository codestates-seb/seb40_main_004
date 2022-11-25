package com.morakmorak.morak_back_end.service.auth_service;

import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.morakmorak.morak_back_end.util.TestConstants.NICKNAME1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

public class CheckDuplicateNicknameTest extends AuthServiceTest {
    @Test
    @DisplayName("닉네임 중복 검사 시 해당 닉네임이 존재할 경우 BusinessLogicException 발생")
    public void checkDuplicateNickname_failed() {
        //given
        given(userRepository.findUserByNickname(NICKNAME1)).willReturn(Optional.of(User.builder().build()));

        //when //then
        assertThatThrownBy(() -> authService.checkDuplicateNickname(NICKNAME1)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("닉네임 중복 검사 시 해당 닉네임이 존재하지 않을 경우 true 반환")
    public void checkDuplicateNickname_success() {
        //given
        given(userRepository.findUserByNickname(NICKNAME1)).willReturn(Optional.empty());

        //when
        Boolean result = authService.checkDuplicateNickname(NICKNAME1);

        //then
        assertThat(result).isTrue();
    }
}
