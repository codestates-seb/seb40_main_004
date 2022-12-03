package com.morakmorak.morak_back_end.controller.auth_controller;

import com.morakmorak.morak_back_end.dto.EmailDto;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.morakmorak.morak_back_end.exception.ErrorCode.AUTH_KEY_ALREADY_EXISTS;
import static com.morakmorak.morak_back_end.exception.ErrorCode.EMAIL_EXISTS;
import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.*;
import static com.morakmorak.morak_back_end.util.TestConstants.EMAIL1;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SendAuthMailTest extends AuthControllerTest {
    @Test
    @DisplayName("이메일 인증을 요청하고 5분 내에 재요청할경우 409 Conflict 반환")
    public void test12() throws Exception {
        //given
        EmailDto.RequestSendMail request = EmailDto.RequestSendMail.builder()
                .email(EMAIL1)
                .build();

        String json = objectMapper.writeValueAsString(request);
        given(authService.sendAuthenticationMailForJoin(anyString())).willThrow(new BusinessLogicException(AUTH_KEY_ALREADY_EXISTS));

        //when
        ResultActions perform = mockMvc
                .perform(post("/auth/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));

        //then
        perform
                .andExpect(status().isConflict())
                .andDo(document(
                                "이메일_인증_요청_실패_409",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("email").description("the same email can only be verified once every 5 minutes.")
                                )
                        )
                );
    }

    @Test
    @DisplayName("이메일 인증이 정상적으로 요청될 경우 201 created와 true를 반환한다.")
    public void test13() throws Exception {
        //given
        EmailDto.RequestSendMail request = EmailDto.RequestSendMail.builder()
                .email(EMAIL1)
                .build();

        String json = objectMapper.writeValueAsString(request);
        given(authService.sendAuthenticationMailForJoin(anyString())).willReturn(Boolean.TRUE);

        //when
        ResultActions perform = mockMvc
                .perform(post("/auth/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));

        //then
        perform
                .andExpect(status().isCreated())
                .andExpect(content().string(Boolean.TRUE.toString()))
                .andDo(document(
                                "이메일_인증_요청_성공_201",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("email").description("the same email can only be verified once every 5 minutes.")
                                ),
                                responseBody()
                        )
                );
    }

    @Test
    @DisplayName("이메일 인증 요청 시 해당 이메일을 가진 유저가 이미 데이터베이스에 존재한다면 409를 반환한다.")
    void requestSendAuthenticationEmail_failed_409() throws Exception {
        //given
        given(authService.sendAuthenticationMailForJoin(anyString())).willThrow(new BusinessLogicException(EMAIL_EXISTS));

        EmailDto.RequestSendMail request = EmailDto.RequestSendMail.builder().email(EMAIL1).build();

        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc.perform(post("/auth/mail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isConflict())
                .andDo(document(
                        "이메일_인증_요청_실패_409_중복",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").description("이미 존재하는 이메일")
                        )
                ));
    }
}
