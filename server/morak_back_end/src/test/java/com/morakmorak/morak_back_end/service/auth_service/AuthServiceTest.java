package com.morakmorak.morak_back_end.service.auth_service;

import com.morakmorak.morak_back_end.adapter.TokenGenerator;
import com.morakmorak.morak_back_end.adapter.UserPasswordManager;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.repository.RedisRepository;
import com.morakmorak.morak_back_end.repository.RoleRepository;
import com.morakmorak.morak_back_end.repository.UserRepository;
import com.morakmorak.morak_back_end.repository.UserRoleRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.AuthService;
import com.morakmorak.morak_back_end.service.MailSenderImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    RedisRepository<User> refreshTokenRedisRepository;

    @Mock
    RedisRepository<String> authKeyRedisRepository;

    @Mock
    MailSenderImpl authMailSenderImpl;
}
