package com.morakmorak.morak_back_end.controller.user_controller;

import com.morakmorak.morak_back_end.dto.AvatarDto;
import com.morakmorak.morak_back_end.dto.ResponseMultiplePaging;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.entity.enums.JobType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentRequest;
import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentResponse;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ONE;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.TWO;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RankingTest extends UserControllerTest {
    @Test
    @DisplayName("유저 랭킹 조회 시 200Ok와 리스트를 반환한다.")
    void getRank() throws Exception {
        //given
        AvatarDto.SimpleResponse avatar = AvatarDto.SimpleResponse.builder().avatarId(ID1)
                .filename(CONTENT1)
                .remotePath(CONTENT2)
                .build();

        UserDto.ResponseRanking response1 = UserDto.ResponseRanking.builder()
                .userId(ID1)
                .answerCount(1L)
                .infoMessage(CONTENT1)
                .jobType(JobType.DEVELOPER)
                .grade(Grade.VIP)
                .nickname(NICKNAME2)
                .likeCount(1L)
                .point(ONE)
                .articleCount(1L)
                .rank(TWO)
                .avatar(avatar)
                .build();

        UserDto.ResponseRanking response2 = UserDto.ResponseRanking.builder()
                .userId(ID1)
                .answerCount(1L)
                .infoMessage(CONTENT1)
                .jobType(JobType.DEVELOPER)
                .grade(Grade.VIP)
                .nickname(NICKNAME2)
                .likeCount(1L)
                .point(ONE)
                .articleCount(1L)
                .rank(TWO)
                .avatar(avatar)
                .build();

        PageRequest pageable = PageRequest.of(1, 2, Sort.by("point"));
        Page<UserDto.ResponseRanking> result = new PageImpl<>(List.of(response1, response2), pageable, 2L);

        ResponseMultiplePaging<UserDto.ResponseRanking> response = new ResponseMultiplePaging<>(List.of(response1, response2), result);

        BDDMockito.given(userService.getUserRankList(any(PageRequest.class))).willReturn(response);

        //when
        ResultActions perform = mockMvc.perform(get("/users/ranks")
                .param("size", "2")
                .param("page", "1")
                .param("q","point"));

        //then
        perform.andExpect(status().isOk())
                .andDo(document(
                                "유저_랭킹조회_성공_200",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestParameters(
                                        parameterWithName("q").description("정렬 조건 - articles(게시글 작성순), answers(답변 작성순), point(포인트순)"),
                                        parameterWithName("page").description("현재 페이지"),
                                        parameterWithName("size").description("페이지당 개수")
                                ),
                                responseFields(
                                        fieldWithPath("data[].rank").type(NUMBER).description("임시로 넣어놨으며 아직 별도 구현 예정 없습니다, 필요 시 협의 필요"),
                                        fieldWithPath("data[].userId").type(NUMBER).description("유저 db 시퀀스값"),
                                        fieldWithPath("data[].answerCount").type(NUMBER).description("답변 작성 횟수"),
                                        fieldWithPath("data[].infoMessage").type(STRING).description("자기소개"),
                                        fieldWithPath("data[].jobType").type(STRING).description("직업"),
                                        fieldWithPath("data[].grade").type(STRING).description("등급"),
                                        fieldWithPath("data[].nickname").type(STRING).description("이메일"),
                                        fieldWithPath("data[].likeCount").type(NUMBER).description("받은 좋아요 수"),
                                        fieldWithPath("data[].point").type(NUMBER).description("보유 포인트"),
                                        fieldWithPath("data[].articleCount").type(NUMBER).description("게시글(정보+질문)글 작성 횟수"),
                                        fieldWithPath("data[].avatar").type(OBJECT).description("유저 프로필 이미지 객체"),
                                        fieldWithPath("data[].avatar.avatarId").type(NUMBER).description("프로필 이미지 db 시퀀스"),
                                        fieldWithPath("data[].avatar.filename").type(STRING).description("프로필 이미지 파일명"),
                                        fieldWithPath("data[].avatar.remotePath").type(STRING).description("프로필 이미지 서버 url"),
                                        fieldWithPath("pageInfo.totalElements").type(NUMBER).description("db 데이터 총 개수"),
                                        fieldWithPath("pageInfo.totalPages").type(NUMBER).description("총 페이지 개수"),
                                        fieldWithPath("pageInfo.page").type(NUMBER).description("현재 페이지"),
                                        fieldWithPath("pageInfo.size").type(NUMBER).description("페이지 사이즈"),
                                        fieldWithPath("pageInfo.sort.empty").type(BOOLEAN).description("해당 페이지가 비어있는지"),
                                        fieldWithPath("pageInfo.sort.sorted").type(BOOLEAN).description("페이지가 정렬되어 있다면 true"),
                                        fieldWithPath("pageInfo.sort.unsorted").type(BOOLEAN).description("페이지가 정렬되지 않았다면 true")
                                )
                        )
                );
    }
}
