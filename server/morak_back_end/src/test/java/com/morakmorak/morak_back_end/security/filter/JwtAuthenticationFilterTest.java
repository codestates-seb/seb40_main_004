package com.morakmorak.morak_back_end.security.filter;

import com.morakmorak.morak_back_end.security.exception.InvalidJwtTokenException;
import com.morakmorak.morak_back_end.security.token.JwtAuthenticationToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


public class JwtAuthenticationFilterTest {
    MockHttpServletRequest mockRequest;
    MockHttpServletResponse mockResponse;
    FilterChain mockFilterChain;
    JwtAuthenticationFilter jwtAuthenticationFilter;
    AuthenticationManager mockAuthenticationManager;

    @BeforeEach
    public void setup() {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        mockFilterChain = Mockito.mock(FilterChain.class);
        mockAuthenticationManager = Mockito.mock(AuthenticationManager.class);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(mockAuthenticationManager);
    }

    @Test
    @DisplayName("JWT 토큰이 없는 요청에는 Authentication Manager가 호출되지 않는다.")
    public void test1() throws ServletException, IOException {
        // given
        given(mockAuthenticationManager.authenticate(any())).willReturn(null);

        // when
        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // then
        verify(mockAuthenticationManager, never()).authenticate(any());
        verify(mockFilterChain, times(1)).doFilter(mockRequest, mockResponse);
    }

    @Test
    @DisplayName("Authorization의 헤더가 'Bearer '로 시작하지 않으면 인증과정을 거치지 않는다")
    public void test2() throws ServletException, IOException {
        // given
        mockRequest.addHeader(JWT_HEADER, INVALID_JWT_PREFIX);
        given(mockAuthenticationManager.authenticate(any())).willReturn(null);

        // when
        jwtAuthenticationFilter.doFilterInternal(mockRequest,mockResponse,mockFilterChain);

        // then
        verify(mockAuthenticationManager, never()).authenticate(any());
        verify(mockFilterChain, times(1)).doFilter(mockRequest,mockResponse);
    }

    @Test
    @DisplayName("유효하지 않은 JWT 토큰이 주어졌을 때, SecurityContext는 null이다.")
    public void test3() throws ServletException, IOException {
        // given
        mockRequest.addHeader(JWT_HEADER, INVALID_BEARER_ACCESS_TOKEN);
        JwtAuthenticationToken token = new JwtAuthenticationToken(INVALID_BEARER_ACCESS_TOKEN);

        given(mockAuthenticationManager.authenticate(token)).willReturn(null);

        // when
        jwtAuthenticationFilter.doFilterInternal(mockRequest,mockResponse,mockFilterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("유효하지 않은 JWT 토큰이 주어졌을 때, JwtException이 발생한다.")
    public void test4() throws Exception {
        // given
        mockRequest.addHeader(JWT_HEADER, BEARER_ACCESS_TOKEN);
        JwtAuthenticationToken token = new JwtAuthenticationToken(ACCESS_TOKEN);

        // when
        when(mockAuthenticationManager.authenticate(token)).thenThrow(new InvalidJwtTokenException(ErrorCode.EXPIRED_EXCEPTION));

        // then
        assertThatThrownBy(() ->
        jwtAuthenticationFilter.doFilterInternal(mockRequest,mockResponse,mockFilterChain)
        ).isInstanceOf(InvalidJwtTokenException.class);
    }

    @Test
    @DisplayName("유효한 토큰이 주어졌을 때, SecurityContext가 authentication을 가진다")
    public void test5() throws Exception {
        // given
        mockRequest.addHeader(JWT_HEADER, BEARER_ACCESS_TOKEN);
        JwtAuthenticationToken token = new JwtAuthenticationToken(ACCESS_TOKEN);
        JwtAuthenticationToken authenticatedToken = new JwtAuthenticationToken(
                Collections.singletonList(
                        () -> "ROLE_USER"
                ),
                EMAIL,
                null
        );

        given(mockAuthenticationManager.authenticate(token)).willReturn(authenticatedToken);

        //when
        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        //then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(authenticatedToken);
    }
}
