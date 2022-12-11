package com.morakmorak.morak_back_end.security.filter;

import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.security.exception.ExceptionFilter;
import com.morakmorak.morak_back_end.security.exception.InvalidJwtTokenException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static com.morakmorak.morak_back_end.exception.ErrorCode.*;
import static org.springframework.http.HttpStatus.*;

public class ExceptionFilterTest {
    MockHttpServletRequest request;
    MockHttpServletResponse response;
    ExceptionFilter exceptionFilter;
    FilterChain mockFilterChain;

    @BeforeEach
    void init() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        mockFilterChain = Mockito.mock(FilterChain.class);
        exceptionFilter = new ExceptionFilter();
    }

    @Test
    @DisplayName("InvalidJwtException을 catch할 경우 response로 403 status를 보낸다")
    void test1() throws ServletException, IOException {
        //given
        BDDMockito.willThrow(new InvalidJwtTokenException(MALFORMED_EXCEPTION)).given(mockFilterChain).doFilter(request,response);
        //when
        exceptionFilter.doFilter(request, response, mockFilterChain);
        //then
        Assertions.assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("SecurityException을 catch할 경우 response로 416 status를 보낸다")
    void test2() throws ServletException, IOException {
        //given
        BDDMockito.willThrow(new SecurityException()).given(mockFilterChain).doFilter(request,response);
        //when
        exceptionFilter.doFilter(request, response, mockFilterChain);
        //then
        Assertions.assertThat(response.getStatus()).isEqualTo(I_AM_A_TEAPOT.value());
    }
}
