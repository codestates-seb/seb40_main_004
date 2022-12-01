package com.morakmorak.morak_back_end.security.config;

import com.morakmorak.morak_back_end.security.exception.ExceptionFilter;
import com.morakmorak.morak_back_end.security.filter.CrawlerFilter;
import com.morakmorak.morak_back_end.security.filter.JwtAuthenticationFilter;
import com.morakmorak.morak_back_end.security.provider.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class AuthenticationManagerConfig extends AbstractHttpConfigurer<AuthenticationManagerConfig, HttpSecurity> {
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    public void configure(HttpSecurity builder) {
        AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

        builder
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CrawlerFilter(), JwtAuthenticationFilter.class)
                .addFilterBefore(new ExceptionFilter(), CrawlerFilter.class)
                .authenticationProvider(jwtAuthenticationProvider);
    }
}
