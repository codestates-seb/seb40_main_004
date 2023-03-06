package com.morakmorak.morak_back_end.Integration.notification;

import com.morakmorak.morak_back_end.controller.NotificationController;
import com.morakmorak.morak_back_end.dto.NotificationDto;
import com.morakmorak.morak_back_end.dto.ResponseMultiplePaging;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.DomainType;
import com.morakmorak.morak_back_end.repository.notification.NotificationRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.AnswerService;
import com.morakmorak.morak_back_end.service.ArticleService;
import com.morakmorak.morak_back_end.service.NotificationService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
public class NotificationTest {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    NotificationService notificationService;

    @Autowired
    EntityManager em;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    NotificationController notificationController;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    ArticleService articleService;

    @Autowired
    AnswerService answerService;

    private final String BASE_URL = "localhost:8080.com";

    User user;

    String accessToken;

    List<Notification> notifications = new ArrayList<>();

    @BeforeEach
    void init() {
        user = User.builder().build();

        for (int i=0; i<30; i++) {
            Notification notification = Notification.builder()
                    .message(String.valueOf(i))
                    .isChecked(i % 2 == 0 ? Boolean.TRUE : Boolean.FALSE)
                    .domainType(DomainType.ARTICLE)
                    .uri("/posts/" + i)
                    .user(user)
                    .build();

            notification.mapUserAndNotification();
            em.persist(notification);
            em.persist(user);
            notifications.add(notification);
        }

        accessToken = jwtTokenUtil.createAccessToken(EMAIL1, user.getId(), ROLE_USER_LIST,NICKNAME2);
    }

    @Test
    @DisplayName("페이지 및 스테이터스 정상 반환 테스 ")
    void getNotifications() throws Exception {
        // given
        // when
        ResultActions perform = mockMvc.perform(get("/notifications?page=1&size=10")
                .header(JWT_HEADER, accessToken)
                .header("User-Agent", "Mozilla 5.0"));

        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.pageInfo.size", is(10)))
                .andExpect(jsonPath("$.pageInfo.page", is(1)))
                .andExpect(jsonPath("$.pageInfo.totalElements", is(30)))
                .andExpect(jsonPath("$.pageInfo.totalPages", is(3)));
    }

    @Test
    @DisplayName("내림차순 반환 테스트")
    void getNotifications2() throws Exception {
        //given
        UserDto.UserInfo token = UserDto.UserInfo.builder()
                .id(user.getId())
                .email(EMAIL1)
                .roles(ROLE_USER_LIST)
                .build();

        //when
        ResponseMultiplePaging<NotificationDto.SimpleResponse> notifications = notificationController.getNotifications(1, 10,token);

        //then
        assertThat(notifications.getData().size()).isEqualTo(10);
        assertThat(notifications.getData().get(0).getMessage()).isEqualTo("29");
        assertThat(notifications.getData().get(9).getMessage()).isEqualTo("20");
    }

    @Test
    @DisplayName("리다이렉트 정상 반환 테스트")
    void getNotification() throws Exception {
        //given
        Long id = notifications.get(0).getId();
        String uri = notifications.get(0).getUri();
        //when
        ResultActions perform = mockMvc.perform(get("/notifications/{id}", id)
                .header("User-Agent", "Mozilla 5.0"));

        //then
        perform.andExpect(status().isPermanentRedirect())
                .andExpect(redirectedUrl(BASE_URL + uri));
    }

    @Test
    @DisplayName("삭제 시 유저가 다를 경우 403 반환 테스트")
    void deleteNotification_failed() throws Exception {
        //given
        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, ID2, ROLE_USER_LIST, NICKNAME2);
        Long id = notifications.get(0).getId();
        //when
        ResultActions perform = mockMvc.perform(delete("/notifications/{id}", id)
                .header(JWT_HEADER, accessToken));

        //then
        perform.andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("로직 정상 수행 후 notification 삭제 여부 확인")
    void deleteNotification() throws Exception {
        //given
        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, user.getId(), ROLE_USER_LIST,NICKNAME1);
        Long id = notifications.get(0).getId();
        //when
        ResultActions perform = mockMvc.perform(delete("/notifications/{id}", id)
                .header(JWT_HEADER, accessToken));

        Optional<Notification> result = notificationRepository.findById(id);

