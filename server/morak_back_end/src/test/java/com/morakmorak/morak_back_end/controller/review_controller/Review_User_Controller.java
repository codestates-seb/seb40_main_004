package com.morakmorak.morak_back_end.controller.review_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.controller.ReviewController;
import com.morakmorak.morak_back_end.dto.BadgeDto;
import com.morakmorak.morak_back_end.dto.ReviewDto;
import com.morakmorak.morak_back_end.entity.Badge;
import com.morakmorak.morak_back_end.entity.enums.BadgeName;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.security.resolver.JwtArgumentResolver;
import com.morakmorak.morak_back_end.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ACCESS_TOKEN;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WithMockUser
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest({ReviewController.class, ExceptionController.class})
public class Review_User_Controller {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JwtArgumentResolver jwtArgumentResolver;

    @MockBean
    ReviewService reviewService;

    Badge helpful = Badge.builder().name(BadgeName.HELPFUL).build();
    Badge smart = Badge.builder().name(BadgeName.SMART).build();
    Badge wise = Badge.builder().name(BadgeName.WISE).build();
    List<BadgeDto.SimpleBadge> validBadges = List.of(
            BadgeDto.SimpleBadge.builder().name(helpful.getName()).badgeId(1L).build(),
            BadgeDto.SimpleBadge.builder().name(smart.getName()).badgeId(2L).build(),
            BadgeDto.SimpleBadge.builder().name(wise.getName()).badgeId(3L).build()
    );

    List<BadgeDto.SimpleBadge> invalidBadges = List.of(
            BadgeDto.SimpleBadge.builder().name(helpful.getName()).badgeId(1L).build(),
            BadgeDto.SimpleBadge.builder().name(smart.getName()).badgeId(2L).build()
    );

    @Test
    @DisplayName("대시보드에서 유효한 채택 요청 성공 시 201 반환")
    public void postReview_DashBoard_success_1() throws Exception {
        //given 유효한 요청 url, 유효한 포인트, 유효한 유저, 유효한 뱃지 개수, 유효한 응답
        ReviewDto.RequestPostReview request = ReviewDto.RequestPostReview.builder().content("15글자 이상의 정성스러운 답변").badges(validBadges).point(Optional.of(10)).build();
        ReviewDto.ResponseSimpleReview response = ReviewDto.ResponseSimpleReview.builder()
                .reviewId(1L)
                .senderId(1L)
                .senderRemainingPoint(50)
                .receiverId(2L)
                .createdAt(LocalDateTime.now())
                .build();

        String content = objectMapper.writeValueAsString(request);
        given(reviewService.createReview(any(), any(), any(), any())).willReturn(response);
        //when
        ResultActions perform =
                mockMvc.perform(
                        post("/users/{user-id}/reviews",1L,1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                                .header(JWT_HEADER, ACCESS_TOKEN)
                );

        //then
        perform.andExpect(status().isCreated())
                .andDo(document(
                        "유저 리뷰 작성 및 후원_성공_201",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        HeaderDocumentation.requestHeaders(
                                headerWithName(JWT_HEADER).description("access token")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("15자 이상의 리뷰 내용입니다."),
                                        fieldWithPath("badges[].badgeId").type(JsonFieldType.NUMBER).description("선택한 뱃지 식별자입니다."),
                                        fieldWithPath("badges[].name").type(JsonFieldType.STRING).description("선택한 뱃지의 이름입니다."),
                                        fieldWithPath("point").type(JsonFieldType.NUMBER).description("보내는 포인트입니다.")
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("senderId").type(JsonFieldType.NUMBER).description("후기 작성자 아이디입니다."),
                                        fieldWithPath("senderRemainingPoint").type(JsonFieldType.NUMBER).description("후기 작성자의 잔여 포인트입니다."),
                                        fieldWithPath("reviewId").type(JsonFieldType.NUMBER).description("리뷰 아이디 입니다."),
                                        fieldWithPath("receiverId").type(JsonFieldType.NUMBER).description("받는 유저 아이디 입니다."),
                                        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("리뷰 등록일시입니다.")
                                )
                        )
                ));
    }
    @Test
    @DisplayName("유효하지 않은 포인트 전송 시 422 반환")
    public void postReview_DashBoard_failed_1() throws Exception {
        ReviewDto.RequestPostReview request = ReviewDto.RequestPostReview.builder().content("15글자 이상의 정성스러운 답변").badges(validBadges).point(Optional.of(500)).build();
        given(reviewService.createReview(any(), any(), any(), any())).willThrow(new BusinessLogicException(ErrorCode.UNPROCESSABLE_REQUEST));
        String json = objectMapper.writeValueAsString(request);

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/users/{user-id}/reviews",1L,1L)
                .content(json)
                .header(JWT_HEADER, ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
        );

        perform.andExpect(status().isUnprocessableEntity())
                .andDo(document(
                        "리뷰 작성 실패_잘못된 포인트_422",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        HeaderDocumentation.requestHeaders(
                                headerWithName(JWT_HEADER).description("access token")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("15자 이상의 리뷰 내용입니다."),
                                        fieldWithPath("badges[].badgeId").type(JsonFieldType.NUMBER).description("선택한 뱃지 식별자입니다."),
                                        fieldWithPath("badges[].name").type(JsonFieldType.STRING).description("선택한 뱃지의 이름입니다."),
                                        fieldWithPath("point").type(JsonFieldType.NUMBER).description("보내는 포인트가 유저의 잔여포인트보다 많은 경우입니다.")
                                )
                        )
                ));
    }
}
