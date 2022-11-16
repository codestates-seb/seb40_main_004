package com.morakmorak.morak_back_end.controller.auth_controller;

import com.morakmorak.morak_back_end.dto.EmailDto;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentRequest;
import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentResponse;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TemporaryPasswordTest extends AuthControllerTest {
    @Test
    @DisplayName("DTO 유효성 검증에 실패하는 경우 400 반환")
    void failed1() throws Exception {
        //given
        EmailDto.RequestVerifyAuthKey request = EmailDto.RequestVerifyAuthKey
                .builder()
                .authKey(null)
                .email(EMAIL1)
                .build();

        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc.perform(post("/auth/password/recovery")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document(
                       "임시_패스워드_실패_400",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("authKey").description("인증키")
                        )
                        )
                );
    }

    @Test
    @DisplayName("authKey가 유효하지 않은 경우 404 반환")
    void failed2() throws Exception {
        //given
        EmailDto.RequestVerifyAuthKey request = EmailDto.RequestVerifyAuthKey
                .builder()
                .authKey(AUTH_KEY)
                .email(EMAIL1)
                .build();

        String json = objectMapper.writeValueAsString(request);

        BDDMockito.given(authService.sendUserPasswordEmail(request.getEmail(), request.getAuthKey())).willThrow(new BusinessLogicException(ErrorCode.INVALID_AUTH_KEY));

        //when
        ResultActions perform = mockMvc.perform(post("/auth/password/recovery")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isNotFound())
                .andDo(document(
                                "임시_패스워드_실패_404",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("email").description("유저 이메일"),
                                        fieldWithPath("authKey").description("잘못된 인증키")
                                )
                        )
                );
    }

    @Test
    @DisplayName("유효성 검증을 통과하고 authKey가 유효한 경우 200 코드 반환")
    void success() throws Exception {
        //given
        EmailDto.RequestVerifyAuthKey request = EmailDto.RequestVerifyAuthKey
                .builder()
                .authKey(AUTH_KEY)
                .email(EMAIL1)
                .build();

        String json = objectMapper.writeValueAsString(request);

        BDDMockito.given(authService.sendUserPasswordEmail(request.getEmail(), request.getAuthKey())).willReturn(Boolean.TRUE);

        //when
        ResultActions perform = mockMvc.perform(post("/auth/password/recovery")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isOk())
                .andExpect(content().string(Boolean.TRUE.toString()))
                .andDo(document(
                                "임시_패스워드_성공_200",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("email").description("유저 이메일"),
                                        fieldWithPath("authKey").description("인증키")
                                ),
                        responseBody()
                        )
                );
    }
}
