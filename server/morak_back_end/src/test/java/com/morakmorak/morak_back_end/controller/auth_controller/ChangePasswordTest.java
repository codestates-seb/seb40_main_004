package com.morakmorak.morak_back_end.controller.article_controller.auth_controller;

import com.morakmorak.morak_back_end.dto.AuthDto;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.morakmorak.morak_back_end.exception.ErrorCode.ONLY_TEST_CODE;
import static com.morakmorak.morak_back_end.security.util.SecurityConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentRequest;
import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentResponse;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ACCESS_TOKEN;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ChangePasswordTest extends AuthControllerTest {
    @Test
    @DisplayName("비밀번호 변경 요청 시 DTO 유효성 검증에 실패한다면 400 BadRequest 반환")
    public void requestChangePassword_failed() throws Exception {
        //given
        AuthDto.RequestChangePassword request = AuthDto.RequestChangePassword.builder()
                .originalPassword(PASSWORD1)
                .newPassword(INVALID_PASSWORD)
                .build();

        String json = objectMapper.writeValueAsString(request);

        given(authService.changePassword(anyString(), anyString(), anyLong())).willThrow(new BusinessLogicException(ONLY_TEST_CODE));

        //when
        ResultActions perform = mockMvc.perform(patch("/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, ACCESS_TOKEN));

        //then
        perform
                .andExpect(status().isBadRequest())
                .andDo(
                        document("비밀번호_변경_실패_400",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(
                                        headerWithName(JWT_HEADER).description("access token")
                                ),
                                requestFields(
                                        fieldWithPath("originalPassword").description("변경 전 패스워드"),
                                        fieldWithPath("newPassword").description("유효성 검사 실패 패스워드")
                                )
                        )
                );
    }

    @Test
    @DisplayName("비밀번호 변경 요청 시 originalPassword가 기존 패스워드와 다르다면 409 Conflict를 반환한다.")
    public void requestChangePassword_failed2() throws Exception {
        //given
        AuthDto.RequestChangePassword request = AuthDto.RequestChangePassword.builder()
                .originalPassword(PASSWORD1)
                .newPassword(PASSWORD2)
                .build();

        String json = objectMapper.writeValueAsString(request);

        given(authService.changePassword(anyString(), anyString(), any())).willThrow(new BusinessLogicException(ErrorCode.MISMATCHED_PASSWORD));

        //when
        ResultActions perform = mockMvc.perform(patch("/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, ACCESS_TOKEN));

        //then
        perform
                .andExpect(status().isConflict())
                .andDo(
                        document("비밀번호_변경_실패_409",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(
                                        headerWithName(JWT_HEADER).description("access token")
                                ),
                                requestFields(
                                        fieldWithPath("originalPassword").description("기존 패스워드(잘못된 값 입력)"),
                                        fieldWithPath("newPassword").description("변경 희망 패스워드")
                                )
                        )
                );
    }

    @Test
    @DisplayName("비밀번호 변경 요청 시 DTO 유효성 검증에 성공하고 DB 패스워드와 originalPassword가 일치한다면 200 OK와 true 반환")
    public void requestChangePassword_success() throws Exception {
        //given
        AuthDto.RequestChangePassword request = AuthDto.RequestChangePassword.builder()
                .originalPassword(PASSWORD1)
                .newPassword(PASSWORD2)
                .build();

        String json = objectMapper.writeValueAsString(request);

        given(authService.changePassword(anyString(), anyString(), any())).willReturn(Boolean.TRUE);

        //when
        ResultActions perform = mockMvc.perform(patch("/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, ACCESS_TOKEN));

        //then
        perform
                .andExpect(status().isOk())
                .andExpect(content().string(Boolean.TRUE.toString()))
                .andDo(
                        document("비밀번호_변경_성공_200",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(
                                        headerWithName(JWT_HEADER).description("access token")
                                ),
                                requestFields(
                                        fieldWithPath("originalPassword").description("변경 전 패스워드"),
                                        fieldWithPath("newPassword").description("유효성 검사 실패 패스워드")
                                ),
                                responseBody()
                        )
                );
    }
}
