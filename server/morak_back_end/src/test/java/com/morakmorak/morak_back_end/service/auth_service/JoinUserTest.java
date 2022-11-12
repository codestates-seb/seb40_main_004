package com.morakmorak.morak_back_end.service.auth_service;

import com.morakmorak.morak_back_end.dto.AuthDto;
import com.morakmorak.morak_back_end.entity.Role;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.UserRole;
import com.morakmorak.morak_back_end.entity.enums.RoleName;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.Optional;

import static com.morakmorak.morak_back_end.security.util.SecurityConstants.REFRESH_TOKEN_EXPIRE_COUNT;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.any;
import static org.mockito.BDDMockito.given;

public class JoinUserTest extends AuthServiceTest {
    @Test
    @DisplayName("유저 회원가입 / 요청값과 동일한 메일을 가진 유저가 이미 존재하다면 BusinessLogicException이 발생한다")
    public void test4() {
        //given
        User requestUser = User.builder()
                .email(REDIS_EMAIL_EMPTY)
                .password(PASSWORD1)
                .build();

        given(userRepository.findUserByEmail(REDIS_EMAIL_EMPTY)).willReturn(Optional.of(requestUser));

        //when then
        assertThatThrownBy(() -> authService.joinUser(requestUser, REDIS_EMAIL_NOT_EMPTY))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("유저 회원가입 / 요청값으로 들어온 메일이 기존에 존재하지 않는다면 회원가입에 성공하고 가입된 아이디를 반환한다")
    public void test5() {
        //given
        User requestUser = User.builder()
                .id(ID1)
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        given(tokenGenerator.generateAccessToken(requestUser)).willReturn(BEARER_ACCESS_TOKEN);
        given(tokenGenerator.generateRefreshToken(requestUser)).willReturn(BEARER_REFRESH_TOKEN);

        //when
        AuthDto.ResponseToken responseToken = authService.joinUser(requestUser, REDIS_AUTH_KEY_NOT_EMPTY);

        //then
        assertThat(responseToken.getAccessToken()).isEqualTo(BEARER_ACCESS_TOKEN);
        assertThat(responseToken.getRefreshToken()).isEqualTo(BEARER_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("유저 회원가입 / 회원가입 시 기본적으로 권한을 가진다.")
    public void test8() {
        //given
        User requestUser = User.builder()
                .id(ID1)
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        Role role = Role.builder().roleName(RoleName.valueOf(ROLE_USER)).build();

        UserRole userRole = UserRole.builder()
                .user(requestUser)
                .role(role)
                .build();

        given(userRepository.findUserByEmail(EMAIL1)).willReturn(Optional.empty());
        given(userRoleRepository.save(ArgumentMatchers.any(UserRole.class))).willReturn(userRole);
        given(tokenGenerator.generateAccessToken(requestUser)).willReturn(BEARER_ACCESS_TOKEN);
        given(tokenGenerator.generateRefreshToken(requestUser)).willReturn(BEARER_REFRESH_TOKEN);

        //when
        authService.joinUser(requestUser, REDIS_AUTH_KEY_NOT_EMPTY);

        //then
        assertThat(userRole.getUser()).isEqualTo(requestUser);
        assertThat(userRole.getRole()).isEqualTo(role);
    }

    @Test
    @DisplayName("회원가입 성공 시 토큰을 반환한다")
    public void test12() {
        //given
        User requestUser = User.builder()
                .id(ID1)
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        Role role = Role.builder().roleName(RoleName.valueOf(ROLE_USER)).build();

        UserRole userRole = UserRole.builder()
                .user(requestUser)
                .role(role)
                .build();

        given(userRepository.findUserByEmail(EMAIL1)).willReturn(Optional.empty());
        given(userRoleRepository.save(ArgumentMatchers.any(UserRole.class))).willReturn(userRole);
        given(tokenGenerator.generateAccessToken(requestUser)).willReturn(BEARER_ACCESS_TOKEN);
        given(tokenGenerator.generateRefreshToken(requestUser)).willReturn(BEARER_REFRESH_TOKEN);

        //when
        AuthDto.ResponseToken responseToken = authService.joinUser(requestUser, REDIS_AUTH_KEY_NOT_EMPTY);

        //then
        assertThat(responseToken.getAccessToken()).isEqualTo(BEARER_ACCESS_TOKEN);
        assertThat(responseToken.getRefreshToken()).isEqualTo(BEARER_REFRESH_TOKEN);
    }
}
