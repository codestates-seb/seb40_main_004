package com.morakmorak.morak_back_end.controller.article_controller.auth_controller;

import com.morakmorak.morak_back_end.dto.AuthDto;
import com.morakmorak.morak_back_end.dto.EmailDto;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentRequest;
import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentResponse;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.AUTH_KEY;
import static com.morakmorak.morak_back_end.util.TestConstants.EMAIL1;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VerifyEmailAuthTest extends AuthControllerTest {
    @Test
    @DisplayName("전송한 인증 키가 올바르지 않을 경우 404 NOT FOUND를 반환한다")
    public void test14() throws Exception {
        //given
        EmailDto.RequestVerifyAuthKey request = EmailDto.RequestVerifyAuthKey
                .builder()
                .authKey(AUTH_KEY)
                .email(EMAIL1)
                .build();

        given(authService.authenticateEmail(anyString(), anyString())).willThrow(new BusinessLogicException(ErrorCode.INVALID_AUTH_KEY));
        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc
                .perform(put("/auth/mail")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json));

        //then
        perform
                .andExpect(status().isNotFound())
                .andDo(document(
                                "이메일_인증키_전송_404",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("authKey").description("invalid authKey"),
                                        fieldWithPath("email").description("user email")
                                )
                        )
                );
    }

    @Test
    @DisplayName("전송한 인증 키가 올바른 경우 회원가입 시 필요한 인증키와 스테이터스 코드 200을 반환한다")
    public void test15() throws Exception {
        //given
        String newAuthKey = "222222222";

        EmailDto.RequestVerifyAuthKey request = EmailDto.RequestVerifyAuthKey
                .builder()
                .authKey(AUTH_KEY)
                .email(EMAIL1)
                .build();

        AuthDto.ResponseAuthKey response = AuthDto.ResponseAuthKey
                .builder()
                .authKey(newAuthKey)
                .build();

        given(authService.authenticateEmail(anyString(), anyString())).willReturn(response);
        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc
                .perform(put("/auth/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));

        //then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authKey").value(newAuthKey))
                .andDo(document(
                                "이메일_인증_성공_200",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("authKey").description("user authKey"),
                                        fieldWithPath("email").description("user email")
                                ),
                                responseFields(
                                        fieldWithPath("authKey").description("set in user's useState And send when requestJoin")
                                )
                        )
                );
    }
}
