package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.adapter.TokenGenerator;
import com.morakmorak.morak_back_end.adapter.UserPasswordManager;
import com.morakmorak.morak_back_end.dto.AuthDto;
import com.morakmorak.morak_back_end.dto.EmailDto;
import com.morakmorak.morak_back_end.entity.Role;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.UserRole;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.repository.RedisRepository;
import com.morakmorak.morak_back_end.repository.RoleRepository;
import com.morakmorak.morak_back_end.repository.UserRepository;
import com.morakmorak.morak_back_end.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

import static com.morakmorak.morak_back_end.entity.enums.RoleName.ROLE_USER;
import static com.morakmorak.morak_back_end.exception.ErrorCode.*;
import static com.morakmorak.morak_back_end.security.util.SecurityConstants.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserPasswordManager userPasswordManager;
    private final TokenGenerator tokenGenerator;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RedisRepository<User> refreshTokenRedisRepository;
    private final RedisRepository<String> mailAuthRedisRepository;
    private final AuthMailSender authMailSenderImpl;

    public AuthDto.ResponseToken loginUser(User user) {
        User dbUser = findUserByEmailOrThrowException(user.getEmail());

        if (!userPasswordManager.compareUserPassword(dbUser, user)) {
            throw new BusinessLogicException(INVALID_USER);
        }

        String accessToken = tokenGenerator.generateAccessToken(dbUser);
        String refreshToken = tokenGenerator.generateRefreshToken(dbUser);
        saveRefreshToken(refreshToken, dbUser);

        return AuthDto
                .ResponseToken
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthDto.ResponseToken joinUser(User user, String authKey) {
        if (mailAuthRedisRepository.getDataAndDelete(authKey, String.class).isEmpty()) {
            throw new BusinessLogicException(INVALID_AUTH_KEY);
        }

        checkDuplicateEmail(user.getEmail());
        userPasswordManager.encryptUserPassword(user);
        saveUserAndBasicUserRole(user);

        String refreshToken = tokenGenerator.generateRefreshToken(user);
        String accessToken = tokenGenerator.generateAccessToken(user);
        saveRefreshToken(refreshToken, user);

        return AuthDto
                .ResponseToken
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Boolean logoutUser(String bearerToken) {
        String token = getTokenValue(bearerToken);
        findAndDeleteRefreshTokenOrThrowException(token);

        return Boolean.TRUE;
    }

    public AuthDto.ResponseToken reissueToken(String bearerToken) {
        tokenGenerator.tokenValidation(bearerToken);

        String token = getTokenValue(bearerToken);
        User findUser = findAndDeleteRefreshTokenOrThrowException(token);

        String refreshToken = tokenGenerator.generateRefreshToken(findUser);
        String accessToken = tokenGenerator.generateAccessToken(findUser);

        return AuthDto.ResponseToken
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public boolean sendAuthenticationMail(EmailDto.RequestSendMail emailDetails) {
        if (mailAuthRedisRepository.getData(emailDetails.getEmail(), String.class).isPresent()) {
            throw new BusinessLogicException(AUTH_KEY_ALREADY_EXISTS);
        }

        String randomKey = generateRandomKey();

        authMailSenderImpl.sendEmail(emailDetails, randomKey);
        return mailAuthRedisRepository.saveData(emailDetails.getEmail(), randomKey, AUTH_KEY_EXPIRATION_PERIOD);
    }

    public AuthDto.ResponseAuthKey authenticateEmail(EmailDto.RequestVerifyAuthKey emailDetails) {
        Optional<String> optionalAuthKey = mailAuthRedisRepository.getDataAndDelete(emailDetails.getEmail(), String.class);
        String savedAuthKey = optionalAuthKey.orElseThrow(() -> new BusinessLogicException(INVALID_AUTH_KEY));

        if (!savedAuthKey.equals(emailDetails.getAuthKey())) throw new BusinessLogicException(INVALID_AUTH_KEY);

        String randomKey = generateRandomKey();
        mailAuthRedisRepository.saveData(randomKey, emailDetails.getEmail(), VALIDITY_PERIOD_OF_THE_AUTHENTICATION_KEY);
        return new AuthDto.ResponseAuthKey(randomKey);
    }

    public Boolean checkDuplicateNickname(String nickname) {
        if (userRepository.findUserByNickname(nickname).isPresent()) throw new BusinessLogicException(NICKNAME_EXISTS);
        return Boolean.TRUE;
    }

    private String generateRandomKey() {
        Random random = new Random();
        return String.valueOf(random.nextInt(88888888) + 11111111);
    }

    private String getTokenValue(String bearerToken) {
        String[] splitToken = bearerToken.split(" ");
        String token = splitToken[1];
        return token;
    }

    private void saveRefreshToken(String refreshToken, User user) {
        String tokenValue = getTokenValue(refreshToken);
        if (!refreshTokenRedisRepository.saveData(tokenValue, user, REFRESH_TOKEN_EXPIRE_COUNT)) {
            throw new BusinessLogicException(UNABLE_TO_GENERATE_TOKEN);
        }
    }

    private User findAndDeleteRefreshTokenOrThrowException(String token) {
        return refreshTokenRedisRepository.getDataAndDelete(token, User.class).orElseThrow(() -> new BusinessLogicException(TOKEN_NOT_FOUND));
    }

    private User findUserByEmailOrThrowException(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new BusinessLogicException(USER_NOT_FOUND));
    }

    private void checkDuplicateEmail(String email) {
        if (userRepository.findUserByEmail(email).isPresent()) throw new BusinessLogicException(EMAIL_EXISTS);
    }

    private void saveUserAndBasicUserRole(User user) {
        Role role_user = roleRepository.findRoleByRoleName(ROLE_USER);

        UserRole build = UserRole.builder()
                .user(user)
                .role(role_user)
                .build();

        userRoleRepository.save(build);
    }
}
