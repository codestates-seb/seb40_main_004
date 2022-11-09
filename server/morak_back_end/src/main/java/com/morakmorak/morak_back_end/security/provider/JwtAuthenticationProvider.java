package com.morakmorak.morak_back_end.security.provider;

import com.morakmorak.morak_back_end.security.token.JwtAuthenticationToken;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.morakmorak.morak_back_end.security.util.SecurityConstants.ROLES;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Claims claims = jwtTokenUtil.parseAccessToken(String.valueOf(((JwtAuthenticationToken) authentication).getJwtToken()));
        String email = claims.getSubject();
        List<String> roles = (List) claims.get(ROLES);

        List<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach( e ->
                authorities.add(() -> e));

        return new JwtAuthenticationToken(authorities, email, null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
