package com.morakmorak.morak_back_end.service.auth_service;

import com.morakmorak.morak_back_end.adapter.RandomKeyGenerator;
import com.morakmorak.morak_back_end.adapter.TokenGenerator;
import com.morakmorak.morak_back_end.adapter.UserPasswordManager;
import com.morakmorak.morak_back_end.config.RedisRepositoryTestImpl;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.repository.*;
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

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(authService, "refreshTokenStore", new RedisRepositoryTestImpl<User>());
        ReflectionTestUtils.setField(authService, "mailAuthKeyStore", new RedisRepositoryTestImpl<String>());
    }
}
