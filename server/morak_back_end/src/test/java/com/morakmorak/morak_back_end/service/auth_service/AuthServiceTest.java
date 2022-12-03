package com.morakmorak.morak_back_end.service.auth_service;

import com.morakmorak.morak_back_end.domain.RandomKeyGenerator;
import com.morakmorak.morak_back_end.domain.TokenGenerator;
import com.morakmorak.morak_back_end.domain.UserPasswordManager;
import com.morakmorak.morak_back_end.config.RedisRepositoryTestImpl;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.UserStatus;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.repository.user.RoleRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.repository.user.UserRoleRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.auth_user_service.AuthService;
import com.morakmorak.morak_back_end.service.mail_service.MailSenderImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static com.morakmorak.morak_back_end.util.TestConstants.EMAIL1;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    AuthService authService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserPasswordManager userPasswordManager;

    @Mock
    TokenGenerator tokenGenerator;

    @Mock
    UserRoleRepository userRoleRepository;

    @Mock
    JwtTokenUtil jwtTokenUtil;

    @Mock
    RoleRepository roleRepository;

    @Mock
    MailSenderImpl authMailSenderImpl;

    @Mock
    RandomKeyGenerator randomKeyGenerator;

    @Mock
    UserMapper userMapper;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(authService, "refreshTokenStore", new RedisRepositoryTestImpl<UserDto.Redis>());
        ReflectionTestUtils.setField(authService, "mailAuthKeyStore", new RedisRepositoryTestImpl<String>());
    }

    @Test
    @DisplayName("탈퇴한 유저가 로그인을 시도할 경우 BusinessLogicException이 발생한다")
    void login_failed() {
        //given
        User user = User.builder().email(EMAIL1).userStatus(UserStatus.DELETED).build();
        given(userRepository.findUserByEmail(user.getEmail())).willReturn(Optional.of(user));

        //when //then
        Assertions.assertThatThrownBy(() ->
                authService.loginUser(user))
                .isInstanceOf(BusinessLogicException.class);
    }
}
