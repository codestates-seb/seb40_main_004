package com.morakmorak.morak_back_end.Integration.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.controller.ReviewController;
import com.morakmorak.morak_back_end.domain.PointCalculator;
import com.morakmorak.morak_back_end.dto.BadgeDto;
import com.morakmorak.morak_back_end.dto.ReviewDto;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.ArticleStatus;
import com.morakmorak.morak_back_end.entity.enums.BadgeName;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.entity.enums.TagName;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.AnswerService;
import com.morakmorak.morak_back_end.service.ArticleService;
import com.morakmorak.morak_back_end.service.NotificationService;
import com.morakmorak.morak_back_end.service.ReviewService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ROLE_USER_LIST;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
public class PostReviewTest {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    NotificationService notificationService;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    EntityManager em;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ReviewController reviewController;

    @Autowired
    ReviewService reviewService;
    @Autowired
    ArticleService articleService;

    @Autowired
    AnswerService answerService;
    @Autowired
    PointCalculator pointCalculator;

    @Autowired
    UserRepository userRepository;

    String accessToken;
    User user;
    User user1;
    Article article;
    Answer answer;
    Badge helpful;
    Badge smart;
    Badge wise;
    List<BadgeDto.SimpleBadge> validBadges;

    @BeforeEach
    void init() {
        user = User.builder().email(EMAIL1).nickname(NICKNAME1).build();
        user1 = User.builder().email(EMAIL2).nickname(NICKNAME2).build();
        em.persist(user);
        em.persist(user1);
        article = Article.builder().user(user).category(Category.builder().name(CategoryName.QNA).build()).isClosed(false).articleStatus(ArticleStatus.POSTING).build();
        em.persist(article);
        accessToken = jwtTokenUtil.createAccessToken(EMAIL1, user.getId(), ROLE_USER_LIST, user.getNickname());
        answer = Answer.builder().user(user1).article(article).build();
        article.getAnswers().add(answer);
        user1.getAnswers().add(answer);
        em.persist(answer);
        Tag.builder().name(TagName.JAVA).build();
        helpful = Badge.builder().name(BadgeName.HELPFUL).build();
        smart = Badge.builder().name(BadgeName.SMART).build();
        wise = Badge.builder().name(BadgeName.WISE).build();
        em.persist(helpful);
        em.persist(smart);
        em.persist(wise);
        validBadges = List.of(
                BadgeDto.SimpleBadge.builder().name(helpful.getName()).badgeId(helpful.getId()).build(),
                BadgeDto.SimpleBadge.builder().name(smart.getName()).badgeId(smart.getId()).build(),
                BadgeDto.SimpleBadge.builder().name(wise.getName()).badgeId(wise.getId()).build()
        );
    }

    @Test
    @DisplayName("????????? ???????????? ???????????? ???????????? 400 ??????")
    void postReview_failed_1() throws Exception {
        //user??? article ??????, user1??? answer ??????, user??? ?????? ??????, badge ????????????
        List<BadgeDto.SimpleBadge> invalidBadges = new ArrayList<>();
        ReviewDto.RequestPostReview request = ReviewDto.RequestPostReview.builder().content("15?????? ????????? ??????????????? ??????").badges(invalidBadges).build();
        String json = objectMapper.writeValueAsString(request);
        ResultActions perform = mockMvc.perform(post("/articles/{article-id}/answers/{answer-id}/reviews", article.getId(), answer.getId())
                .content(json)
                .header(JWT_HEADER, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
        );
        perform.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("????????? ??????????????? ?????? ???????????? ???????????? ??????????????? 400 ??????")
    void checkRemainingPoints_failed_2() throws Exception {
        //user??? article ??????, user1??? answer ??????, user??? ?????? ??????, ???????????? 0?????? 500 ???????????? ??????

        ReviewDto.RequestPostReview request = ReviewDto.RequestPostReview.builder().content("15?????? ????????? ??????????????? ??????").badges(validBadges).point(Optional.of(500)).build();
        String json = objectMapper.writeValueAsString(request);
        ResultActions perform = mockMvc.perform(post("/articles/{article-id}/answers/{answer-id}/reviews", article.getId(), answer.getId())
                .content(json)
                .header(JWT_HEADER, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
        );
        perform.andExpect(status().isUnprocessableEntity());
        Assertions.assertThatThrownBy(() -> reviewService.checkRemainingPoints(user, request.getPoint().get())).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("???????????? ?????????????????? ?????? ?????? 409 ??????")
    void checkReceiverIsSameUser_failed_3() throws Exception {
        //user??? answerByUser ???????????? ??? ?????? ??????
        Answer answerByUser = Answer.builder().user(user).article(article).build();
        user.getAnswers().add(answerByUser);
        article.getAnswers().add(answerByUser);
        em.persist(answerByUser);
        ReviewDto.RequestPostReview request = ReviewDto.RequestPostReview.builder().content("15?????? ????????? ??????????????? ??????").badges(validBadges).build();
        String json = objectMapper.writeValueAsString(request);

        ResultActions perform = mockMvc.perform(post("/articles/{article-id}/answers/{answer-id}/reviews", article.getId(), answerByUser.getId())
                .content(json)
                .header(JWT_HEADER, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
        );
        perform.andExpect(status().isConflict());
    }

    @Test
    @DisplayName("???????????? ?????? ??????(?????? ?????? ??????)??? ?????? ?????? ????????? ?????? 401 ??????")
    void checkReviewSenderPermission_failed_4() throws Exception {
        //user1??? ???????????? ?????????, user1??? ?????? ?????? ?????? ??????(????????? ?????? ?????? ????????? ?????? ?????????)
        ReviewDto.RequestPostReview request = ReviewDto.RequestPostReview.builder().content("15?????? ????????? ??????????????? ??????").badges(validBadges).build();
        String json = objectMapper.writeValueAsString(request);
        String accessTokenForUser1 = jwtTokenUtil.createAccessToken(EMAIL2, user1.getId(), ROLE_USER_LIST, user.getNickname());
        ResultActions perform = mockMvc.perform(post("/articles/{article-id}/answers/{answer-id}/reviews", article.getId(), answer.getId())
                .content(json)
                .header(JWT_HEADER, accessTokenForUser1)
                .contentType(MediaType.APPLICATION_JSON)
        );
        perform.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("???????????? ?????? ??? created ????????????, ???????????? ?????? sender??? ??????, receiver??? ????????????.")
    void donatePoint_success_1() throws Exception {
        //user?????? 50????????? ??????
        user.plusPoint(Answer.builder().build(), pointCalculator);
        Integer user_InitialPoint = user.getPoint();
        Integer user1_InitialPoint = user1.getPoint();
        ReviewDto.RequestPostReview request = ReviewDto.RequestPostReview.builder().content("15?????? ????????? ??????????????? ??????").badges(validBadges).point(Optional.of(10)).build();
        String json = objectMapper.writeValueAsString(request);
        ResultActions perform = mockMvc.perform(post("/articles/{article-id}/answers/{answer-id}/reviews", article.getId(), answer.getId())
                .content(json)
                .header(JWT_HEADER, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
        );
        perform.andExpect(status().isCreated());
        Assertions.assertThat(user_InitialPoint > user.getPoint()).isTrue();
        Assertions.assertThat(user1_InitialPoint < user1.getPoint()).isTrue();
    }
}
