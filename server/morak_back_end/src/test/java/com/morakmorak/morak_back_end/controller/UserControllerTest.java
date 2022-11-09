package com.morakmorak.morak_back_end.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.WebConfig;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.security.resolver.JwtArgumentResolver;
import com.morakmorak.morak_back_end.service.AuthService;
import com.morakmorak.morak_back_end.service.UserService;
import com.morakmorak.morak_back_end.util.SecurityTestConfig;
import com.morakmorak.morak_back_end.util.TestConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@Import(SecurityTestConfig.class)
@WebMvcTest({UserController.class, UserMapper.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class AuthControllerTestJoa {
    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JwtArgumentResolver jwtArgumentResolver;

    /*
    * 리퀘스트 - 닉네임, 자기소개
    * 리스폰스 - 대시보드
    *
    * 예외 상황 - 닉네임 중복 시 409 Conflict를 반환한다.
    * 예외 상황 - DTO 검증 실패 시 400 BadRequest를 반환한다.
    * */

    @Test
    @DisplayName("유저 정보 수정 시 DTO 검증에 실패한다면 400 Bad Request를 반환한다.")
    public void editUserProfile_failed() throws Exception {
        // given
        UserDto.RequestEditProfile request = UserDto.RequestEditProfile
                .builder()
                .nickname(INVALID_NICKNAME)
                .introduce(EMAIL1)
                .build();

        given(userService.editUserProfile(request)).willThrow(new BusinessLogicException(ErrorCode.ONLY_TEST_CODE));
        String json = objectMapper.writeValueAsString(request);

        // when
        ResultActions perform = mockMvc.perform(patch("/users/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        // then
        perform.andExpect(status().isBadRequest())
                .andDo(
                        document("request edit user profile /failed",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("nickname").description("유효하지 않은 값을 전송할 경우 400 코드가 반환됩니다."),
                                        fieldWithPath("introduce").description("자기소개")
                                )
                        )
                );
    }

    @Test
    @DisplayName("유저 정보 수정에 성공한다면 스테이터스 200번과 대시보드에 관련된 데이터를 반환한다.")
    public void editUserProfile_success() throws Exception {
        // given
        UserDto.RequestEditProfile request = UserDto.RequestEditProfile
                .builder()
                .nickname(NICKNAME1)
                .introduce(EMAIL1)
                .build();

        UserDto.ResponseDashBoard response = UserDto.ResponseDashBoard
                        .builder()
                                .nickname(NICKNAME1)
                                        .build();

        given(userService.editUserProfile(request)).willReturn(response);
        String json = objectMapper.writeValueAsString(request);

        // when
        ResultActions perform = mockMvc.perform(patch("/users/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(NICKNAME1))
                .andDo(
                        document("request edit user profile /success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("nickname").description("유효하지 않은 값을 전송할 경우 400 코드가 반환됩니다."),
                                        fieldWithPath("email").description("user email")
                                ),
                                responseFields(
                                        fieldWithPath("nickname").description("변경된 닉네임이 반환됩니다.")
                                )
                        )
                );
    }
}
