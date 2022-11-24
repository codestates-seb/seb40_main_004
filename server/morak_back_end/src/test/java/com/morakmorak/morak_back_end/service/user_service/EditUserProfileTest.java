package com.morakmorak.morak_back_end.service.user_service;

import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.morakmorak.morak_back_end.util.TestConstants.ID1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

public class EditUserProfileTest extends UserServiceTest {
    @Test
    @DisplayName("해당 유저가 존재하지 않을 때 BusinessLogicException 발생")
    void editUserProfile_failed() {
        //given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        //when //then
        assertThatThrownBy(() -> userService.editUserProfile(requestUser, ID1)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("해당 닉네임이 이미 존재할 때 BusinessLogicException 발생")
    void editUserProfile_failed2() {
        //given
        User dbUser = User.builder().build();
        given(userRepository.findById(anyLong())).willReturn(Optional.of(dbUser));
        given(userRepository.findUserByNickname(anyString())).willReturn(Optional.of(dbUser));

        //when then
        assertThatThrownBy(() -> userService.editUserProfile(requestUser, ID1)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("로직이 정상적으로 수행된 경우 수정이 반영된 값을 반환")
    void editUserProfile_failed3() {
        //given
        UserDto.SimpleEditProfile response = UserDto.SimpleEditProfile.builder()
                .blog(requestUser.getBlog())
                .nickname(requestUser.getNickname())
                .github(requestUser.getGithub())
                .jobType(requestUser.getJobType())
                .infoMessage(requestUser.getInfoMessage())
                .build();

        User dbUser = User.builder().build();
        given(userRepository.findById(ID1)).willReturn(Optional.of(dbUser));
        given(userRepository.findUserByNickname(requestUser.getNickname())).willReturn(Optional.empty());
        given(userMapper.userToEditProfile(any(User.class))).willReturn(response);

        //when
        UserDto.SimpleEditProfile result = userService.editUserProfile(requestUser, ID1);

        //then
        assertThat(result.getBlog()).isEqualTo(requestUser.getBlog());
        assertThat(result.getGithub()).isEqualTo(requestUser.getGithub());
        assertThat(result.getNickname()).isEqualTo(requestUser.getNickname());
        assertThat(result.getJobType()).isEqualTo(requestUser.getJobType());
        assertThat(result.getInfoMessage()).isEqualTo(requestUser.getInfoMessage());
    }
}
