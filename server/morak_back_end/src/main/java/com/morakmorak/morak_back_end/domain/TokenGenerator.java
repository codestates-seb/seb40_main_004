package com.morakmorak.morak_back_end.domain;

import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenGenerator {
    private final JwtTokenUtil jwtTokenUtil;

    public String generateAccessToken(User user) {
        return user.injectUserInformationForAccessToken(jwtTokenUtil);
    }

    public String generateRefreshToken(User user) {
        return user.injectUserInformationForRefreshToken(jwtTokenUtil);
    }

    public boolean tokenValidation(String refreshToken) {
        jwtTokenUtil.parseRefreshToken(refreshToken);
        return true;
    }
}
