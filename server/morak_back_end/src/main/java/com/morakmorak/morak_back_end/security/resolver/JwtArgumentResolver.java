package com.morakmorak.morak_back_end.security.resolver;

import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.security.util.SecurityConstants;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

import static com.morakmorak.morak_back_end.security.util.SecurityConstants.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(RequestUser.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String bearerToken = webRequest.getHeader(JWT_HEADER);

        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith(JWT_PREFIX)) {
                return null;
        }

        Claims claims = jwtTokenUtil.parseAccessToken(bearerToken);
        String subject = claims.getSubject();
        List<String> roles = (List) claims.get(ROLES);
        Long id;
        if (claims.get(ID).getClass().equals(Integer.class)) {
            id = ((Integer)claims.get(ID)).longValue();
        } else { id = (Long)claims.get(ID); }

        return UserDto.UserInfo
                .builder()
                .email(subject)
                .id(id)
                .roles(roles)
                .build();
    }
}
