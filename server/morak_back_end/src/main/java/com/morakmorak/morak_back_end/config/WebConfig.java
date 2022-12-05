package com.morakmorak.morak_back_end.config;

import com.morakmorak.morak_back_end.security.resolver.JwtArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final JwtArgumentResolver jwtArgumentResolver;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOrigins("http://localhost:3000/", "http://seb40-main-004-hyeonwooga.vercel.app/", "https://seb40-main-004-hyeonwooga.vercel.app/")
                .exposedHeaders("*")
                .allowedMethods(
                        HttpMethod.GET.name(),
                        HttpMethod.PATCH.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.OPTIONS.name()
                );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(jwtArgumentResolver);
    }
}
