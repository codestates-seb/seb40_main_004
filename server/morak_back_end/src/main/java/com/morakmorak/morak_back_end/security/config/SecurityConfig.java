package com.morakmorak.morak_back_end.security.config;

import com.morakmorak.morak_back_end.security.exception.CustomAuthenticationEntryPoint;
import com.morakmorak.morak_back_end.security.oauth.CustomOauth2Service;
import com.morakmorak.morak_back_end.security.oauth.CustomSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsUtils;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationManagerConfig authenticationManagerConfig;
    private final CustomOauth2Service customOauth2Service;
    private final CustomSuccessHandler customSuccessHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .headers().frameOptions().disable()
                .and()
                .csrf().disable()
                .cors().disable()
                .apply(authenticationManagerConfig)
                .and()
                .httpBasic().disable()
                .authorizeHttpRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .mvcMatchers("/test/all/**").permitAll()
                .mvcMatchers("/test/user/**").hasAnyRole("USER","MANAGER","ADMIN")
                .mvcMatchers("/test/manager/**").hasAnyRole("MANAGER","ADMIN")
                .mvcMatchers("/test/admin/**").hasAnyRole("ADMIN")
                .anyRequest().permitAll()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .and()
                .oauth2Login()
                .successHandler(customSuccessHandler)
                .userInfoEndpoint()
                .userService(customOauth2Service);

        return http.build();
    }
}
