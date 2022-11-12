package com.morakmorak.morak_back_end.controller.auth_controller;

import com.morakmorak.morak_back_end.dto.AuthDto;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.morakmorak.morak_back_end.exception.ErrorCode.*;
import static com.morakmorak.morak_back_end.util.ApiDocumentUtils.getDocumentRequest;
import static com.morakmorak.morak_back_end.util.ApiDocumentUtils.getDocumentResponse;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class JoinTest extends AuthControllerTest {
    @Test
    @DisplayName("회원가입하는 유저의 입력값이 검증에 실패할 경우 400 BadRequest 반환")
    public void test3() throws Exception {
        //given
        AuthDto.RequestJoin request = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .email(INVALID_EMAIL)
                .authKey(AUTH_KEY)
                .build();

        String json = objectMapper.writeValueAsString(request);

        given(authService.joinUser(any(User.class), anyString())).willThrow(new BusinessLogicException(ONLY_TEST_CODE));

        //when
        ResultActions perform = mockMvc
                .perform(post("/auth")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON));

        //then
        perform
                .andExpect(status().isBadRequest())
                .andDo(document(
                                "회원가입_실패_400",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("email").description("invalid email"),
                                        fieldWithPath("password").description("password"),
                                        fieldWithPath("nickname").description("nickname"),
                                        fieldWithPath("authKey").description("auth Key")
                                )
                        )
                );
    }

    @Test
    @DisplayName("회원가입하는 유저의 이메일이 이미 데이터베이스에 존재할 경우 409 Conflict 반환")
    public void test4() throws Exception {
        //given
        AuthDto.RequestJoin request = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .email(EMAIL1)
                .authKey(AUTH_KEY)
                .build();

        String json = objectMapper.writeValueAsString(request);
        given(authService.joinUser(any(User.class), anyString())).willThrow(new BusinessLogicException(EMAIL_EXISTS));

        //when
        ResultActions perform = mockMvc
                .perform(post("/auth")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON));

        //then
        perform
                .andExpect(status().isConflict())
                .andDo(document(
                                "회원가입_실패_409",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("email").description("exist email"),
                                        fieldWithPath("password").description("valid password"),
                                        fieldWithPath("nickname").description("valid nickname"),
                                        fieldWithPath("authKey").description("auth Key")
                                )
                        )
                );
    }


    @Test
    @DisplayName("회원가입하는 유저의 입력값이 유효하고 중복되지 않는다면 201 스테이터스 코드와 시퀀스값 반환")
    public void test6() throws Exception {
        //given
        AuthDto.RequestJoin request = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .email(EMAIL1)
                .authKey(AUTH_KEY)
                .build();

        AuthDto.ResponseToken token = AuthDto.ResponseToken.builder()
                .accessToken(BEARER_ACCESS_TOKEN)
                .refreshToken(BEARER_REFRESH_TOKEN)
                .build();

        String json = objectMapper.writeValueAsString(request);
        given(authService.joinUser(any(User.class), anyString())).willReturn(token);

        //when
        ResultActions perform = mockMvc
                .perform(post("/auth")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON));

        //then
        perform
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value(BEARER_ACCESS_TOKEN))
                .andExpect(jsonPath("$.refreshToken").value(BEARER_REFRESH_TOKEN))
                .andDo(document(
                                "회원가입_성공_201",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("email").description("valid email"),
                                        fieldWithPath("password").description("valid password"),
                                        fieldWithPath("nickname").description("valid nickname"),
                                        fieldWithPath("authKey").description("auth key")
                                ),
                                responseFields(
                                        fieldWithPath("accessToken").description("set client's local storage"),
                                        fieldWithPath("refreshToken").description("set client's cookie")
                                )
                        )
                );
    }

    @Test
    @DisplayName("회원가입하려는 유저의 인증키가 유효하지 않은 경우 404 NOT FOUND 반환")
    public void test16() throws Exception {
        //given
        AuthDto.RequestJoin request = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .email(EMAIL1)
                .authKey(AUTH_KEY)
                .build();

        String json = objectMapper.writeValueAsString(request);
        given(authService.joinUser(any(User.class), anyString())).willThrow(new BusinessLogicException(ErrorCode.INVALID_AUTH_KEY));

        //when
        ResultActions perform = mockMvc
                .perform(post("/auth")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON));

        //then
        perform
                .andExpect(status().isNotFound())
                .andDo(document(
                                "회원가입_실패_404",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("email").description("valid email"),
                                        fieldWithPath("password").description("valid password"),
                                        fieldWithPath("nickname").description("valid nickname"),
                                        fieldWithPath("authKey").description("auth key is only valid for one hour")
                                )
                        )
                );
    }

    @Test
    @DisplayName("회원가입 요청 시 동일한 닉네임이 데이터베이스에 존재한다면 409 Conflict를 반환한다.")
    public void requestJoin_failed() throws Exception {
        //given
        AuthDto.RequestJoin request = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .email(EMAIL1)
                .authKey(AUTH_KEY)
                .build();

        String json = objectMapper.writeValueAsString(request);
        given(authService.joinUser(any(User.class), anyString())).willThrow(new BusinessLogicException(NICKNAME_EXISTS));

        //when
        ResultActions perform = mockMvc
                .perform(post("/auth")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON));

        //then
        perform
                .andExpect(status().isConflict())
                .andDo(document(
                                "회원가입_실패_409_닉네임중복",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("email").description("valid email"),
                                        fieldWithPath("password").description("valid password"),
                                        fieldWithPath("nickname").description("이미 존재하는 닉네임."),
                                        fieldWithPath("authKey").description("auth Key")
                                )
                        )
                );
    }

}
