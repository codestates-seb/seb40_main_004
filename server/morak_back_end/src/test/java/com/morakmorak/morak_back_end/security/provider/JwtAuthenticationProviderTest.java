package com.morakmorak.morak_back_end.security.provider;

import com.morakmorak.morak_back_end.security.exception.InvalidJwtTokenException;
import com.morakmorak.morak_back_end.security.token.JwtAuthenticationToken;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.morakmorak.morak_back_end.security.util.SecurityConstants.*;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ID;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_PREFIX;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ROLES;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtAuthenticationProviderTest {
    JwtAuthenticationProvider provider;

    @BeforeEach
    public void setup() {
        provider = new JwtAuthenticationProvider(new JwtTokenUtil(SECRET_KEY, REFRESH_KEY));
    }

    private String createToken(String email, Long id, List<String> roles, Date now, Long expire, String secretKey) {
        Claims claims = Jwts
                .claims()
                .setSubject(email);

        claims.put(ID, id);
        claims.put(ROLES, roles);
        return JWT_PREFIX + Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_COUNT))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    @Test
    @DisplayName("JwtAuthenticationProvider는 JwtAuthenticationToken에 대한 처리만 지원한다 / 실패")
    public void test1() {
        //given when then
        assertThat(provider.supports(UsernamePasswordAuthenticationToken.class)).isFalse();
        assertThat(provider.supports(AbstractAuthenticationToken.class)).isFalse();
        assertThat(provider.supports(Authentication.class)).isFalse();
    }

    @Test
    @DisplayName("JwtAuthenticationProvider는 JwtAuthenticationToken에 대한 처리만 지원한다 / 성공")
    public void test2() {
        //given when then
        assertThat(provider.supports(JwtAuthenticationToken.class)).isTrue();
    }

    @Test
    @DisplayName("만료된 토큰을 받을 받았을 때, JwtInvalidException이 발생한다.")
    public void test3() {
        // given
        Date past = new Date(System.currentTimeMillis() - REFRESH_TOKEN_EXPIRE_COUNT);
        String invalidToken = createToken(EMAIL1, ID1, ROLE_USER_ADMIN_LIST, past, ACCESS_TOKEN_EXPIRE_COUNT, SECRET_KEY);
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(invalidToken);

        // when then
        assertThatThrownBy(() -> provider.authenticate(authenticationToken))
                .isInstanceOf(InvalidJwtTokenException.class);
    }

    @Test
    @DisplayName("잘못된 시크릿 키를 가진 토큰을 받았을 때, JwtInvalidExeption이 발생한다.")
    public void test4() {
        //given
        String invalidToken = createToken(EMAIL1, ID1, ROLE_USER_ADMIN_LIST, new Date(), ACCESS_TOKEN_EXPIRE_COUNT, INVALID_SECRET_KEY);
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(invalidToken);

        //when then
        assertThatThrownBy(() -> provider.authenticate(authenticationToken))
                .isInstanceOf(InvalidJwtTokenException.class);
    }

    @Test
    @DisplayName("잘못된 형태의 토큰을 받았을 때, JwtInvalidException이 발생한다.")
    public void test5() {
        //given
        String malformedToken = "Bearer blah blah token";
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(malformedToken);

        //when then
        assertThatThrownBy(() -> provider.authenticate(authenticationToken))
                .isInstanceOf(InvalidJwtTokenException.class);
    }

    @Test
    @DisplayName("올바른 형태의 토큰을 받았을 때, 인증 정보를 반환한다.")
    public void test6() {
        //given
        String token = createToken(EMAIL1, ID1, ROLE_USER_LIST, new Date(), ACCESS_TOKEN_EXPIRE_COUNT, SECRET_KEY);
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(token);

        //when
        Authentication authenticate = provider.authenticate(authenticationToken);

        //then
        assertThat(authenticate.getPrincipal()).isEqualTo(EMAIL1);
        assertThat(authenticate.getCredentials()).isNull();
        Collection<? extends GrantedAuthority> authorities = authenticate.getAuthorities();
        authorities.forEach(e ->
                assertThat(e.getAuthority()).isEqualTo(ROLE_USER));
    }
}