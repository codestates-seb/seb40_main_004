package com.morakmorak.morak_back_end.Integration.answer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.repository.AnswerRepository;
import com.morakmorak.morak_back_end.repository.RedisRepository;
import com.morakmorak.morak_back_end.repository.UserRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.AnswerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ROLE_USER_LIST;
import static com.morakmorak.morak_back_end.util.TestConstants.EMAIL1;
import static com.morakmorak.morak_back_end.util.TestConstants.NICKNAME1;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
public class AnswerPressLikeButton {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RedisRepository<String> mailAuthRedisRepository;

    @Autowired
    AnswerService answerService;

    @Autowired
    EntityManager em;

    @Autowired
    AnswerRepository answerRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("로그인한 회원이 게시글의 좋아요를 처음 눌렀을 경우 좋아요가 눌리고 201코드와 dto가 리턴된다.")
    public void pressLikeButton_suc1() throws Exception {
        //give
        Answer answer = Answer.builder().content("내용입니다. 잘부탁드립니다. 제발 문제가 안생기길 바랍니다.")
                .build();
        em.persist(answer);

        User user = User.builder().email(EMAIL1).nickname(NICKNAME1).build();
        em.persist(user);

        Answer dbAnswer = answerRepository.findAnswerByContent(answer.getContent()).orElseThrow(() -> new RuntimeException("게시글없음"));
        User dbUser = userRepository.findUserByEmail(EMAIL1).orElseThrow(() -> new RuntimeException("유저없음"));

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, dbUser.getId(), ROLE_USER_LIST);

        //when
        ResultActions perform = mockMvc.perform(
                post("/articles/1/answers/"+dbAnswer.getId()+"/likes")
                        .header(JWT_HEADER, accessToken)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.answerId").value(answer.getId()))
                .andExpect(jsonPath("$.userId").value(dbUser.getId()))
                .andExpect(jsonPath("$.isLiked").value(true))
                .andExpect(jsonPath("$.likeCount").value(1));
    }
    @Test
    @DisplayName("로그인한 회원이 답변글의 좋아요를 두번째 눌렀을 경우 좋아요가 취소되고 201코드와 dto가 리턴된다.")
    public void pressLikeButton_suc2() throws Exception {
        //give
        Answer answer = Answer.builder().content("내용입니다. 잘부탁드립니다. 제발 문제가 안생기길 바랍니다.")
                .build();
        em.persist(answer);

        User user = User.builder().email(EMAIL1).nickname(NICKNAME1).build();
        em.persist(user);

        AnswerLike answerLike = AnswerLike.builder().user(user).answer(answer).build();
        em.persist(answerLike);

        Answer dbAnswer = answerRepository.findAnswerByContent(answer.getContent()).orElseThrow(() -> new RuntimeException("답변없음"));
        User dbUser = userRepository.findUserByEmail(EMAIL1).orElseThrow(() -> new RuntimeException("유저없음"));

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, dbUser.getId(), ROLE_USER_LIST);

        //when
        ResultActions perform = mockMvc.perform(
                post("/articles/1/answers/"+dbAnswer.getId()+"/likes")
                        .header(JWT_HEADER, accessToken)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.answerId").value(answer.getId()))
                .andExpect(jsonPath("$.userId").value(dbUser.getId()))
                .andExpect(jsonPath("$.isLiked").value(false))
                .andExpect(jsonPath("$.likeCount").value(0));
    }

    @Test
    @DisplayName("로그인한 회원이 존재하지 않는 게시글의 좋아요를 눌렀을경우 404코드와 Article_Not_Found 가 리턴된다.")
    public void pressLikeButton_fail1() throws Exception {
        //give
        User user = User.builder().email(EMAIL1).nickname(NICKNAME1).build();
        em.persist(user);

        User dbUser = userRepository.findUserByEmail(EMAIL1).orElseThrow(() -> new RuntimeException("유저없음"));

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, dbUser.getId(), ROLE_USER_LIST);

        //when
        ResultActions perform = mockMvc.perform(
                post("/articles/1/answers/"+500+"/likes")
                        .header(JWT_HEADER, accessToken)
        );
        //then
        perform.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("로그인 하지 않은 회원이 게시글의 좋아요를 눌렀을경우 404코드와 User_Not_Found 가 리턴된다.")
    public void pressLikeButton_fail2() throws Exception {
        //give
        Answer answer = Answer.builder().content("내용입니다. 잘부탁드립니다. 제발 문제가 안생기길 바랍니다.")
                .build();
        em.persist(answer);

        Answer dbAnswer = answerRepository.findAnswerByContent(answer.getContent()).orElseThrow();
        //when
        ResultActions perform = mockMvc.perform(
                post("/articles/1/answers/"+dbAnswer.getId()+"/likes")
        );

        //then
        perform.andExpect(status().isNotFound());
    }
}
