package com.morakmorak.morak_back_end.controller.auth_controller;

import com.morakmorak.morak_back_end.dto.AuthDto;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.security.exception.InvalidJwtTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import static com.morakmorak.morak_back_end.exception.ErrorCode.TOKEN_NOT_FOUND;
import static com.morakmorak.morak_back_end.security.util.SecurityConstants.REFRESH_HEADER;
import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.*;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ReissueTest extends AuthControllerTest {
    @Test
    @DisplayName("요청받은 리프레시 토큰이 존재하지 않을 때 404 NotFound를 반환한다.")
    public void test9() throws Exception {
        //given
        given(authService.reissueToken(BEARER_REFRESH_TOKEN)).willThrow(new BusinessLogicException(TOKEN_NOT_FOUND));

        //when
        ResultActions perform = mockMvc
                .perform(put("/auth/token")
                        .header(REFRESH_HEADER, BEARER_REFRESH_TOKEN));

        //then
        perform.andExpect(status().isNotFound())
                .andDo(
                        document("리프레시_토큰_갱신_실패_404",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(
                                        headerWithName(REFRESH_HEADER).description("that token dose not exist in the database")
                                ))
                );
    }

    @Test
    @DisplayName("요청받은 리프레시 토큰이 유효하지 않을 때 401 UnAuthorized를 반환한다.")
    public void test10() throws Exception {
        //given
        given(authService.reissueToken(BEARER_REFRESH_TOKEN)).willThrow(new InvalidJwtTokenException(ErrorCode.EXPIRED_EXCEPTION));

        //when
        ResultActions perform = mockMvc
                .perform(put("/auth/token")
                        .header(REFRESH_HEADER, BEARER_REFRESH_TOKEN));

        //then
        perform.andExpect(status().isUnauthorized())
                .andDo(document(
                        "리프래시_토큰_갱신_실패_401",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(REFRESH_HEADER).description("invalid token(expired, malformed, unsupported, signature, illegalArgument..)")
                        ))
                );
    }

    @Test
    @DisplayName("요청받은 리프레시 토큰이 유효하고 데이터베이스에 존재하는 경우 201 Created와 함께 RefreshToken/AccessToken을 반환한다.")
    public void test11() throws Exception {
        //given
        AuthDto.ResponseToken response = AuthDto.ResponseToken
                .builder()
                .accessToken(BEARER_ACCESS_TOKEN)
                .refreshToken(BEARER_REFRESH_TOKEN)
                .avatarPath("http://image/image.jpg/test")
                .build();

        given(authService.reissueToken(BEARER_REFRESH_TOKEN)).willReturn(response);

        //when
        ResultActions perform = mockMvc
                .perform(put("/auth/token")
                        .header(REFRESH_HEADER, BEARER_REFRESH_TOKEN));

        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value(BEARER_ACCESS_TOKEN))
                .andExpect(jsonPath("$.refreshToken").value(BEARER_REFRESH_TOKEN))
                .andDo(document(
                                "리프레시_토큰_갱신_성공_201",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(
                                        headerWithName(REFRESH_HEADER).description("valid token")
                                ),
                                responseFields(
                                        fieldWithPath("accessToken").description("set client's localStorage"),
                                        fieldWithPath("refreshToken").description("set client's cookie"),
                                        fieldWithPath("avatarPath").description("user profile image")
                                )
                        )
                );
    }

}
