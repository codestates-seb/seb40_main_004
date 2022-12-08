package com.morakmorak.morak_back_end.security.exception;

import com.morakmorak.morak_back_end.security.util.SecurityConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.morakmorak.morak_back_end.security.util.SecurityConstants.*;
import static org.springframework.http.HttpStatus.*;

@Component
public class ExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (InvalidJwtTokenException e) {
            setStatus(response, UNAUTHORIZED.value());
            response.getWriter().write(e.getErrorCode().getMessage());
        } catch (SecurityException e) {
            setStatus(response, I_AM_A_TEAPOT.value());
            response.getWriter().write(DO_NOT_CRAWL);
        }
    }

    private void setStatus(HttpServletResponse response, int statusCode) {
        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
    }
}
