package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.config.JpaQueryFactoryConfig;
import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.Calendar;
import com.morakmorak.morak_back_end.entity.Category;
import com.morakmorak.morak_back_end.entity.enums.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

// TODO : 테스트 시 매번 calendar 365개를 저장하는 방식이 너무 비효율적이다. 해당 부분에 대한 로직 자체를 검토하고 다시 활성화 예정.
@Transactional
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(JpaQueryFactoryConfig.class)
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ReviewBadgeRepository reviewBadgeRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    CalendarRepository calendarRepository;

    UserQueryRepository userQueryRepository;

    @Autowired
    JPAQueryFactory jpaQueryFactory;

    private User user;
    private User other;
    private Badge badge1;
    private Badge badge2;
    private Badge badge3;
    private Badge badge4;
    private Article article1;
    private Article article2;
    private Answer answer1;
    private Answer answer2;
    private Tag tag1;
    private Tag tag2;
    private Tag tag3;
    private Tag tag4;
//
//    @BeforeEach
//    void init() {
//        userQueryRepository = new UserQueryRepository(jpaQueryFactory);
//
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
//        user = User.builder()
//                .nickname(NICKNAME1)
//                .password(PASSWORD1)
//                .email(EMAIL1)
//                .blog(TISTORY_URL)
//                .github(GITHUB_URL)
//                .grade(Grade.VIP)
//                .avatar(avatar1)
//                .build();
//
//        other = User.builder()
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
//        tag1 = new Tag(TagName.JAVA);
//        tag2 = new Tag(TagName.C);
//        tag3 = new Tag(TagName.NODE);
//        tag4 = new Tag(TagName.SPRING);
//
//        badge1 = new Badge(BadgeName.KINDLY);
//        badge2 = new Badge(BadgeName.POO);
//        badge3 = new Badge(BadgeName.WISE);
//        badge4 = new Badge(BadgeName.SMART);
//
//        answer1 = Answer.builder()
//                .user(user)
//                .content(CONTENT2)
//                .isPicked(Boolean.FALSE)
//                .build();
//
//        answer2 = Answer.builder()
//                .user(user)
//                .content(CONTENT2)
//                .isPicked(Boolean.FALSE)
//                .build();
//
//        article1 = Article.builder()
//                .category(new Category(CategoryName.QNA))
//                .clicks(THREE)
//                .content(CONTENT1)
//                .isClosed(Boolean.FALSE)
//                .title(TITLE2)
//                .user(other)
//                .build();
//
//        article2 = Article.builder()
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
//        userRepository.save(user);
//        userRepository.save(other);
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
//        reviewBadgeRepository.save(reviewBadge1);
//        reviewBadgeRepository.save(reviewBadge2);
//        reviewBadgeRepository.save(reviewBadge3);
//        reviewBadgeRepository.save(reviewBadge4);
//
//        review1.mapArticleAndUsers();
//        review2.mapArticleAndUsers();
//
//        reviewRepository.save(review1);
//        reviewRepository.save(review2);
//
//        reviewBadge1.mapBadgeAndReview();
//        reviewBadge2.mapBadgeAndReview();
//        reviewBadge3.mapBadgeAndReview();
//        reviewBadge4.mapBadgeAndReview();
//
//        commentRepository.save(comment1);
//        commentRepository.save(comment2);
//        commentRepository.save(comment3);
//        commentRepository.save(comment4);
//
//        comment1.mapArticleAndUser();
//        comment2.mapArticleAndUser();
//        comment3.mapArticleAndUser();
//        comment4.mapArticleAndUser();
//    }
//
//    @Test
//    @DisplayName("테스트 전제 조건 확인 _ 위에서 저장한 값과 DB의 값이 일치한다.")
//    void test() {
//        // 1. 유저에 대한 연관관계 확인
//        assertThat(user.getArticles().size()).isEqualTo(ONE);
//        assertThat(other.getArticles().size()).isEqualTo(ONE);
//        assertThat(user.getAnswers().size()).isEqualTo(TWO);
//        assertThat(user.getAnswers().stream()
//                .map(e -> e.getReview()).collect(Collectors.toList()).size()).isEqualTo(TWO);
//        assertThat(user.getReceivedReviews().size()).isEqualTo(TWO);
//        assertThat(user.getAvatar().getRemotePath()).isEqualTo(CONTENT1);
//        assertThat(user.getComments().size()).isEqualTo(FOUR);
//
//        List<ReviewBadge> reviewBadges = user.getReceivedReviews()
//                .stream().map(Review::getReviewBadges).collect(Collectors.toList())
//                .stream().flatMap(Collection::stream).collect(Collectors.toList());
//
//        assertThat(reviewBadges.size()).isEqualTo(FOUR);
//
//        // 2. 아티클에 대한 연관관계 확인
//        assertThat(article1.getArticleTags().size()).isEqualTo(THREE);
//        assertThat(article2.getArticleTags().size()).isEqualTo(THREE);
//
//        assertThat(article1.getComments().size()).isEqualTo(ONE);
//        assertThat(article2.getComments().size()).isEqualTo(ONE);
//
//        List<Tag> tags = article1.getArticleTags().stream()
//                .map(e -> e.getTag())
//                .collect(Collectors.toList());
//
//        assertThat(tags.size()).isEqualTo(THREE);
//        assertThat(article1.getUser().getNickname()).isEqualTo(NICKNAME2);
//        assertThat(article2.getUser().getNickname()).isEqualTo(NICKNAME1);
//        assertThat(article1.getIsClosed()).isTrue();
//
//        // 3. 답글에 대한 연관관계 확인
//        assertThat(answer1.getComments().size()).isEqualTo(ONE);
//        assertThat(answer2.getComments().size()).isEqualTo(ONE);
//
//        assertThat(answer1.getUser()).isNotNull();
//        assertThat(answer2.getUser()).isNotNull();
//
//        assertThat(answer1.getIsPicked()).isTrue();
//        assertThat(answer2.getReview()).isNotNull();
//        assertThat(answer1.getReview()).isNotNull();
//
//        List<Calendar> all = calendarRepository.findAll();
//
//        all.forEach(e -> System.out.println(e.getDate()));
//    }
//
//    @Test
//    @DisplayName("일별 활동량 반환")
//    void getActivities() {
//        //given
//        //when
//        //then
//        int year = LocalDate.now().getYear();
//        Date startDate = Date.valueOf(year + "-01-01");
//        Date endDate = Date.valueOf(year+1 + "-01-01");
//
//        List<ActivityQueryDto> userActivities = userRepository.getUserActivitiesOnThisYear(startDate, endDate, user.getId());
//        assertThat(userActivities.get(0).getArticleCount()).isEqualTo(user.getArticles().size());
//        assertThat(userActivities.get(0).getAnswerCount()).isEqualTo(user.getAnswers().size());
//        assertThat(userActivities.get(0).getDate()).isEqualTo(LocalDate.now().toString());
//        assertThat(userActivities.get(0).getCommentCount()).isEqualTo(user.getComments().size());
//        assertThat(userActivities.get(0).getTotal()).isEqualTo(user.getArticles().size() + user.getComments().size() + user.getAnswers().size());
//    }
//
//    @Test
//    @DisplayName("탑3 태그 반환")
//    void getTopTag() {
//        //given //when
//        List<TagQueryDto> usersTop3Tags = userRepository.getUsersTop3Tags(user.getId());
//
//        //then
//        assertThat(usersTop3Tags.size()).isEqualTo(THREE);
//    }
//
//    @Test
//    @DisplayName("탑3 배지 반환")
//    void getTopBadge() {
//        // given //when
//        List<BadgeQueryDto> usersTop3Badges = userRepository.getUsersTop3Badges(user.getId());
//
//        //then
//        assertThat(usersTop3Badges.size()).isEqualTo(THREE);
//    }
//
//    @Test
//    @DisplayName("최근 질문글 50개 반환")
//    void get50RecentQuestions() {
//        // given
//
//        for (int i=1; i<=50; i++) {
//            Article article = Article.builder()
//                    .isClosed(false)
//                    .user(user)
//                    .title("제목" + i)
//                    .content("내용" + i)
//                    .category(new Category(CategoryName.QNA))
//                    .clicks(i)
//                            .build();
//
//            entityManager.persist(article);
//
//            ArticleLike likeTable = ArticleLike.builder()
//                    .user(user)
//                    .article(article)
//                    .build();
//
//            likeTable.mapUserAndArticleWithLike();
//
//            Comment comment = Comment.builder().article(article).user(user)
//                    .content("내용" + i).user(user).build();
//
//            comment.mapArticleAndUser();
//
//            ArticleTag articleTag1 = ArticleTag.builder()
//                    .tag(tag1)
//                    .article(article)
//                    .build();
//
//            ArticleTag articleTag2 = ArticleTag.builder()
//                    .tag(tag1)
//                    .article(article)
//                    .build();
//
//            ArticleTag articleTag3 = ArticleTag.builder()
//                    .tag(tag1)
//                    .article(article)
//                    .build();
//
//            ArticleTag articleTag4 = ArticleTag.builder()
//                    .tag(tag1)
//                    .article(article)
//                    .build();
//
//            articleTag1.mapArticleAndTagWithArticleTag();
//            articleTag2.mapArticleAndTagWithArticleTag();
//            articleTag3.mapArticleAndTagWithArticleTag();
//            articleTag4.mapArticleAndTagWithArticleTag();
//
//            entityManager.persist(comment);
//            entityManager.persist(articleTag1);
//            entityManager.persist(articleTag2);
//            entityManager.persist(articleTag3);
//            entityManager.persist(articleTag4);
//            entityManager.flush();
//        }
//
//        // when
//        List<Article> recentQuestions = userQueryRepository.get50RecentQuestions(user.getId());
//
//        //then
//        assertThat(recentQuestions.size()).isEqualTo(50);
//        assertThat(recentQuestions.get(0).getAnswers().size()).isEqualTo(0);
//        assertThat(recentQuestions.get(0).getComments().size()).isEqualTo(1);
//        assertThat(recentQuestions.get(0).getArticleLikes().size()).isEqualTo(1);
//        assertThat(recentQuestions.get(0).getTitle()).isEqualTo("제목50");
//        assertThat(recentQuestions.get(49).getArticleLikes().size()).isEqualTo(1);
//        assertThat(recentQuestions.get(49).getTitle()).isEqualTo("제목1");
//        assertThat(recentQuestions.get(49).getComments().size()).isEqualTo(1);
//        assertThat(recentQuestions.get(0).getUser().getId()).isEqualTo(user.getId());
//        assertThat(recentQuestions.get(0).getUser().getAvatar().getRemotePath()).isEqualTo(CONTENT1);
//        assertThat(recentQuestions.get(0).getArticleTags().size()).isEqualTo(4);
//    }
//
//    @Test
//    @DisplayName("받은 리뷰 모두 조회")
//    void getReceivedReviews() {
//        //given
//        //when
//        List<ReviewDto.Response> receivedReviews = userQueryRepository.getReceivedReviews(user.getId());
//
//        //then
//        assertThat(receivedReviews.size()).isEqualTo(TWO);
//        assertThat(receivedReviews.get(0).getUserInfo().getUserId()).isEqualTo(user.getId());
//        assertThat(receivedReviews.get(0).getUserInfo().getNickname()).isEqualTo(user.getNickname());
//        assertThat(receivedReviews.get(0).getUserInfo().getGrade()).isEqualTo(user.getGrade());
//    }
//
//    @Test
//    @DisplayName("유저 기본 연관관계 조회")
//    void getUserDashboardBasicInfo() {
//        //given
//        //when
//        UserDto.ResponseDashBoard dashboardBasicInfo = userQueryRepository.getUserDashboardBasicInfo(user.getId());
//
//        //then
//        assertThat(dashboardBasicInfo.getUserId()).isEqualTo(user.getId());
//        assertThat(dashboardBasicInfo.getNickname()).isEqualTo(user.getNickname());
//        assertThat(dashboardBasicInfo.getEmail()).isEqualTo(user.getEmail());
//        assertThat(dashboardBasicInfo.getAvatar().getAvatarId()).isEqualTo(user.getAvatar().getId());
//        assertThat(dashboardBasicInfo.getAvatar().getRemotePath()).isEqualTo(user.getAvatar().getRemotePath());
//    }
}