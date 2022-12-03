package com.morakmorak.morak_back_end.controller.auth_controller;

import com.morakmorak.morak_back_end.dto.EmailDto;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.morakmorak.morak_back_end.exception.ErrorCode.*;
import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.*;
import static com.morakmorak.morak_back_end.util.TestConstants.EMAIL1;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SendEmailForFindPasswordTest extends AuthControllerTest {
    @Test
    @DisplayName("패스워드 찾기를 요청했을 때, 해당 이메일로 등록된 계정이 존재하지 않는다면 404 Not Found를 반환한다.")
    void findPassword_failed() throws Exception {
        //given
        EmailDto.RequestSendMail request = EmailDto.RequestSendMail.builder()
                .email(EMAIL1)
                .build();

        given(authService.sendAuthenticationMailForFindPwd(EMAIL1)).willThrow(new BusinessLogicException(USER_NOT_FOUND));

        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc
                .perform(post("/auth/password/support")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));

        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("비밀번호_찾기_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").description("존재하지 않는 유저 메일일 경우")
                        ),
                        responseBody())
                );
    }

    @Test
    @DisplayName("패스워드 찾기를 요청했을 때, 해당 이메일로 등록된 계정이 존재하고 로직이 정상적으로 수행되었다면 201 created와 true를 반환한다.")
    void findPassword_success() throws Exception {
        //given
        EmailDto.RequestSendMail request = EmailDto.RequestSendMail.builder()
                .email(EMAIL1)
                .build();

        given(authService.sendAuthenticationMailForFindPwd(EMAIL1)).willReturn(Boolean.TRUE);

        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc
                .perform(post("/auth/password/support")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));

        //then
        perform.andExpect(status().isCreated())
                .andExpect(content().string(Boolean.TRUE.toString()))
                .andDo(document("비밀번호_찾기_성공_201",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").description("user email")
                        ),
                        responseBody())
                );
    }

    @Test
    @DisplayName("패스워드 찾기를 요청했을 때, 5분 이내에 해당 메일로 인증키를 요청한 기록이 있다면 409 반환")
    void findPassword_failed2() throws Exception {
        //given
        EmailDto.RequestSendMail request = EmailDto.RequestSendMail.builder()
                .email(EMAIL1)
                .build();

        given(authService.sendAuthenticationMailForFindPwd(EMAIL1)).willThrow(new BusinessLogicException(AUTH_KEY_ALREADY_EXISTS));

        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc
                .perform(post("/auth/password/support")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));

        //then
        perform.andExpect(status().isConflict())
                .andDo(document("비밀번호_찾기_실패_409",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").description("5분 내에 해당 이메일로 인증키를 요청한 이력이 있을 경우")
                        ),
                        responseBody())
                );
    }
}
