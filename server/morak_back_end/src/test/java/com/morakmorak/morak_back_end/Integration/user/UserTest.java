package com.morakmorak.morak_back_end.Integration.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.controller.UserController;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

// TODO : 테스트 시 매번 calendar 365개를 저장하는 방식이 너무 비효율적이다. 해당 부분에 대한 로직 자체를 검토하고 다시 활성화 예정.
@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
public class UserTest {
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserController userController;
    @Autowired
    EntityManager entityManager;
    @Autowired
    UserRepository userRepository;


//
//    @Test
//    @DisplayName("시퀀스 값(User ID)을 찾을 수 없는 경우 404 반환")
//    public void getDashboard_failed() throws Exception {
//        //given
//        //when then
//        mockMvc.perform(get("/users/-1/dashboard"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @DisplayName("대시보드 정상 반환 테스트")
//    public void getDashboard_success() throws Exception {
//        //given
//        Avatar avatar1 = Avatar.builder()
//                .fileSize(THREE)
//                .localPath(CONTENT1)
//                .originalFilename(CONTENT2)
//                .remotePath(CONTENT1)
//                .build();
//
//        Avatar avatar2 = Avatar.builder()
//                .fileSize(THREE)
//                .localPath(CONTENT1)
//                .originalFilename(CONTENT2)
//                .remotePath(CONTENT1)
//                .build();
//
//        User user = User.builder()
//                .nickname(NICKNAME1)
//                .password(PASSWORD1)
//                .email(EMAIL1)
//                .blog(TISTORY_URL)
//                .point(ONE)
//                .github(GITHUB_URL)
//                .grade(Grade.VIP)
//                .jobType(JobType.DEVELOPER)
//                .avatar(avatar1)
//                .build();
//
//        User other = User.builder()
//                .grade(Grade.VIP)
//                .nickname(NICKNAME2)
//                .blog(TISTORY_URL)
//                .github(GITHUB_URL)
//                .email(EMAIL1)
//                .password(PASSWORD1)
//                .jobType(JobType.DEVELOPER)
//                .avatar(avatar2)
//                .build();
//
//        em.persist(user);
//        em.persist(other);
//
//        Tag tag1 = new Tag(TagName.JAVA);
//        Tag tag2 = new Tag(TagName.C);
//        Tag tag3 = new Tag(TagName.NODE);
//        Tag tag4 = new Tag(TagName.SPRING);
//
//        Badge badge1 = new Badge(BadgeName.KINDLY);
//        Badge badge2 = new Badge(BadgeName.POO);
//        Badge badge3 = new Badge(BadgeName.WISE);
//        Badge badge4 = new Badge(BadgeName.SMART);
//
//        Answer answer1 = Answer.builder()
//                .user(user)
//                .content(CONTENT2)
//                .isPicked(Boolean.FALSE)
//                .build();
//
//        Answer answer2 = Answer.builder()
//                .user(user)
//                .content(CONTENT2)
//                .isPicked(Boolean.FALSE)
//                .build();
//
//        Article article1 = Article.builder()
//                .category(new Category(CategoryName.QNA))
//                .clicks(THREE)
//                .content(CONTENT1)
//                .isClosed(Boolean.FALSE)
//                .title(TITLE2)
//                .user(other)
//                .build();
//
//        Article article2 = Article.builder()
//                .category(new Category(CategoryName.INFO))
//                .clicks(ONE)
//                .content(CONTENT2)
//                .title(TITLE1)
//                .isClosed(Boolean.FALSE)
//                .user(user)
//                .build();
//
//        Article article3 = Article.builder()
//                .category(new Category(CategoryName.INFO))
//                .clicks(ONE)
//                .content(CONTENT2)
//                .title(TITLE1)
//                .isClosed(Boolean.FALSE)
//                .user(user)
//                .build();
//
//        article1.addAnswer(answer1);
//        article2.addAnswer(answer2);
//
//        em.persist(article1);
//        em.persist(article2);
//
//        ArticleTag articleTag1 = ArticleTag.builder().article(article1).tag(tag1).build();
//        ArticleTag articleTag2 = ArticleTag.builder().article(article1).tag(tag2).build();
//        ArticleTag articleTag3 = ArticleTag.builder().article(article1).tag(tag3).build();
//
//        ArticleTag articleTag4 = ArticleTag.builder().article(article1).tag(tag1).build();
//        ArticleTag articleTag5 = ArticleTag.builder().article(article1).tag(tag4).build();
//        ArticleTag articleTag6 = ArticleTag.builder().article(article1).tag(tag3).build();
//
//        AnswerLike answerLike1 = AnswerLike.builder().answer(answer1).user(user).build();
//        ArticleLike articleLike1 = ArticleLike.builder().article(article1).user(user).build();
//
//        AnswerLike answerLike2 = AnswerLike.builder().answer(answer1).user(other).build();
//        ArticleLike articleLike2 = ArticleLike.builder().article(article2).user(other).build();
//
//        Review review1 = Review.builder().answer(answer1).article(article1).questioner(other).answerer(user).content(CONTENT2).build();
//        Review review2 = Review.builder().answer(answer2).article(article1).questioner(other).answerer(user).content(CONTENT1).build();
//
//        ReviewBadge reviewBadge1 = ReviewBadge.builder().review(review1).badge(badge1).build();
//        ReviewBadge reviewBadge2 = ReviewBadge.builder().review(review2).badge(badge2).build();
//        ReviewBadge reviewBadge3 = ReviewBadge.builder().review(review1).badge(badge3).build();
//        ReviewBadge reviewBadge4 = ReviewBadge.builder().review(review2).badge(badge4).build();
//
//        em.persist(article1);
//        em.persist(article2);
//
//        Comment comment1 = Comment.builder()
//                .answer(answer1)
//                .content(CONTENT2)
//                .user(user)
//                .build();
//
//        Comment comment2 = Comment.builder()
//                .answer(answer2)
//                .content(CONTENT2)
//                .user(user)
//                .build();
//
//        Comment comment3 = Comment.builder()
//                .article(article1)
//                .content(CONTENT2)
//                .user(user)
//                .build();
//
//        Comment comment4 = Comment.builder()
//                .article(article2)
//                .content(CONTENT2)
//                .user(user)
//                .build();
//
//        em.persist(comment1);
//        em.persist(comment2);
//        em.persist(comment3);
//        em.persist(comment4);
//
//        other.getArticles().add(article1);
//        user.getArticles().add(article2);
//
//        user.getAnswers().add(answer1);
//        user.getAnswers().add(answer2);
//
//        article1.getArticleTags().add(articleTag1);
//        article1.getArticleTags().add(articleTag2);
//        article1.getArticleTags().add(articleTag3);
//
//        article2.getArticleTags().add(articleTag4);
//        article2.getArticleTags().add(articleTag5);
//        article2.getArticleTags().add(articleTag6);
//
//        article1.getArticleLikes().add(articleLike1);
//        article2.getArticleLikes().add(articleLike2);
//
//        answer1.getAnswerLike().add(answerLike1);
//        answer1.getAnswerLike().add(answerLike2);
//
//        review1.mapArticleAndUsers();
//        review2.mapArticleAndUsers();
//
//        reviewBadge1.mapBadgeAndReview();
//        reviewBadge2.mapBadgeAndReview();
//        reviewBadge3.mapBadgeAndReview();
//        reviewBadge4.mapBadgeAndReview();
//
//        comment1.mapArticleAndUser();
//        comment2.mapArticleAndUser();
//        comment3.mapArticleAndUser();
//        comment4.mapArticleAndUser();
//
//        int year = LocalDateTime.now().getYear();
//        int month = LocalDateTime.now().getMonthValue();
//        int day = LocalDateTime.now().getDayOfMonth();
//
//        //when
//        ResultActions perform = mockMvc.perform(get("/users/{id}/dashboard", user.getId()));
//
//        System.out.println(article3.getCreatedAt());
//        System.out.println(article2.getCreatedAt());
//
//        //then
//        perform.andExpect(status().isOk())
//                .andExpect(jsonPath("$.userId").value(user.getId()))
//                .andExpect(jsonPath("$.nickname").value(user.getNickname()))
//                .andExpect(jsonPath("$.jobType").value(user.getJobType().toString()))
//                .andExpect(jsonPath("$.grade").value(user.getGrade().toString()))
//                .andExpect(jsonPath("$.point").value(user.getPoint()))
//                .andExpect(jsonPath("$.github").value(user.getGithub()))
//                .andExpect(jsonPath("$.blog").value(user.getBlog()))
//                .andExpect(jsonPath("$.avatar.avatarId").value(user.getAvatar().getId()))
//                .andExpect(jsonPath("$.avatar.filename").value(user.getAvatar().getOriginalFilename()))
//                .andExpect(jsonPath("$.avatar.remotePath").value(user.getAvatar().getRemotePath()))
//                .andExpect(jsonPath("$.tags[0].name", is(TagName.JAVA.toString())))
//                .andExpect(jsonPath("$.reviewBadges[0].name", is(BadgeName.POO.toString())))
//                .andExpect(jsonPath("$.reviewBadges[1].name", is(BadgeName.SMART.toString())))
//                .andExpect(jsonPath("$.reviewBadges[2].name", is(BadgeName.KINDLY.toString())))
//                .andExpect(jsonPath("$.activities[0].total", is(7)))
//                .andExpect(jsonPath("$.activities[0].articleCount", is(1)))
//                .andExpect(jsonPath("$.activities[0].commentCount", is(4)))
//                .andExpect(jsonPath("$.activities[0].answerCount", is(2)))
//                .andExpect(jsonPath("$.reviews[0].reviewId").value(review2.getId()))
//                .andExpect(jsonPath("$.reviews[0].content").value(review2.getContent()))
//                .andExpect(jsonPath("$.reviews[0].userInfo.userId").value(user.getId()))
//                .andExpect(jsonPath("$.reviews[1].reviewId").value(review1.getId()))
//                .andExpect(jsonPath("$.reviews[1].content").value(review1.getContent()))
//                .andExpect(jsonPath("$.reviews[1].userInfo.userId").value(user.getId()))
//                .andDo(print());
//    }
}
