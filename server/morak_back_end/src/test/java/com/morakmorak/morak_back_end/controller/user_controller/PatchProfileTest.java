package com.morakmorak.morak_back_end.controller.user_controller;

import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.JobType;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentRequest;
import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentResponse;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ROLE_USER_LIST;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.ID1;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PatchProfileTest extends UserControllerTest {
    @Test
    @DisplayName("해당 유저가 존재하지 않을 경우 404 예외 반환")
    public void patchUserProfile_failed1() throws Exception {
        // given
        UserDto.SimpleEditProfile request = UserDto.SimpleEditProfile.builder()
                .nickname(NICKNAME2)
                .blog(TISTORY_URL)
                .github(GITHUB_URL)
                .infoMessage(CONTENT1)
                .jobType(JobType.DEVELOPER)
                .build();

        String json = objectMapper.writeValueAsString(request);

        BDDMockito.given(userService.editUserProfile(any(User.class), any())).willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        String token = jwtTokenUtil.createAccessToken(EMAIL1, ID1, ROLE_USER_LIST);

        // when
        ResultActions perform = mockMvc.perform(patch("/users/profiles")
                .header(JWT_HEADER, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("회원정보_수정_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("infoMessage").description("자기소개"),
                                fieldWithPath("github").description("깃허브 주소"),
                                fieldWithPath("blog").description("블로그 주소"),
                                fieldWithPath("jobType").description("직업")
                        )
                ));
    }

    @Test
    @DisplayName("해당 닉네임이 이미 존재할 경우 409 반환")
    public void patchUserProfile_failed3() throws Exception {
        // given
        UserDto.SimpleEditProfile request = UserDto.SimpleEditProfile.builder()
                .nickname(NICKNAME2)
                .blog(TISTORY_URL)
                .github(GITHUB_URL)
                .infoMessage(CONTENT1)
                .jobType(JobType.DEVELOPER)
                .build();

        String json = objectMapper.writeValueAsString(request);

        BDDMockito.given(userService.editUserProfile(any(User.class), any())).willThrow(new BusinessLogicException(ErrorCode.NICKNAME_EXISTS));
        String token = jwtTokenUtil.createAccessToken(EMAIL1, ID1, ROLE_USER_LIST);

        // when
        ResultActions perform = mockMvc.perform(patch("/users/profiles")
                .header(JWT_HEADER, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isConflict())
                .andDo(document("회원정보_수정_실패_409",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("infoMessage").description("자기소개"),
                                fieldWithPath("github").description("깃허브 주소"),
                                fieldWithPath("blog").description("블로그 주소"),
                                fieldWithPath("jobType").description("직업")
                        )
                ));
    }

    @Test
    @DisplayName("프로필 수정 요청이 유효성 검증을 통과하지 못할 경우 400 상태코드 반환")
    public void patchUserProfile_failed2() throws Exception {
        // given
        UserDto.SimpleEditProfile request = UserDto.SimpleEditProfile.builder()
                .nickname(INVALID_NICKNAME)
                .blog(TISTORY_URL)
                .github(GITHUB_URL)
                .infoMessage(CONTENT1)
                .jobType(JobType.DEVELOPER)
                .build();

        String json = objectMapper.writeValueAsString(request);

        BDDMockito.given(userService.editUserProfile(any(User.class), any())).willThrow(new BusinessLogicException(ErrorCode.ONLY_TEST_CODE));
        String token = jwtTokenUtil.createAccessToken(EMAIL1, ID1, ROLE_USER_LIST);

        // when
        ResultActions perform = mockMvc.perform(patch("/users/profiles")
                .header(JWT_HEADER, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("회원정보_수정_실패_400",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("nickname").description("유효하지 않은 닉네임(null 혹은 웹 어플리케이션 규칙에 맞지 않는 경우)"),
                                fieldWithPath("infoMessage").description("유효성 검증 대상 아님"),
                                fieldWithPath("github").description("유효성 검증 대상 아님"),
                                fieldWithPath("blog").description("유효성 검증 대상 아님"),
                                fieldWithPath("jobType").description("유효하지 않은 직업(미리 협의된 대문자 상수만 가능, 혹은 null인 경우)")
                        )
                ));
    }

    @Test
    @DisplayName("해당 유저가 존재하고 유효성 검증에 통과할 경우 200 OK와 변경된 내역에 대한 반환")
    public void patchUserProfile_success() throws Exception {
        // given
        UserDto.SimpleEditProfile request = UserDto.SimpleEditProfile.builder()
                .nickname(NICKNAME2)
                .blog(TISTORY_URL)
                .github(GITHUB_URL)
                .infoMessage(CONTENT1)
                .jobType(JobType.DEVELOPER)
                .build();

        String json = objectMapper.writeValueAsString(request);

        BDDMockito.given(userService.editUserProfile(any(User.class), any())).willReturn(request);
        String token = jwtTokenUtil.createAccessToken(EMAIL1, ID1, ROLE_USER_LIST);

        // when
        ResultActions perform = mockMvc.perform(patch("/users/profiles")
                .header(JWT_HEADER, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(request.getNickname()))
                .andExpect(jsonPath("$.blog").value(request.getBlog()))
                .andExpect(jsonPath("$.github").value(request.getGithub()))
                .andExpect(jsonPath("$.infoMessage").value(request.getInfoMessage()))
                .andExpect(jsonPath("$.jobType").value(request.getJobType().toString()))
                .andDo(document("회원정보_수정_성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("infoMessage").description("자기소개"),
                                fieldWithPath("github").description("깃허브 주소"),
                                fieldWithPath("blog").description("블로그 주소"),
                                fieldWithPath("jobType").description("직업")
                        ),
                        responseFields(
                                fieldWithPath("nickname").description("수정 후 닉네임"),
                                fieldWithPath("infoMessage").description("수정 후 자기소개"),
                                fieldWithPath("github").description("수정 후 깃허브 주소"),
                                fieldWithPath("blog").description("수정 후 블로그 주소"),
                                fieldWithPath("jobType").description("수정 후 직업")
                        )
                ));
    }
}
