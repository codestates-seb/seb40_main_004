package com.morakmorak.morak_back_end.security.util;

import com.morakmorak.morak_back_end.security.exception.InvalidJwtTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenUtilTest {
    private JwtTokenUtil jwtTokenUtil;

    @BeforeEach
    public void init() {
        jwtTokenUtil = new JwtTokenUtil(SECRET_KEY, REFRESH_KEY);
    }

    private Claims parseClaim(String token, byte[] secretKeyByte) {
        token = token.split(" ")[1];

        return  Jwts.parserBuilder()
                .setSigningKey(getSigningKey(secretKeyByte))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey(byte[] secretKeyByte) {
        return Keys.hmacShaKeyFor(secretKeyByte);
    }

    @Test
    @DisplayName("액세스 토큰을 만들 때 사용된 인자값을 분해 시 그대로 반환한다.")
    public void test1() {
        //given
        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, ID1, ROLE_USER_ADMIN_LIST);

        //when
        Claims userInfo = parseClaim(accessToken, SECRET_KEY.getBytes());
        String email = userInfo.getSubject();
        Long id = ((Integer)userInfo.get(ID)).longValue();
        List<String> roles = (List<String>)userInfo.get(ROLES);

        //then
        assertThat(email).isEqualTo(EMAIL1);
        assertThat(id).isEqualTo(ID1);
        assertThat(roles).isEqualTo(ROLE_USER_ADMIN_LIST);
    }

    @Test
    @DisplayName("리프레시 토큰을 만들 때 사용된 인자값을 분해 시 그대로 반환한다.")
    public void test2() {
        //given
        String refreshToken = jwtTokenUtil.createRefreshToken(EMAIL1, ID1, ROLE_USER_ADMIN_LIST);

        //when
        Claims userInfo = parseClaim(refreshToken, REFRESH_KEY.getBytes());
        String email = userInfo.getSubject();
        Long id = ((Integer)userInfo.get(ID)).longValue();
        List<String> roles = (List<String>)userInfo.get(ROLES);

        //then
        assertThat(email).isEqualTo(EMAIL1);
        assertThat(id).isEqualTo(ID1);
        assertThat(roles).isEqualTo(ROLE_USER_ADMIN_LIST);
    }

    @Test
    @DisplayName("유효하지 않은 액세스 토큰을 분해 시 JwtInvalidException이 발생한다")
    public void test3() {
        //given when then
        assertThatThrownBy(() ->
                jwtTokenUtil.parseAccessToken(INVALID_BEARER_ACCESS_TOKEN))
                .isInstanceOf(InvalidJwtTokenException.class);
    }

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰을 분해 시 JwtInvalidException이 발생한다")
    public void test4() {
        //given when then
        assertThatThrownBy(() ->
                jwtTokenUtil.parseRefreshToken(INVALID_BEARER_REFRESH_TOKEN))
                .isInstanceOf(InvalidJwtTokenException.class);
    }

    @Test
    @DisplayName("기한이 지난 액세스 토큰을 분해 시 ExpiredJwtException이 발생한다")
    public void test5() throws Exception {
        //given
        Long backTime = -(14 * 24 * 60 * 60 * 1000L);
        String expiredToken = jwtTokenUtil.createToken(EMAIL1, ID1, ROLE_USER_ADMIN_LIST, backTime, SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        //when then
        assertThatThrownBy(
                () -> parseClaim(expiredToken, SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("기한이 지난 리프레시 토큰을 분해 시 ExpiredJwtException이 발생한다")
    public void test6() throws Exception {
        //given
        Long backTime = -(14 * 24 * 60 * 60 * 1000L);
        String expiredToken = jwtTokenUtil.createToken(EMAIL1, ID1, ROLE_USER_ADMIN_LIST, backTime, REFRESH_KEY.getBytes(StandardCharsets.UTF_8));

        //when then
        assertThatThrownBy(
                () -> parseClaim(expiredToken, REFRESH_KEY.getBytes(StandardCharsets.UTF_8)))
                .isInstanceOf(ExpiredJwtException.class);
    }
}