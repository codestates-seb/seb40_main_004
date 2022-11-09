package com.morakmorak.morak_back_end.security.oauth;

import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (response.isCommitted()) {
            return;
        }

        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        String email = (String)user.getAttributes().get("email");
        User dbUser = userRepository.findUserByEmail(email).orElseThrow(() -> new BusinessLogicException(USER_NOT_FOUND));

        List<String> roles = dbUser.getUserRoles()
                .stream().map(e -> e.getRole().getRoleName().toString())
                .collect(Collectors.toList());

        String accessToken = jwtTokenUtil.createAccessToken(dbUser.getEmail(), dbUser.getId(), roles);
        String refreshToken = jwtTokenUtil.createRefreshToken(dbUser.getEmail(), dbUser.getId(), roles);

        String refreshTokenValue = refreshToken.split(" ")[1];

        refreshTokenRepository.save(new RefreshToken(refreshTokenValue));

        String url = UriComponentsBuilder.fromUriString(REDIRECT_URL_OAUTH2)
                .queryParam(ACCESS_TOKEN, accessToken)
                .queryParam(REFRESH_HEADER, refreshToken)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, url);
    }
}
