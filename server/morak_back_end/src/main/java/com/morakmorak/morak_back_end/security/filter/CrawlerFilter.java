package com.morakmorak.morak_back_end.security.filter;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CrawlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isMethodEqualsGet(request)) {
            String userAgent = request.getHeader("user-agent");
            if (!StringUtils.hasText(userAgent)) throw new SecurityException();
        }

        filterChain.doFilter(request, response);
    }

    private boolean isMethodEqualsGet(HttpServletRequest request) {
        return request.getMethod().equals("GET");
    }
}
