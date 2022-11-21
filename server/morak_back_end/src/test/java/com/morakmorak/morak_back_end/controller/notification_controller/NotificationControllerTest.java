package com.morakmorak.morak_back_end.controller.notification_controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.ApiDocumentUtils;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.controller.BookmarkController;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.dto.NotificationDto;
import com.morakmorak.morak_back_end.entity.Notification;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.security.resolver.JwtArgumentResolver;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.util.SecurityTestConstants;
import com.morakmorak.morak_back_end.util.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.BDDMockito;
import org.mockito.Mock;
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

import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.*;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.awaitility.Awaitility.given;

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

    JwtTokenUtil jwtTokenUtil;

    @BeforeEach
    public void init() {
        jwtTokenUtil = new JwtTokenUtil(SECRET_KEY, REFRESH_KEY);
    }

    @Mock
    NotificationService notificationService;

    void getNotification_failed_404() throws Exception {
        //given
        BDDMockito.given(notificationService.findNotificationsBy(any())).willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        //when
        ResultActions perform = mockMvc.perform(get("/notifications/1"));

        //then
        perform.andExpect(status().isNotFound())
                .andDo(document(
                        "알림조회_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("액세스 토큰")
                        )
                )
                );
    }

    void getNotification_failed_200() throws Exception {
        //given
        User user = User.builder().id(ID1).build();

        NotificationDto.SimpleResponse note1 = NotificationDto.SimpleResponse.builder()
                .id(ID1)
                .message("회원님이 작성하신 \"자바 2명 타세요\" 게시글이 좋아요 10개 돌파했습니다.")
                .isChecked(Boolean.FALSE)
                .build();

        NotificationDto.SimpleResponse note2 = NotificationDto.SimpleResponse.builder()
                .id(ID2)
                .message("회원님이 작성하신 \"자바 2명 타세요\" 게시글이 좋아요 10개 돌파했습니다.")
                .isChecked(Boolean.FALSE)
                .build();

        NotificationDto.ResponseList list = NotificationDto.ResponseList.builder()
                .notifications(List.of(note1, note2))
                .checkedNotifications(0)
                .unCheckedNotifications(2)
                .total(2)
                .build();

        BDDMockito.given(notificationService.findNotificationsBy(any())).willReturn(list);

        //when
        ResultActions perform = mockMvc.perform(get("/notification/1"));

        //then
        perform.andExpect(status().isOk())
                .andDo(document(
                        "알림조회_성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("notifications[].id").type(JsonFieldType.NUMBER).description("알림 id"),
                                fieldWithPath("notifications[].message").type(JsonFieldType.NUMBER).description("알림 메세지"),
                                fieldWithPath("notifications[].isChecked").type(JsonFieldType.NUMBER).description("유저가 해당 메세지를 확인(클릭)했는지"),
                                fieldWithPath("checkedNotifications").type(JsonFieldType.NUMBER).description("확인한 알림 수"),
                                fieldWithPath("unCheckedNotifications").type(JsonFieldType.NUMBER).description("확인하지 않은 알림 수"),
                                fieldWithPath("total").type(JsonFieldType.NUMBER).description("전체 알림 수")
                        )
                ));
    }
}
