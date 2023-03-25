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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.http.HttpMethod.*;

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
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .apply(authenticationManagerConfig)
                .and()
                .httpBasic().disable()
                .authorizeHttpRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .mvcMatchers("/test/all/**").permitAll()
                .mvcMatchers("/auth/**").permitAll()
                .mvcMatchers("/login/**").permitAll()
                .mvcMatchers("/oauth/**").permitAll()
                .mvcMatchers("/cra/**").permitAll()
                .mvcMatchers(GET, "/users/**").permitAll()
                .mvcMatchers(GET, "/calendars/**").permitAll()
                .mvcMatchers("/test/user/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .mvcMatchers("/test/manager/**").hasAnyRole("MANAGER", "ADMIN")
                .mvcMatchers("/test/admin/**").hasAnyRole("ADMIN")
                .mvcMatchers(GET, "/articles/**").permitAll()
                .mvcMatchers(POST, "/articles/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .mvcMatchers(PUT, "/articles/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .mvcMatchers(PATCH, "/articles/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .mvcMatchers(DELETE, "/articles/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .mvcMatchers(POST, "/users/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .mvcMatchers(PUT, "/users/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .mvcMatchers(PATCH, "/users/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .mvcMatchers(DELETE, "/users/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .mvcMatchers(GET, "/files/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .mvcMatchers(POST, "/files/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .mvcMatchers(PUT, "/files/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .mvcMatchers(DELETE, "/files/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .mvcMatchers(GET, "/notifications/**").permitAll()
                .mvcMatchers(DELETE, "/notifications/**").permitAll()
                .mvcMatchers(GET,"/answers/**").permitAll()
                .mvcMatchers(POST,"/answers/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .mvcMatchers(PATCH,"/answers/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .mvcMatchers(PUT,"/answers/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .anyRequest().denyAll()
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000/");
        config.addAllowedOrigin("https://morakmorak.vercel.app/");
        config.addAllowedHeader("*");
        config.setAllowedMethods(List.of("GET", "POST", "DELETE", "PATCH", "PUT", "OPTION"));
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}

