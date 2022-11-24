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
    @DisplayName("뱃지가 아무것도 선택되지 않았다면 400 반환")
    void postReview_failed_1() throws Exception {
        //user가 article 작성, user1이 answer 작성, user의 채택 요청, badge 선택안함
        List<BadgeDto.SimpleBadge> invalidBadges = new ArrayList<>();
        ReviewDto.RequestPostReview request = ReviewDto.RequestPostReview.builder().content("15글자 이상의 정성스러운 답변").badges(invalidBadges).build();
        String json = objectMapper.writeValueAsString(request);
        ResultActions perform = mockMvc.perform(post("/articles/{article-id}/answers/{answer-id}/reviews", article.getId(), answer.getId())
                .content(json)
                .header(JWT_HEADER, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
        );
        perform.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("보유한 포인트보다 많은 포인트를 보내려고 시도했다면 400 반환")
    void checkRemainingPoints_failed_2() throws Exception {
        //user가 article 작성, user1이 answer 작성, user의 채택 요청, 포인트가 0인데 500 보내려고 시도

        ReviewDto.RequestPostReview request = ReviewDto.RequestPostReview.builder().content("15글자 이상의 정성스러운 답변").badges(validBadges).point(Optional.of(500)).build();
        String json = objectMapper.writeValueAsString(request);
        ResultActions perform = mockMvc.perform(post("/articles/{article-id}/answers/{answer-id}/reviews", article.getId(), answer.getId())
                .content(json)
                .header(JWT_HEADER, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
        );
        perform.andExpect(status().isBadRequest());
        Assertions.assertThatThrownBy(() -> reviewService.checkRemainingPoints(user, request.getPoint().get())).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("글쓴이와 답변작성자가 같은 경우 409 반환")
    void checkReceiverIsSameUser_failed_3() throws Exception {
        //user가 answerByUser 자문자답 후 채택 요청
        Answer answerByUser = Answer.builder().user(user).article(article).build();
        user.getAnswers().add(answerByUser);
        article.getAnswers().add(answerByUser);
        em.persist(answerByUser);
        ReviewDto.RequestPostReview request = ReviewDto.RequestPostReview.builder().content("15글자 이상의 정성스러운 답변").badges(validBadges).build();
        String json = objectMapper.writeValueAsString(request);

        ResultActions perform = mockMvc.perform(post("/articles/{article-id}/answers/{answer-id}/reviews", article.getId(), answerByUser.getId())
                .content(json)
                .header(JWT_HEADER, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
        );
        perform.andExpect(status().isConflict());
    }

    @Test
    @DisplayName("글쓴이가 아닌 유저(권한 없는 유저)가 채택 요청 보내는 경우 401 반환")
    void checkReviewSenderPermission_failed_4() throws Exception {
        //user1은 글쓴이가 아닌데, user1의 답변 채택 요청 전송(그러면 본인 채택 로직이 먼저 캐치함)
        ReviewDto.RequestPostReview request = ReviewDto.RequestPostReview.builder().content("15글자 이상의 정성스러운 답변").badges(validBadges).build();
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
    @DisplayName("정상적인 요청 시 created 반환되며, 포인트의 경우 sender는 차감, receiver는 증가된다.")
    void donatePoint_success_1() throws Exception {
        //user에게 50포인트 존재
        user.addPoint(Answer.builder().build(), pointCalculator);
        Integer user_InitialPoint = user.getPoint();
        Integer user1_InitialPoint = user1.getPoint();
        ReviewDto.RequestPostReview request = ReviewDto.RequestPostReview.builder().content("15글자 이상의 정성스러운 답변").badges(validBadges).point(Optional.of(10)).build();
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
