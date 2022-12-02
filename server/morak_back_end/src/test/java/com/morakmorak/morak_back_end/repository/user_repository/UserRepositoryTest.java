package com.morakmorak.morak_back_end.repository.user_repository;

import com.morakmorak.morak_back_end.config.JpaQueryFactoryConfig;
import com.morakmorak.morak_back_end.dto.ActivityDto;
import com.morakmorak.morak_back_end.dto.TagQueryDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.*;
import com.morakmorak.morak_back_end.repository.*;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
import com.morakmorak.morak_back_end.repository.user.UserQueryRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.mockito.BDDMockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@Transactional
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(JpaQueryFactoryConfig.class)
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager entityManager;

    UserQueryRepository userQueryRepository;

    @Autowired
    JPAQueryFactory jpaQueryFactory;

    @MockBean
    DateTimeProvider dateTimeProvider;

    @SpyBean
    AuditingHandler auditingHandler;

    LocalDate january1st = LocalDate.now().withDayOfYear(1);
    LocalDate december31st = LocalDate.now().withDayOfYear(365);

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

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        auditingHandler.setDateTimeProvider(dateTimeProvider);
    }

    @BeforeEach
    void init() {
        userQueryRepository = new UserQueryRepository(jpaQueryFactory);

        user = User.builder().nickname(NICKNAME1).point(100).build();
        other = User.builder().nickname(NICKNAME2).point(1000).build();

        Article article_user1 = Article.builder().user(user).build();
        Article article_user2 = Article.builder().user(user).build();

        article_user1.injectUserForMapping(user);
        article_user2.injectUserForMapping(user);

        Article article_other1 = Article.builder().user(other).build();
        article_other1.injectUserForMapping(other);

        Answer answer_other1 = Answer.builder().user(other).article(article_user1).build();
        answer_other1.injectUser(other);

        ArticleLike articleLike1 = ArticleLike.builder().article(article_user1).user(other).build();
        ArticleLike articleLike2 = ArticleLike.builder().article(article_user1).user(other).build();
        ArticleLike articleLike3 = ArticleLike.builder().article(article_user1).user(other).build();
        ArticleLike articleLike4 = ArticleLike.builder().article(article_user1).user(other).build();
        articleLike1.mapUserAndArticleWithLike();
        articleLike1.mapUserAndArticleWithLike();

        ArticleLike articleLike_user1 = ArticleLike.builder().article(article_other1).user(user).build();
        ArticleLike articleLike_user2 = ArticleLike.builder().article(article_other1).user(user).build();
        articleLike_user1.mapUserAndArticleWithLike();
        articleLike_user2.mapUserAndArticleWithLike();

        AnswerLike answerLike_user1 = AnswerLike.builder().answer(answer_other1).user(other).build();

        entityManager.persist(user);
        entityManager.persist(other);

        entityManager.persist(article_user1);
        entityManager.persist(article_user2);

        entityManager.persist(article_other1);

        entityManager.persist(answer_other1);

        entityManager.persist(articleLike1);
        entityManager.persist(articleLike2);

        entityManager.persist(articleLike_user1);
        entityManager.persist(articleLike_user2);

        entityManager.persist(answerLike_user1);
    }

    @Test
    @DisplayName("포인트순 조회시 ohter가 1위")
    void getUserRankPage1() {
        //given
        PageRequest page = PageRequest.of(0, 10, Sort.by("point"));
        //when
        Page<User> rank = userQueryRepository.getRankData(page);
        //then
        assertThat(rank.toList().get(0).getNickname()).isEqualTo(NICKNAME2);
        assertThat(rank.toList().get(1).getNickname()).isEqualTo(NICKNAME1);
    }

    @Test
    @DisplayName("게시글 작성순 조회시 user가 1위")
    void getUserRankPage2() {
        //given
        PageRequest page = PageRequest.of(0, 2, Sort.by("articles"));
        //when
        Page<User> rank = userQueryRepository.getRankData(page);
        //then

        assertThat(rank.toList().get(0).getNickname()).isEqualTo(NICKNAME1);
        assertThat(rank.toList().get(1).getNickname()).isEqualTo(NICKNAME2);
    }

    @Test
    @DisplayName("답변순 조회시 other가 1위,")
    void getUserRankPage3() {
        //given
        PageRequest page = PageRequest.of(0, 2, Sort.by("answers"));
        //when
        Page<User> rank = userQueryRepository.getRankData(page);
        //then
        assertThat(rank.toList().get(0).getNickname()).isEqualTo(NICKNAME2);
        assertThat(rank.toList().get(1).getNickname()).isEqualTo(NICKNAME1);
    }

    @Test
    @DisplayName("연간 일별 데이터를 날짜로 groupby하여 반환한다(article)")
    void getUserArticlesDataBetween1() {
        // given
        User user = User.builder().build();
        entityManager.persist(user);

        BDDMockito.given(dateTimeProvider.getNow()).willReturn(Optional.of(LocalDateTime.of(2022,1,6,0,0,0)));

        for (int i=0; i<10; i++) {
            Article article = Article.builder().user(user).build();
            article.injectUserForMapping(user);
            entityManager.persist(article);
        }

        for (int i=1; i<=5; i++) {
            BDDMockito.given(dateTimeProvider.getNow()).willReturn(Optional.of(LocalDateTime.of(2022,1,i,0,0,0)));
            Article article = Article.builder().user(user).build();
            article.injectUserForMapping(user);
            entityManager.persist(article);
        }
        // when
        List<ActivityDto.Temporary> result = userQueryRepository.getUserArticlesDataBetween(january1st, december31st, user.getId());

        // then
        assertThat(result.size()).isEqualTo(6);
        assertThat(result.get(0).getCount()).isEqualTo(10);
        assertThat(result.get(1).getCount()).isEqualTo(1);
        assertThat(result.get(2).getCount()).isEqualTo(1);
        assertThat(result.get(3).getCount()).isEqualTo(1);
        assertThat(result.get(4).getCount()).isEqualTo(1);
        assertThat(result.get(5).getCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("연간 일별 데이터를 날짜로 groupby하여 반환한다(answer)")
    void getUserArticlesDataBetween2() {
        // given
        User user = User.builder().build();
        entityManager.persist(user);

        BDDMockito.given(dateTimeProvider.getNow()).willReturn(Optional.of(LocalDateTime.of(2022,1,6,0,0,0)));

        for (int i=0; i<10; i++) {
            Article article = Article.builder().user(user).build();
            Answer answer = Answer.builder().article(article).user(user).build();
            article.injectUserForMapping(user);
            answer.injectUser(user);
            entityManager.persist(article);
            entityManager.persist(answer);
        }

        for (int i=1; i<=5; i++) {
            BDDMockito.given(dateTimeProvider.getNow()).willReturn(Optional.of(LocalDateTime.of(2022,1,i,0,0,0)));
            Article article = Article.builder().user(user).build();
            Answer answer = Answer.builder().article(article).user(user).build();
            article.injectUserForMapping(user);
            answer.injectUser(user);
            entityManager.persist(article);
            entityManager.persist(answer);
        }
        // when
        List<ActivityDto.Temporary> result = userQueryRepository.getUserAnswersDataBetween(january1st, december31st, user.getId());

        // then
        assertThat(result.size()).isEqualTo(6);
        assertThat(result.get(0).getCount()).isEqualTo(10);
        assertThat(result.get(1).getCount()).isEqualTo(1);
        assertThat(result.get(2).getCount()).isEqualTo(1);
        assertThat(result.get(3).getCount()).isEqualTo(1);
        assertThat(result.get(4).getCount()).isEqualTo(1);
        assertThat(result.get(5).getCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("연간 일별 데이터를 날짜로 groupby하여 반환한다(comment)")
    void getUserArticlesDataBetween3() {
        // given
        User user = User.builder().build();
        entityManager.persist(user);

        BDDMockito.given(dateTimeProvider.getNow()).willReturn(Optional.of(LocalDateTime.of(2022,1,6,0,0,0)));

        for (int i=0; i<10; i++) {
            Article article = Article.builder().user(user).build();
            Comment comment = Comment.builder().article(article).user(user).build();
            article.injectUserForMapping(user);
            comment.injectUser(user);
            entityManager.persist(article);
            entityManager.persist(comment);
        }

        for (int i=1; i<=5; i++) {
            BDDMockito.given(dateTimeProvider.getNow()).willReturn(Optional.of(LocalDateTime.of(2022,1,i,0,0,0)));
            Article article = Article.builder().user(user).build();
            Comment comment = Comment.builder().article(article).user(user).build();
            article.injectUserForMapping(user);
            entityManager.persist(article);
            entityManager.persist(comment);
        }
        // when
        List<ActivityDto.Temporary> result = userQueryRepository.getUserCommentsDataBetween(january1st, december31st, user.getId());

        // then
        assertThat(result.size()).isEqualTo(6);
        assertThat(result.get(0).getCount()).isEqualTo(10);
        assertThat(result.get(1).getCount()).isEqualTo(1);
        assertThat(result.get(2).getCount()).isEqualTo(1);
        assertThat(result.get(3).getCount()).isEqualTo(1);
        assertThat(result.get(4).getCount()).isEqualTo(1);
        assertThat(result.get(5).getCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("해당 일자의 article 작성 기록을 모두 가져온다")
    void getWriteArticleHistoryOn() {
        //given
        BDDMockito.given(dateTimeProvider.getNow()).willReturn(Optional.of(LocalDate.now()));
        User user = User.builder().build();
        userRepository.save(user);

        for (int i=0; i<5; i++) {
            Article article = Article.builder().title(CONTENT1).build();
            Comment comment1 = Comment.builder().article(article).build();
            Comment comment2 = Comment.builder().article(article).build();
            ArticleLike articleLike = ArticleLike.builder().article(article).user(other).build();
            article.injectUserForMapping(user);
            articleLike.mapUserAndArticleWithLike();;
            comment1.injectArticle(article);
            comment2.injectArticle(article);

            entityManager.persist(article);
            entityManager.persist(articleLike);
            entityManager.persist(comment1);
            entityManager.persist(comment2);
        }

        //when
        List<ActivityDto.Article> result = userQueryRepository.getWrittenArticleHistoryOn(LocalDate.now(), user.getId());

        //then
        assertThat(result.size()).isEqualTo(5);
        assertThat(result.get(0).getTitle()).isEqualTo(CONTENT1);
        assertThat(result.get(0).getLikeCount()).isEqualTo(1L);
        assertThat(result.get(0).getCommentCount()).isEqualTo(2L);
    }

    @Test
    @DisplayName("해당 일자의 answer 작성 기록을 모두 가져온다")
    void getWrittenAnswerHistoryOn() {
        //given
        BDDMockito.given(dateTimeProvider.getNow()).willReturn(Optional.of(LocalDate.now()));
        User user = User.builder().build();
        userRepository.save(user);

        for (int i=0; i<5; i++) {
            Article article = Article.builder().title(CONTENT1).user(other).build();
            Answer answer = Answer.builder().article(article).user(user).build();
            Comment comment1 = Comment.builder().article(article).build();
            Comment comment2 = Comment.builder().article(article).build();
            ArticleLike articleLike = ArticleLike.builder().article(article).user(other).build();
            article.injectUserForMapping(other);
            answer.injectUser(user);
            articleLike.mapUserAndArticleWithLike();;

            entityManager.persist(article);
            entityManager.persist(articleLike);
            entityManager.persist(comment1);
            entityManager.persist(comment2);
        }

        //when
        List<ActivityDto.Article> result = userQueryRepository.getWrittenAnswerHistoryOn(LocalDate.now(), user.getId());

        //then
        assertThat(result.size()).isEqualTo(5);
        assertThat(result.get(0).getTitle()).isEqualTo(CONTENT1);
        assertThat(result.get(0).getLikeCount()).isEqualTo(1L);
        assertThat(result.get(0).getCommentCount()).isEqualTo(2L);
    }

    @Test
    @DisplayName("해당 일자의 comment 작성 기록을 모두 가져온다")
    void getWrittenCommentHistoryOn() {
        //given
        BDDMockito.given(dateTimeProvider.getNow()).willReturn(Optional.of(LocalDate.now()));
        User user = User.builder().build();
        userRepository.save(user);

        List<Long> articleIds = new ArrayList<>();

        for (int i=0; i<5; i++) {
            Article article = Article.builder().title(CONTENT1).build();
            Comment comment1 = Comment.builder().user(user).content(CONTENT2).article(article).build();
            Comment comment2 = Comment.builder().user(user).content(CONTENT2).article(article).build();
            ArticleLike articleLike = ArticleLike.builder().article(article).user(other).build();
            articleLike.mapUserAndArticleWithLike();;
            comment1.injectArticle(article);
            comment2.injectArticle(article);

            entityManager.persist(article);
            entityManager.persist(articleLike);
            entityManager.persist(comment1);
            entityManager.persist(comment2);

            articleIds.add(article.getId());
        }

        //when
        List<ActivityDto.Comment> result = userQueryRepository.getWrittenCommentHistoryOn(LocalDate.now(), user.getId());

        //then
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.get(0).getContent()).isEqualTo(CONTENT2);
    }
    
    @Test
    @DisplayName("유저의 활동 내역이 없어도 예외가 발생하지 않는다")
    void getTopTags() {
        //given
        User user = User.builder().build();
        Article build = Article.builder().build();
        Tag build1 = Tag.builder().name(TagName.C).build();
        ArticleTag build2 = ArticleTag.builder().article(build).tag(build1).build();

        Article a = Article.builder().build();
        Tag b = Tag.builder().name(TagName.JAVA).build();
        ArticleTag c = ArticleTag.builder().article(build).tag(build1).build();

        Article a1 = Article.builder().build();
        Tag b1 = Tag.builder().name(TagName.AWS).build();
        ArticleTag c1 = ArticleTag.builder().article(build).tag(build1).build();


        build2.mapArticleAndTagWithArticleTag();
        user.addArticle(build);
        c.mapArticleAndTagWithArticleTag();
        c1.mapArticleAndTagWithArticleTag();
        entityManager.persist(user);
        entityManager.persist(build);
        entityManager.persist(build2);

        entityManager.persist(a);
        entityManager.persist(b);
        entityManager.persist(c);

        entityManager.persist(a1);
        entityManager.persist(b1);
        entityManager.persist(c1);

        entityManager.persist(build1);
        
        //when //then
        List<TagQueryDto> result = userRepository.getUsersTop3Tags(user.getId());
    }

    @Test
    @DisplayName("삭제된 게시글은 조회되지 않는다.")
    void test() {
        //given
        User user = User.builder().build();
        userRepository.save(user);

        for (int i=0; i<50; i++) {
            Category category = new Category(CategoryName.QNA);
            Article article = Article.builder().category(category).articleStatus(ArticleStatus.REMOVED).user(user).build();
            user.addArticle(article);
            entityManager.persist(category);
            entityManager.persist(article);
        }

        //when
        List<Article> recentQuestions = userQueryRepository.get50RecentQuestions(user.getId());

        //then
        Assertions.assertThat(recentQuestions.size()).isEqualTo(0);
    }
}