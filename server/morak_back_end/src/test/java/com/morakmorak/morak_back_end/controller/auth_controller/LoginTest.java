package com.morakmorak.morak_back_end.controller.auth_controller;

import com.morakmorak.morak_back_end.dto.AuthDto;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.morakmorak.morak_back_end.exception.ErrorCode.INVALID_USER;
import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentRequest;
import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentResponse;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.BEARER_REFRESH_TOKEN;
import static com.morakmorak.morak_back_end.util.TestConstants.EMAIL1;
import static com.morakmorak.morak_back_end.util.TestConstants.PASSWORD1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LoginTest extends AuthControllerTest {
    @Test
    @DisplayName("로그인 / 입력된 아이디 혹은 패스워드가 유효하지 않을 경우 401 UNAUTHORIZED 반환")
    public void requestLogin1() throws Exception {
        // given
        AuthDto.RequestLogin requestLogin = AuthDto.RequestLogin
                .builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        String json = objectMapper.writeValueAsString(requestLogin);

        given(authService.loginUser(any(User.class))).willThrow(new BusinessLogicException(INVALID_USER));

        // when
        ResultActions perform = mockMvc
                .perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                );

        // then
        perform.andExpect(status().isUnauthorized())
                .andDo(document("로그인_실패_401",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("email").description("Invalid email or not found email"),
                                        fieldWithPath("password").description("Invalid password or not found email")
                                )
                        )
                );
    }

    @Test
    @DisplayName("로그인 / 입력된 아이디 및 패스워드가 유효한 경우 AccessToken과 RefreshToken 반환 반환")
    public void requestLogin2() throws Exception {
        // given
        AuthDto.RequestLogin requestLogin = AuthDto.RequestLogin
                .builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        String json = objectMapper.writeValueAsString(requestLogin);

        AuthDto.ResponseToken responseToken = AuthDto.ResponseToken.builder()
                .accessToken(BEARER_ACCESS_TOKEN)
                .refreshToken(BEARER_REFRESH_TOKEN)
                .avatarPath("http://image/image.jpg/test")
                .build();

        given(authService.loginUser(any(User.class))).willReturn(responseToken);

        // when
        ResultActions perform = mockMvc
                .perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                );

        // then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value(BEARER_ACCESS_TOKEN))
                .andExpect(jsonPath("$.refreshToken").value(BEARER_REFRESH_TOKEN))
                .andDo(document("로그인_성공_201",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("email").description("valid user email"),
                                        fieldWithPath("password").description("valid user password")
                                ),
                                responseFields(
                                        fieldWithPath("accessToken").description("accessToken, set client's LocalStorage"),
                                        fieldWithPath("refreshToken").description("refreshToken, set client's Cookie"),
                                        fieldWithPath("avatarPath").description("profile image remote path")
                                )
                        )
                );
    }
}
