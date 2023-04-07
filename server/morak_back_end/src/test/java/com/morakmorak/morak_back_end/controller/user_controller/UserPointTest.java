package com.morakmorak.morak_back_end.controller.user_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.controller.UserController;
import com.morakmorak.morak_back_end.dto.AvatarDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.exception.webHook.ErrorNotificationGenerator;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.security.resolver.JwtArgumentResolver;
import com.morakmorak.morak_back_end.service.auth_user_service.PointService;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentRequest;
import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentResponse;
import static com.morakmorak.morak_back_end.security.util.SecurityConstants.NICKNAME;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.util.TestConstants.AVATAR;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@Import(SecurityTestConfig.class)
@WebMvcTest({UserController.class, ExceptionController.class, UserMapper.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
public class UserPointTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    PointService pointService;
    @MockBean
    UserService userService;
    @MockBean
    UserMapper userMapper;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JwtArgumentResolver jwtArgumentResolver;
    @MockBean
    ErrorNotificationGenerator errorNotificationGenerator;

    @Test
    @DisplayName("유저가 존재하지 않는 경우 포인트 조회 요청 시 404 반환")
    void getUserPoint_failed_1() throws Exception {
        //given
        Long INVALID_USERID = 1L;
        String INVALID_USER_ACCESS_TOKEN = "Invalid access token";

        BDDMockito.given(userService.findVerifiedUserById(any())).willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        BDDMockito.given(pointService.getRemainingPoint(any())).willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        ResultActions result = mockMvc.perform(get("/users/points", INVALID_USERID)
                .header(JWT_HEADER, INVALID_USER_ACCESS_TOKEN)
        );
        //then
        result.andExpect(status().isNotFound())
                .andDo(document(
                        "포인트 조회 실패_유저 없음_404",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("존재하지 않는 유저의 access token")
                        )
                ));
    }

    @Test
    @DisplayName("유효한 포인트 조회 요청인 경우 200, responsePoint 반환")
    void getUserPoint_succes_1() throws Exception {
        //given
        Long VALID_USERID = 1L;
        UserDto.ResponseSimpleUserDto userInfo = UserDto.ResponseSimpleUserDto.builder()
                .userId(VALID_USERID)
                .nickname(NICKNAME)
                .grade(Grade.CANDLE)
                .build();
        UserDto.ResponsePoint response = UserDto.ResponsePoint.builder().point(10).userInfo(userInfo).avatar(AvatarDto.SimpleResponse.of(AVATAR)).build();
        String VALID_USER_ACCESS_TOKEN = "Valid access token";

        BDDMockito.given(pointService.getRemainingPoint(any())).willReturn(response);
        //when
        ResultActions result = mockMvc.perform(get("/users/points", VALID_USERID)
                .header(JWT_HEADER, VALID_USER_ACCESS_TOKEN)
        );
        //then
        result.andExpect(status().isOk())
                .andDo(
                        document("포인트 조회 요청 성공_200",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(
                                        headerWithName(JWT_HEADER).description("액세스 토큰")
                                ),
                                responseFields(List.of(
                                        fieldWithPath("userInfo.userId").type(JsonFieldType.NUMBER).description("유저의 아이디입니다."),
                                        fieldWithPath("userInfo.nickname").type(JsonFieldType.STRING).description("유저의 닉네임입니다."),
                                        fieldWithPath("userInfo.grade").type(JsonFieldType.STRING).description("유저의 등급입니다."),
                                        fieldWithPath("avatar.avatarId").type(JsonFieldType.NUMBER).description("아바타 파일의 아이디 입니다."),
                                        fieldWithPath("avatar.filename").type(JsonFieldType.STRING).description("아바타 파일의 이름입니다."),
                                        fieldWithPath("avatar.remotePath").type(JsonFieldType.STRING).description("아바타 파일의 경로입니다."),
                                        fieldWithPath("point").description("조회하는 유저 잔여포인트"))
                                )
                        )
                );

    }
}