        //then
        perform.andExpect(status().isNoContent());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("작성한 글에 좋아요가 10개 달리면 알림이 생성된다")
    void article_notification() throws Exception {
        //given
        User author = User.builder().nickname(NICKNAME1).build();
        Article article = Article.builder().user(author).title(CONTENT1).build();

        em.persist(author);
        em.persist(article);

        //when
        for (int i=0; i<10; i++) {
            User other = User.builder().nickname(NICKNAME2 + i).build();
            em.persist(other);
            articleService.pressLikeButton(article.getId(), UserDto.UserInfo.builder().id(other.getId()).build());
        }

        //then
        StringBuilder stringBuilder = new StringBuilder();

        String result = stringBuilder.append("회원님께서 작성하신 ")
                .append("\"")
                .append(article.getTitle())
                .append("\"")
                .append("의 좋아요가 ")
                .append(10)
                .append("개를 돌파했어요.")
                .toString();

        assertThat(author.getNotifications().size()).isEqualTo(ONE);
        assertThat(author.getNotifications().get(0).getMessage()).isEqualTo(result);
    }

    @Test
    @DisplayName("작성한 글에 좋아요가 30개 달리면 알림이 생성된다")
    void article_notification2() throws Exception {
        //given
        User author = User.builder().nickname(NICKNAME1).build();
        Article article = Article.builder().user(author).title(CONTENT1).build();

        em.persist(author);
        em.persist(article);

        //when
        for (int i=0; i<30; i++) {
            User other = User.builder().nickname(NICKNAME2 + i).build();
            em.persist(other);
            articleService.pressLikeButton(article.getId(), UserDto.UserInfo.builder().id(other.getId()).build());
        }

        //then
        StringBuilder stringBuilder = new StringBuilder();

        String result = stringBuilder.append("회원님께서 작성하신 ")
                .append("\"")
                .append(article.getTitle())
                .append("\"")
                .append("의 좋아요가 ")
                .append(30)
                .append("개를 돌파했어요.")
                .toString();

        assertThat(author.getNotifications().size()).isEqualTo(THREE);
        assertThat(author.getNotifications().get(2).getMessage()).isEqualTo(result);
    }

    @Test
    @DisplayName("작성한 답글에 좋아요가 10개 달리면 알림이 생성된다")
    void article_notification3() throws Exception {
        //given
        User author = User.builder().nickname(NICKNAME1).build();
        Article article = Article.builder().user(author).title(CONTENT1).build();
        Answer answer = Answer.builder().user(author).article(article).content(CONTENT2).build();

        em.persist(author);
        em.persist(article);
        em.persist(answer);

        //when
        for (int i=0; i<30; i++) {
            User other = User.builder().nickname(NICKNAME2 + i).build();
            em.persist(other);
            answerService.pressLikeButton(answer.getId(), UserDto.UserInfo.builder().id(other.getId()).build());
        }

        //then
        StringBuilder stringBuilder = new StringBuilder();

        String result = stringBuilder.append("회원님께서 작성하신 ")
                .append("\"")
                .append(answer.getArticle().getTitle())
                .append("\"")
                .append("의 답변에 대한 좋아요가 ")
                .append(10)
                .append("개를 돌파했어요.")
                .toString();

        assertThat(author.getNotifications().size()).isEqualTo(THREE);
        assertThat(author.getNotifications().get(0).getMessage()).isEqualTo(result);
    }

    @Test
    @DisplayName("작성한 답글에 좋아요가 100개 달리면 알림이 생성된다")
    void article_notification4() throws Exception {
        //given
        User author = User.builder().nickname(NICKNAME1).build();
        Article article = Article.builder().user(author).title(CONTENT1).build();
        Answer answer = Answer.builder().user(author).article(article).content(CONTENT2).build();

        em.persist(author);
        em.persist(article);
        em.persist(answer);

        //when
        for (int i=0; i<100; i++) {
            User other = User.builder().nickname(NICKNAME2 + i).build();
            em.persist(other);
            answerService.pressLikeButton(answer.getId(), UserDto.UserInfo.builder().id(other.getId()).build());
        }

        //then
        StringBuilder stringBuilder = new StringBuilder();

        String result = stringBuilder.append("회원님께서 작성하신 ")
                .append("\"")
                .append(answer.getArticle().getTitle())
                .append("\"")
                .append("의 답변에 대한 좋아요가 ")
                .append(100)
                .append("개를 돌파했어요.")
                .toString();

        assertThat(author.getNotifications().size()).isEqualTo(10);
        assertThat(author.getNotifications().get(9).getMessage()).isEqualTo(result);
    }
}
