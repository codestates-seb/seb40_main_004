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
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest({ReviewController.class, ExceptionController.class})
public class Review_Answer_Controller {
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
    @DisplayName("답변에 대한 유효한 채택 요청 성공 시 201 반환")
    public void postReview_Answer_success_1() throws Exception {
        //given 유효한 요청 url, 유효한 포인트, 유효한 유저, 유효한 뱃지 개수, 유효한 응답
        ReviewDto.RequestPostReview request = ReviewDto.RequestPostReview.builder().content("15글자 이상의 정성스러운 답변").badges(validBadges).point(Optional.of(10)).build();
        ReviewDto.ResponseDetailReview response = ReviewDto.ResponseDetailReview.builder()
                .reviewId(1L)
                .articleId(1L)
                .answerId(1L)
                .content("JPA는 아무잘못이없다구요. 아시겠어요? ")
                .senderId(1L)
                .senderNickname("더티체킹해주세요")
                .remainingPoint(50)
                .receiverId(2L)
                .receiverNickname("빵사고돈남겨와라")
                .createdAt(LocalDateTime.now())
                .badges(validBadges).build();

        String content = objectMapper.writeValueAsString(request);
        given(reviewService.createReview(any(), any(), any(), any(), any())).willReturn(response);
        //when
        ResultActions perform =
                mockMvc.perform(
                        post("/articles/{article-id}/answers/{answer-id}/reviews", 1L, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                                .header(JWT_HEADER, ACCESS_TOKEN)
                );

        //then
        perform.andExpect(status().isCreated())
                .andDo(document(
                        "답변 채택 및 후원_성공_201",
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
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용입니다."),
                                        fieldWithPath("remainingPoint").type(JsonFieldType.NUMBER).description("리뷰 내용입니다."),
                                        fieldWithPath("articleId").type(JsonFieldType.NUMBER).description("질문글 식별자입니다."),
                                        fieldWithPath("answerId").type(JsonFieldType.NUMBER).description("채택된 답변 식별자입니다."),
                                        fieldWithPath("senderId").type(JsonFieldType.NUMBER).description("보내는 유저 아이디입니다."),
                                        fieldWithPath("senderNickname").type(JsonFieldType.STRING).description("보내는 유저 닉네임입니다."),
                                        fieldWithPath("receiverId").type(JsonFieldType.NUMBER).description("받는 유저 아이디 입니다."),
                                        fieldWithPath("receiverNickname").type(JsonFieldType.STRING).description("받는 유저 닉네임입니다."),
                                        fieldWithPath("reviewId").type(JsonFieldType.NUMBER).description("생성된 리뷰의 식별자입니다."),
                                        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("리뷰 등록일시입니다."),
                                        fieldWithPath("badges[].badgeId").type(JsonFieldType.NUMBER).description("선택한 뱃지 식별자입니다."),
                                        fieldWithPath("badges[].name").type(JsonFieldType.STRING).description("선택한 뱃지의 이름입니다.")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("유효하지 않은 포인트 전송 시 422 반환")
    public void postReview_Answer_failed_1() throws Exception {
        ReviewDto.RequestPostReview request = ReviewDto.RequestPostReview.builder().content("15글자 이상의 정성스러운 답변").badges(validBadges).point(Optional.of(500)).build();
        given(reviewService.createReview(any(), any(), any(), any(), any())).willThrow(new BusinessLogicException(ErrorCode.UNPROCESSABLE_REQUEST));
        String json = objectMapper.writeValueAsString(request);
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/articles/{article-id}/answers/{answer-id}/reviews", 1L,1L)
                .content(json)
                .header(JWT_HEADER, ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
        );
        perform.andExpect(status().isUnprocessableEntity())
                .andDo(document(
                        "답변 채택 실패_잘못된 포인트_422",
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
    @Test
    @DisplayName("답변 작성자가 자신을 채택하려는 경우 409 반환")
    public void postReview_Answer_failed_2() throws Exception {
        ReviewDto.RequestPostReview request = ReviewDto.RequestPostReview.builder().content("15글자 이상의 정성스러운 답변").badges(validBadges).point(Optional.of(500)).build();
        given(reviewService.createReview(any(), any(), any(), any(), any())).willThrow(new BusinessLogicException(ErrorCode.UNABLE_TO_REVIEW));
        String json = objectMapper.writeValueAsString(request);
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/articles/{article-id}/answers/{answer-id}/reviews", 1L, 1L)
                .content(json)
                .header(JWT_HEADER, ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
        );
        perform.andExpect(status().isConflict())
                .andDo(document(
                        "답변 채택 실패_본인채택불가_409",
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
                                        fieldWithPath("point").type(JsonFieldType.NUMBER).description("유효한 포인트입니다.")
                                )
                        )
                ));
    }

}
