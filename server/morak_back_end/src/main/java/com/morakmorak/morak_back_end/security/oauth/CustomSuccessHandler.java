package com.morakmorak.morak_back_end.security.oauth;

import com.morakmorak.morak_back_end.domain.TokenGenerator;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.Avatar;
import com.morakmorak.morak_back_end.entity.RefreshToken;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.repository.RefreshTokenRepository;
import com.morakmorak.morak_back_end.repository.redis.RedisRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.security.util.SecurityConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.morakmorak.morak_back_end.exception.ErrorCode.USER_NOT_FOUND;
import static com.morakmorak.morak_back_end.security.util.SecurityConstants.*;

@Component
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final RedisRepository<UserDto.Redis> refreshTokenStore;
    private final TokenGenerator tokenGenerator;
    private final UserMapper userMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (response.isCommitted()) {
            return;
        }

        User user = findUserBy(authentication);

        String accessToken = tokenGenerator.generateAccessToken(user);
        String refreshToken = tokenGenerator.generateRefreshToken(user);
        UserDto.Redis redisUserDto = userMapper.userToRedisUser(user);
        refreshTokenStore.saveData(refreshToken, redisUserDto, REFRESH_TOKEN_EXPIRE_COUNT);

        String avatarPath = (user.getAvatar() == null) ? "" : user.getAvatar().getRemotePath();

        String uri = createResponseUrl(accessToken, refreshToken, avatarPath);

        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private User findUserBy(Authentication authentication) {
        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        String email = (String)user.getAttributes().get("email");

        return userRepository.findUserByEmail(email).orElseThrow(() -> new BusinessLogicException(USER_NOT_FOUND));
    }

    private String createResponseUrl(String accessToken, String refreshToken, String avatarPath) {
        return UriComponentsBuilder.fromUriString(REDIRECT_URL_OAUTH2)
                .queryParam(ACCESS_TOKEN, accessToken)
                .queryParam(REFRESH_HEADER, refreshToken)
                .queryParam("avatarPath", avatarPath)
                .build()
                .toUriString();
    }
}
