package com.morakmorak.morak_back_end.security.resolver;

import com.morakmorak.morak_back_end.controller.TestController;
import com.morakmorak.morak_back_end.security.exception.InvalidJwtTokenException;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.NestedServletException;

import static com.morakmorak.morak_back_end.exception.ErrorCode.*;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.INVALID_BEARER_ACCESS_TOKEN;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.EMAIL1;
import static com.morakmorak.morak_back_end.util.TestConstants.ID1;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityTestConfig.class)
@WebMvcTest({JwtArgumentResolver.class, TestController.class})
@MockBean(JpaMetamodelMappingContext.class)
public class JwtArgumentResolverTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    JwtTokenUtil jwtTokenUtil;

    @Test
    @DisplayName("@RequestUser 어노테이션은 있으나 헤더에 토큰이 없는 경우 null을 반환한다. (해당 컨트롤러는 반환값이 있으므로 예외 발생)")
    public void test1() throws Exception {
        //given
        //when
        //then
        Assertions.assertThatThrownBy(() -> mockMvc.perform(post("/test/resolver")))
                .isInstanceOf(NestedServletException.class);
    }

    @Test
    @DisplayName("@RequestUser 어노테이션이 있고, 유효하지 않은 형태의 토큰이 입력되는 경우 401 UnAuthorized 반환")
    public void test2() throws Exception {
        //given
        given(jwtTokenUtil.parseAccessToken(INVALID_BEARER_ACCESS_TOKEN))
                .willThrow(new InvalidJwtTokenException(MALFORMED_EXCEPTION));
        //when
        ResultActions perform = mockMvc.perform(post("/test/resolver")
                .header(JWT_HEADER, INVALID_BEARER_ACCESS_TOKEN));

        //then
        perform
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("@RequestUser 어노테이션이 있고, 유효한 형태의 토큰이 입력되는 경우 UserInfo 반환")
    public void test3() throws Exception {
        //given
        Claims claims = Jwts.claims().setSubject(EMAIL1);
        claims.put(ROLES, ROLE_USER_LIST);
        claims.put(ID, ID1);
        given(jwtTokenUtil.parseAccessToken(BEARER_ACCESS_TOKEN)).willReturn(claims);

        //when
        ResultActions perform = mockMvc.perform(post("/test/resolver")
                .header(JWT_HEADER, BEARER_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(content().string(EMAIL1));
    }
}
