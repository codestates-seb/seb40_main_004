package com.morakmorak.morak_back_end.Integration.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.controller.UserController;
import com.morakmorak.morak_back_end.domain.PointCalculator;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.repository.answer.AnswerRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static com.morakmorak.morak_back_end.util.TestConstants.NICKNAME1;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
public class GetUserAnswerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    EntityManager em;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserController userController;
    @Autowired
    UserService userService;

    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    PointCalculator pointCalculator;

    @Autowired
    UserRepository userRepository;
    Avatar avatar;
    String accessToken;
    User user;
    Article article;
    Answer answerPicked;
    Answer answerNotPicked;

    @BeforeEach
    void setup() {
        avatar = Avatar.builder().remotePath("remotePath").originalFilename("fileName").build();
        em.persist(avatar);

        user = User.builder().nickname(NICKNAME1).grade(Grade.BRONZE).avatar(avatar).build();
        article = Article.builder().title("테스트 타이틀입니다.").content("콘탠트입니다. 질문을 많이 올려주세요.").user(user).build();
        em.persist(article);

        List<AnswerLike> likeList = new ArrayList<>();
        answerPicked = Answer.builder().content("15글자 이상의 유효한 답변내용입니다.").user(user).isPicked(false).answerLike(likeList).article(article).build();
        AnswerLike like = AnswerLike.builder().answer(answerPicked).user(user).build();
        likeList.add(like);
        user.getAnswers().add(answerPicked);
        answerPicked.getAnswerLike().add(like);

        answerNotPicked = Answer.builder().content("채택된 답변입니다.").user(user).isPicked(true).article(article).build();
        user.getAnswers().add(answerNotPicked);

        em.persist(answerPicked);
        em.persist(answerNotPicked);
    }

    @Test
    @DisplayName("특정 유저 대시보드에서 유저가 답변한 페이지 요청이 유효할 시 200과 답변리스트를 반환한다.")
    void getUserAnswerList_success_1() throws Exception {
        //유저가 있고, 유저가 답변한 목록이 있을 때 요청이 유효한 경우 (파라미터 잘 들어온 경우)응답 dto가 잘 만들어진다
        ResultActions perform = mockMvc.perform(
                get("/users/{user-id}/answers", user.getId())
                        .param("page", "1")
                        .param("size", "50")
                        .header("User-Agent", "Mozilla 5.0")
        );

        //검증
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].answerId").value(answerNotPicked.getId().intValue()))
                .andExpect(jsonPath("$.data[0].content").exists())
                .andExpect(jsonPath("$.data[0].isPicked").value(true))
                .andExpect(jsonPath("$.data[0].answerLikeCount").exists())
                .andExpect(jsonPath("$.data[0].commentCount").value(0))
                .andExpect(jsonPath("$.data[0].createdAt").exists())
                .andExpect(jsonPath("$.data[0].userInfo.userId").value(user.getId().intValue()))
                .andExpect(jsonPath("$.data[0].userInfo.nickname").exists())
                .andExpect(jsonPath("$.data[0].userInfo.grade").exists())
                .andExpect(jsonPath("$.data[1].answerId").value(answerPicked.getId().intValue()))
                .andExpect(jsonPath("$.data[1].content").exists())
                .andExpect(jsonPath("$.data[1].isPicked").value(false))
                .andExpect(jsonPath("$.data[1].answerLikeCount").exists())
                .andExpect(jsonPath("$.data[1].commentCount").value(0))
                .andExpect(jsonPath("$.data[1].createdAt").exists())
                .andExpect(jsonPath("$.data[1].userInfo.userId").value(user.getId().intValue()))
                .andExpect(jsonPath("$.data[1].userInfo.nickname").exists())
                .andExpect(jsonPath("$.data[1].userInfo.grade").exists())
                .andExpect(jsonPath("$.pageInfo.page").value(1))
                .andExpect(jsonPath("$.pageInfo.size").value(50))
                .andExpect(jsonPath("$.pageInfo.totalElements").value(2))
                .andExpect(jsonPath("$.pageInfo.totalPages").value(1))
                .andExpect(jsonPath("$.pageInfo.sort.sorted").value(false));
    }
    @Test
    @DisplayName("특정 유저 대시보드에서 유저 아이디가 잘못된 경우.")
    void getUserAnswerList_failed_1() throws Exception {
        //유저가 있고, 유저가 답변한 목록이 있을 때 요청이 유효한 경우 (파라미터 잘 들어온 경우)응답 dto가 잘 만들어진다
        ResultActions perform = mockMvc.perform(
                get("/users/-1/answers")
                        .param("page", "1")
                        .param("size", "50")
                        .header("User-Agent", "Mozilla 5.0")
        );

        //검증
        perform.andExpect(status().isNotFound());
    }
}
