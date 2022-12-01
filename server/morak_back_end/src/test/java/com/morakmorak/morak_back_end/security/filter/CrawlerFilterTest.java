package com.morakmorak.morak_back_end.security.filter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

public class CrawlerFilterTest {
    MockHttpServletRequest mockRequest;
    MockHttpServletResponse mockResponse;
    FilterChain mockFilterChain;
    CrawlerFilter crawlerFilter;

    @BeforeEach
    public void setup() {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        mockFilterChain = Mockito.mock(FilterChain.class);
        crawlerFilter = new CrawlerFilter();
    }

    @Test
    @DisplayName("user-agent가 null인 GET 요청 시 SecurityException이 발생한다.")
    void test1() throws ServletException, IOException {
        //given
        //when
        mockRequest.setMethod("GET");
        //then
        Assertions.assertThatThrownBy(() ->
                crawlerFilter.doFilter(mockRequest, mockResponse, mockFilterChain))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    @DisplayName("user-agent가 null이 아닌 요청 시 SecurityException이 발생하지 않는다.")
    void test2() throws ServletException, IOException {
        //given
        mockRequest.addHeader("user-agent", "Mozila 5.0");
        //when
        crawlerFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        //then
        BDDMockito.verify(mockFilterChain, Mockito.times(1)).doFilter(mockRequest,mockResponse);
    }

    @Test
    @DisplayName("user-agent의 키 값에 대문자가 섞여있어도 SecurityException이 발생하지 않는다")
    void test3() throws ServletException, IOException {
        //given
        mockRequest.addHeader("User-Agent", "Mozila 5.0");
        //when
        crawlerFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        //then
        BDDMockito.verify(mockFilterChain, Mockito.times(1)).doFilter(mockRequest,mockResponse);
    }
}
