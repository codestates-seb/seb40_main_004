package com.morakmorak.morak_back_end.controller.auth_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.controller.AuthController;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.security.resolver.JwtArgumentResolver;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.AuthService;
import com.morakmorak.morak_back_end.util.SecurityTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.morakmorak.morak_back_end.exception.ErrorCode.TOKEN_NOT_FOUND;
import static com.morakmorak.morak_back_end.security.util.SecurityConstants.REFRESH_HEADER;
import static com.morakmorak.morak_back_end.util.ApiDocumentUtils.getDocumentRequest;
import static com.morakmorak.morak_back_end.util.ApiDocumentUtils.getDocumentResponse;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LogoutTest extends AuthControllerTest {

    @Test
    @DisplayName("리프레시 토큰이 존재하지 않을 시 404 NotFound를 반환")
    public void test7() throws Exception {
        //given
        given(authService.logoutUser(BEARER_REFRESH_TOKEN)).willThrow(new BusinessLogicException(TOKEN_NOT_FOUND));

        //when
        ResultActions perform = mockMvc
                .perform(delete("/auth/token")
                        .header(REFRESH_HEADER, BEARER_REFRESH_TOKEN));

        //then
        perform
                .andExpect(status().isNotFound())
                .andDo(document(
                                "로그아웃_실패_404",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(
                                        headerWithName(REFRESH_HEADER).description("Not Found RefreshToken")
                                )
                        )
                );
    }

    @Test
    @DisplayName("리프레시 토큰이 존재하고 정상적으로 로직이 수행됐을 경우 204 NoContent를 반환한다.")
    public void test8() throws Exception {
        //given
        given(authService.logoutUser(BEARER_REFRESH_TOKEN)).willReturn(Boolean.TRUE);

        //when
        ResultActions perform = mockMvc
                .perform(delete("/auth/token")
                        .header(REFRESH_HEADER, BEARER_REFRESH_TOKEN));

        //then
        perform
                .andExpect(status().isNoContent())
                .andDo(document(
                                "로그아웃_성공_204",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(
                                        headerWithName(REFRESH_HEADER).description("valid refreshToken")
                                )
                        )
                );
    }
}
