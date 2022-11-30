package com.morakmorak.morak_back_end.service.auth_user_service;

import com.morakmorak.morak_back_end.domain.RandomKeyGenerator;
import com.morakmorak.morak_back_end.domain.TokenGenerator;
import com.morakmorak.morak_back_end.domain.UserPasswordManager;
import com.morakmorak.morak_back_end.dto.AuthDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.Role;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.UserRole;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.repository.redis.RedisRepository;
import com.morakmorak.morak_back_end.repository.user.RoleRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.repository.user.UserRoleRepository;
import com.morakmorak.morak_back_end.service.mail_service.MailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.morakmorak.morak_back_end.entity.enums.RoleName.ROLE_USER;
import static com.morakmorak.morak_back_end.exception.ErrorCode.*;
import static com.morakmorak.morak_back_end.security.util.SecurityConstants.*;
import static com.morakmorak.morak_back_end.service.mail_service.MailSenderImpl.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserPasswordManager userPasswordManager;
    private final TokenGenerator tokenGenerator;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RedisRepository<UserDto.Redis> refreshTokenStore;
    private final RedisRepository<String> mailAuthKeyStore;
    private final MailSender mailSenderImpl;
    private final UserMapper userMapper;
    private final RandomKeyGenerator randomKeyGenerator;

    public AuthDto.ResponseToken loginUser(User user) {
        User dbUser = findUserByEmailOrThrowException(user.getEmail());

        if (!userPasswordManager.comparePasswordWithUser(dbUser, user)) {
            throw new BusinessLogicException(INVALID_USER);
        }

        String accessToken = tokenGenerator.generateAccessToken(dbUser);
        String refreshToken = tokenGenerator.generateRefreshToken(dbUser);
        UserDto.Redis redisUser = userMapper.userToRedisUser(dbUser);
        saveRefreshToken(refreshToken, redisUser);

        return AuthDto
                .ResponseToken
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .avatarPath(redisUser.getAvatarPath())
                .build();
    }

    public AuthDto.ResponseToken joinUser(User user, String authKey) {
        if (mailAuthKeyStore.getDataAndDelete(authKey, String.class).isEmpty()) {
            throw new BusinessLogicException(INVALID_AUTH_KEY);
        }

        checkDuplicateEmail(user.getEmail());
        checkDuplicateNickname(user.getNickname());
        userPasswordManager.encryptUserPassword(user);
        saveUserAndBasicUserRole(user);

        String refreshToken = tokenGenerator.generateRefreshToken(user);
        String accessToken = tokenGenerator.generateAccessToken(user);

        UserDto.Redis redisUser = userMapper.userToRedisUser(user);
        saveRefreshToken(refreshToken, redisUser);

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
        UserDto.Redis redisUser = findAndDeleteRefreshTokenOrThrowException(token);

        User user = userMapper.redisUserToUser(redisUser);
        String refreshToken = tokenGenerator.generateRefreshToken(user);
        String accessToken = tokenGenerator.generateAccessToken(user);

        return AuthDto.ResponseToken
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .avatarPath(redisUser.getAvatarPath())
                .build();
    }

    public Boolean sendAuthenticationMailForJoin(String emailAddress) {
        checkDuplicateEmail(emailAddress);
        return sendAuthenticationMail(emailAddress);
    }

    public Boolean sendAuthenticationMailForFindPwd(String emailAddress) {
        findUserByEmailOrThrowException(emailAddress);
        return sendAuthenticationMail(emailAddress);
    }

    public Boolean sendAuthenticationMail(String emailAddress) {
        if (mailAuthKeyStore.getData(emailAddress, String.class).isPresent()) {
            throw new BusinessLogicException(AUTH_KEY_ALREADY_EXISTS);
        }

        String randomKey = randomKeyGenerator.generateMailAuthKey();

        mailSenderImpl.sendMail(emailAddress, randomKey, BASIC_AUTH_SUBJECT);
        return mailAuthKeyStore.saveData(emailAddress, randomKey, AUTH_KEY_EXPIRATION_PERIOD);
    }

    public AuthDto.ResponseAuthKey authenticateEmail(String emailAddress, String authKey) {
        Optional<String> optionalAuthKey = mailAuthKeyStore.getDataAndDelete(emailAddress, String.class);
        String savedAuthKey = optionalAuthKey.orElseThrow(() -> new BusinessLogicException(INVALID_AUTH_KEY));

        if (!savedAuthKey.equals(authKey)) throw new BusinessLogicException(INVALID_AUTH_KEY);

        String randomKey = randomKeyGenerator.generateMailAuthKey();
        mailAuthKeyStore.saveData(randomKey, emailAddress, VALIDITY_PERIOD_OF_THE_AUTHENTICATION_KEY);
        return new AuthDto.ResponseAuthKey(randomKey);
    }

    public Boolean changePassword(String originalPassword, String newPassword, Long userId) {
        User dbUser = userRepository.findById(userId).orElseThrow(() -> new BusinessLogicException(USER_NOT_FOUND));

        if (!userPasswordManager.comparePasswordWithPlainPassword(dbUser, originalPassword)) {
            throw new BusinessLogicException(MISMATCHED_PASSWORD);
        }

        userPasswordManager.changeUserPassword(dbUser, newPassword);

        return Boolean.TRUE;
    }

    public Boolean deleteAccount(String password, Long userId) {
        User dbUser = userRepository.findById(userId).orElseThrow(() -> new BusinessLogicException(USER_NOT_FOUND));
        User requestUser = new User(password);

        if (!userPasswordManager.comparePasswordWithUser(dbUser, requestUser)) throw new BusinessLogicException(MISMATCHED_PASSWORD);

        userRepository.delete(dbUser);
        return Boolean.TRUE;
    }

    public Boolean sendUserPasswordEmail(String emailAddress, String authKey) {
        String storedKey = mailAuthKeyStore.getDataAndDelete(emailAddress, String.class).orElseThrow(() -> new BusinessLogicException(INVALID_AUTH_KEY));
        if (!authKey.equals(storedKey)) throw new BusinessLogicException(INVALID_AUTH_KEY);

        User dbUser = findUserByEmailOrThrowException(emailAddress);
        String randomKey = randomKeyGenerator.generateTemporaryPassword();
        dbUser.changePassword(randomKey);
        userPasswordManager.encryptUserPassword(dbUser);
        return mailSenderImpl.sendMail(dbUser.getEmail(), randomKey, BASIC_PASSWORD_SUBJECT);
    }

    public Boolean checkDuplicateNickname(String nickname) {
        if (userRepository.findUserByNickname(nickname).isPresent()) throw new BusinessLogicException(NICKNAME_EXISTS);
        return Boolean.TRUE;
    }

    private String getTokenValue(String bearerToken) {
        String[] splitToken = bearerToken.split(" ");
        String token = splitToken[1];
        return token;
    }

    private void saveRefreshToken(String refreshToken, UserDto.Redis user) {
        String tokenValue = getTokenValue(refreshToken);
        if (!refreshTokenStore.saveData(tokenValue, user, REFRESH_TOKEN_EXPIRE_COUNT)) {
            throw new BusinessLogicException(UNABLE_TO_GENERATE_TOKEN);
        }
    }

    private UserDto.Redis findAndDeleteRefreshTokenOrThrowException(String token) {
        return refreshTokenStore.getDataAndDelete(token, UserDto.Redis.class).orElseThrow(() -> new BusinessLogicException(TOKEN_NOT_FOUND));
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
