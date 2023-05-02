package com.morakmorak.morak_back_end.controller.notification_controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.ApiDocumentUtils;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.controller.BookmarkController;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.controller.NotificationController;
import com.morakmorak.morak_back_end.dto.NotificationDto;
import com.morakmorak.morak_back_end.dto.PageInfo;
import com.morakmorak.morak_back_end.dto.ResponseMultiplePaging;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.Notification;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.exception.webHook.ErrorNotificationGenerator;
import com.morakmorak.morak_back_end.security.resolver.JwtArgumentResolver;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.NotificationService;
import com.morakmorak.morak_back_end.util.SecurityTestConstants;
import com.morakmorak.morak_back_end.util.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.*;
import static com.morakmorak.morak_back_end.exception.ErrorCode.*;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@Import(SecurityTestConfig.class)
@WebMvcTest({ExceptionController.class, NotificationController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class NotificationControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JwtArgumentResolver jwtArgumentResolver;

    @MockBean
    NotificationService notificationService;
    @MockBean
    ErrorNotificationGenerator errorNotificationGenerator;
    JwtTokenUtil jwtTokenUtil;

    private final String BASE_URL = "localhost:8080.com";

    @BeforeEach
    public void init() {
        jwtTokenUtil = new JwtTokenUtil(SECRET_KEY, REFRESH_KEY);
    }

    @Test
    void getNotification_failed_404() throws Exception {
        //given
        String token = jwtTokenUtil.createAccessToken(EMAIL1, ID1, ROLE_USER_LIST,NICKNAME1);
        BDDMockito.given(notificationService.findNotificationsBy(any(), any(PageRequest.class))).willThrow(new BusinessLogicException(USER_NOT_FOUND));

        //when
        ResultActions perform = mockMvc.perform(get("/notifications?page=1&size=2")
                .header(JWT_HEADER, token));

        //then
        perform.andExpect(status().isNotFound())
                .andDo(document(
                        "알림조회_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("액세스 토큰")
                        ),
                        requestParameters(
                                parameterWithName("page").description("현재 페이지"),
                                parameterWithName("size").description("현재 사이즈")
                        )
                )
                );
    }

    @Test
    void getNotification_failed_200() throws Exception {
        //given
        String token = jwtTokenUtil.createAccessToken(EMAIL1, ID1, ROLE_USER_LIST,NICKNAME1);

        NotificationDto.SimpleResponse note1 = NotificationDto.SimpleResponse.builder()
                .notificationId(ID1)
                .message("회원님이 작성하신 \"자바 2명 타세요\" 게시글이 좋아요 10개 돌파했습니다.")
                .isChecked(Boolean.FALSE)
                .createdAt(LocalDateTime.now())
                .build();

        NotificationDto.SimpleResponse note2 = NotificationDto.SimpleResponse.builder()
                .notificationId(ID2)
                .message("회원님이 작성하신 \"자바 2명 타세요\" 게시글이 좋아요 10개 돌파했습니다.")
                .isChecked(Boolean.FALSE)
                .createdAt(LocalDateTime.now())
                .build();

        PageRequest pageable = PageRequest.of(1, 10);
        PageImpl<NotificationDto.SimpleResponse> page = new PageImpl<>(List.of(note1, note2), pageable, 2L);

        ResponseMultiplePaging<NotificationDto.SimpleResponse> response = new ResponseMultiplePaging(List.of(note1, note2), page);

        BDDMockito.given(notificationService.findNotificationsBy(any(), any(PageRequest.class))).willReturn(response);

        //when
        ResultActions perform = mockMvc.perform(get("/notifications?page=1&size=10")
                .header(JWT_HEADER, token));

        //then
        perform.andExpect(status().isOk())
                .andDo(document(
                        "알림조회_성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("액세스 토큰")
                        ),
                        requestParameters(
                                parameterWithName("page").description("현재 페이지"),
                                parameterWithName("size").description("현재 사이즈")
                        ),
                        responseFields(
                                fieldWithPath("data[].notificationId").type(NUMBER).description("알림 id"),
                                fieldWithPath("data[].message").type(STRING).description("알림 메세지"),
                                fieldWithPath("data[].isChecked").type(BOOLEAN).description("유저가 해당 메세지를 확인(클릭)했는지"),
                                fieldWithPath("data[].createdAt").type(STRING).description("알림이 생성된 일시"),
                                fieldWithPath("pageInfo.page").type(NUMBER).description("현재 페이지"),
                                fieldWithPath("pageInfo.size").type(NUMBER).description("페이즈당 사이즈"),
                                fieldWithPath("pageInfo.totalElements").type(NUMBER).description("전체 객체 수"),
                                fieldWithPath("pageInfo.totalPages").type(NUMBER).description("전체 페이지 수"),
                                fieldWithPath("pageInfo.sort.empty").type(BOOLEAN).description("정렬 조건이 없다면 true"),
                                fieldWithPath("pageInfo.sort.unsorted").type(BOOLEAN).description("정렬되지 않았다면 true"),
                                fieldWithPath("pageInfo.sort.sorted").type(BOOLEAN).description("정렬되었다면 true")
                        )
                ));
    }

    @Test
    void getNotificationUri() throws Exception {
        //given
        given(notificationService.findNotificationUriBy(ID1)).willReturn("/anyString");
        //when
        ResultActions perform = mockMvc.perform(get("/notifications/1"));
        //then
        perform.andExpect(status().isPermanentRedirect())
                .andExpect(redirectedUrl(BASE_URL + "/anyString"))
                .andDo(
                        document("알림_개별조회_성공_200",
                        getDocumentRequest(),
                        getDocumentResponse()
                )
                );
    }

    @Test
    @DisplayName("해당 Notification 객체가 존재하지 않으면 404 반환")
    void deleteNotification_failed1() throws Exception {
        //given
        String token = jwtTokenUtil.createAccessToken(EMAIL1, ID1, ROLE_USER_LIST,NICKNAME1);
        willThrow(new BusinessLogicException(NOTIFICATION_NOT_FOUND)).given(notificationService).deleteNotificationData(anyLong(), any());
        //when
        ResultActions perform = mockMvc.perform(delete("/notifications/1")
                .header(JWT_HEADER, token));
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document(
                        "알림삭제_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("해당 Notification에 대해 해당 유저의 권한이 없다면 403 Forbidden 반환")
    void deleteNotification_failed2() throws Exception {
        //given
        String token = jwtTokenUtil.createAccessToken(EMAIL1, ID1, ROLE_USER_LIST,NICKNAME1);
        willThrow(new BusinessLogicException(NO_ACCESS_TO_THAT_OBJECT)).given(notificationService).deleteNotificationData(anyLong(), any());
        //when
        ResultActions perform = mockMvc.perform(delete("/notifications/1")
                .header(JWT_HEADER, token));
        //then
        perform.andExpect(status().isForbidden())
                .andDo(document(
                        "알림삭제_실패_403",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("로직이 정상적으로 실행된다면 203 반환")
    void deleteNotification_success() throws Exception {
        //given
        String token = jwtTokenUtil.createAccessToken(EMAIL1, ID1, ROLE_USER_LIST,NICKNAME1);
        //when
        ResultActions perform = mockMvc.perform(delete("/notifications/1")
                .header(JWT_HEADER, token));
        //then
        perform.andExpect(status().isNoContent())
                .andDo(document(
                        "알림삭제_성공_203",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }
}
