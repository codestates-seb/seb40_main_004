package com.morakmorak.morak_back_end.Integration.answer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.repository.answer.AnswerRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ROLE_USER_LIST;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
public class DeleteAnswerTest {
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private AnswerRepository answerRepository;
    @PersistenceContext
    EntityManager em;
    User USER_1;
    User USER_2;
    Article ARTICLE_USER1;
    Answer USER1_ANSWER;
    Answer USER2_ANSWER;
    Comment COMMENT;
    AnswerLike LIKE;
    Report REPORT;

    @BeforeEach
    void setup() {
        USER_1 = User.builder().email(EMAIL1).nickname(NICKNAME1).build();
        USER_2 = User.builder().email(EMAIL2).nickname(NICKNAME2).build();
        em.persist(USER_1);
        em.persist(USER_2);


        ARTICLE_USER1 = Article.builder().user(USER_1).category(Category.builder().name(CategoryName.QNA).build()).build();
        USER1_ANSWER = Answer.builder().content("?????????????????? ????????? ?????? ?????????").user(USER_1).article(ARTICLE_USER1).build();
        USER2_ANSWER = Answer.builder().content("?????? ???????????????. ?????????????????? ?????????").user(USER_2).article(ARTICLE_USER1).build();

        COMMENT = Comment.builder().answer(USER2_ANSWER).build();
        LIKE = AnswerLike.builder().user(USER_1).answer(USER2_ANSWER).build();
        USER2_ANSWER.getComments().add(COMMENT);
        USER2_ANSWER.getAnswerLike().add(LIKE);

        REPORT = Report.builder().answer(USER2_ANSWER).build();
        em.persist(ARTICLE_USER1);
        em.persist(USER1_ANSWER);
        em.persist(USER2_ANSWER);
    }

    @Test
    @DisplayName("???????????? ?????? ?????? ?????? ??? ?????? ????????? ????????? ???????????? 200??? ????????????.")
    void deleteAnswer_success() throws Exception {
        //given ?????? 2??? ????????? ?????? ?????? ????????? ?????????.
        String USER2_TOKEN = jwtTokenUtil.createAccessToken(USER_2.getEmail(), USER_2.getId(), ROLE_USER_LIST, USER_2.getNickname());
        Long USER1_ANSWER_ID = USER1_ANSWER.getId();
        //when ?????? ?????? ?????????
        ResultActions result = mockMvc.perform(
                delete("/articles/{article-id}/answers/{answer-id}", ARTICLE_USER1.getId(), USER2_ANSWER.getId())
                        .header(JWT_HEADER, USER2_TOKEN)
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].answerId").value(USER1_ANSWER_ID))
                .andExpect(jsonPath("$.data[0].content").exists())
                .andExpect(jsonPath("$.data[0].answerLikeCount").exists())
                .andExpect(jsonPath("$.data[0].isPicked").value(false))
                .andExpect(jsonPath("$.data[0].commentCount").value(0))
                .andExpect(jsonPath("$.data[0].createdAt").exists())
                .andExpect(jsonPath("$.data[0].userInfo.userId").value(USER_1.getId().intValue()))
                .andExpect(jsonPath("$.data[0].userInfo.nickname").value(USER_1.getNickname()))
                .andExpect(jsonPath("$.data[0].userInfo.grade").exists())
                .andExpect(jsonPath("$.pageInfo.page").value(1))
                .andExpect(jsonPath("$.pageInfo.size").value(5))
                .andExpect(jsonPath("$.pageInfo.totalElements").value(1))
                .andExpect(jsonPath("$.pageInfo.totalPages").value(1))
                .andExpect(jsonPath("$.pageInfo.sort.sorted").value(false));
    }

    @Test
    @DisplayName("???????????? ?????? ?????? ?????? ?????? ??? 404 ????????????.")
    void deleteAnswer_failed() throws Exception {
        //given ?????? 2??? ????????? ?????? ?????? ????????? ?????????.
        String USER2_TOKEN = jwtTokenUtil.createAccessToken(USER_2.getEmail(), USER_2.getId(), ROLE_USER_LIST, USER_2.getNickname());
        Long USER1_ANSWER_ID = USER1_ANSWER.getId();
        answerRepository.deleteById(USER2_ANSWER.getId());
        //when ?????? ?????? ?????????
        ResultActions result = mockMvc.perform(
                delete("/articles/{article-id}/answers/{answer-id}", ARTICLE_USER1.getId(), USER2_ANSWER.getId())
                        .header(JWT_HEADER, USER2_TOKEN)
        );

        result.andExpect(status().isNotFound());
    }
}
