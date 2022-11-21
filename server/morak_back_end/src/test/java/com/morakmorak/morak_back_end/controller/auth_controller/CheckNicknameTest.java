package com.morakmorak.morak_back_end.controller.article_controller.auth_controller;

import com.morakmorak.morak_back_end.dto.AuthDto;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.morakmorak.morak_back_end.exception.ErrorCode.NICKNAME_EXISTS;
import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentRequest;
import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentResponse;
import static com.morakmorak.morak_back_end.util.TestConstants.INVALID_NICKNAME;
import static com.morakmorak.morak_back_end.util.TestConstants.NICKNAME1;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CheckNicknameTest extends AuthControllerTest{
    @Test
    @DisplayName("중복 확인하는 닉네임이 이미 존재할 경우 409 Conflict를 반환한다.")
    public void checkDuplicateNickname_failed1() throws Exception {
        //given
        AuthDto.RequestCheckNickname dto = AuthDto.RequestCheckNickname
                .builder()
                .nickname(NICKNAME1)
                .build();

        given(authService.checkDuplicateNickname(NICKNAME1)).willThrow(new BusinessLogicException(NICKNAME_EXISTS));

        String json = objectMapper.writeValueAsString(dto);

        //when
        ResultActions perform = mockMvc.perform(post("/auth/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isConflict())
                .andDo(document(
                                "닉네임_중복_체크_실패_409",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("nickname").description("이미 존재하는 닉네임 요청")
                                )
                        )
                );
    }

    @Test
    @DisplayName("닉네임 중복 확인 요청 시 dto 검증에 실패할 경우 400 BadRequest를 반환한다.")
    public void checkDuplicateNickname_failed2() throws Exception {
        //given
        AuthDto.RequestCheckNickname dto = AuthDto.RequestCheckNickname
                .builder()
                .nickname(INVALID_NICKNAME)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        //when
        ResultActions perform = mockMvc.perform(post("/auth/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document(
                                "닉네임_중복확인_유효하지_않은_요청값_400",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("nickname").description("유효성 검사 실패, 닉네임은 null이거나 공백일 수 없으며 닉네임 규정을 충족해야합니다.")
                                )
                        )
                );
    }

    @Test
    @DisplayName("닉네임 중복 확인 요청 시 중복 닉네임이 존재하지 않고 dto 유효성 검사에 성공할 경우 true와 200 OK를 반환한다.")
    public void checkDuplicateNickname_success() throws Exception {
        //given
        AuthDto.RequestCheckNickname dto = AuthDto.RequestCheckNickname
                .builder()
                .nickname(NICKNAME1)
                .build();

        given(authService.checkDuplicateNickname(NICKNAME1)).willReturn(Boolean.TRUE);

        String json = objectMapper.writeValueAsString(dto);

        //when
        ResultActions perform = mockMvc.perform(post("/auth/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isOk())
                .andExpect(content().string(Boolean.TRUE.toString()))
                .andDo(document(
                                "닉네임_중복확인_성공_200",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("nickname").description("중복되지 않는 닉네임")
                                ),
                                responseBody()
                        )
                );
    }
}
