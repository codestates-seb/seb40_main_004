package com.morakmorak.morak_back_end.service.auth_service;

import com.morakmorak.morak_back_end.domain.RandomKeyGenerator;
import com.morakmorak.morak_back_end.domain.TokenGenerator;
import com.morakmorak.morak_back_end.domain.UserPasswordManager;
import com.morakmorak.morak_back_end.config.RedisRepositoryTestImpl;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.repository.user.RoleRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.repository.user.UserRoleRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.auth_user_service.AuthService;
import com.morakmorak.morak_back_end.service.mail_service.MailSenderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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
}
