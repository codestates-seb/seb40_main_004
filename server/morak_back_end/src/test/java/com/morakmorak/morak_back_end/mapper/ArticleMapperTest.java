package com.morakmorak.morak_back_end.mapper;

import com.morakmorak.morak_back_end.dto.ArticleDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.entity.enums.TagName;
import com.morakmorak.morak_back_end.repository.ArticleRepository;
import com.morakmorak.morak_back_end.util.ArticleTestConstants;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ArticleMapperTest {
    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    EntityManager em;

    @BeforeEach
    public void data() {
        Tag JAVA = Tag.builder().name(TagName.JAVA).build();
        em.persist(JAVA);

        Category info = Category.builder().name("info").build();
        em.persist(info);


        Avatar avatar = Avatar.builder().remotePath("remotePath").originalFileName("originalFileName").build();
        em.persist(avatar);
        User user = User.builder().avatar(avatar).nickname("testNickname").grade(Grade.BRONZE).build();
        em.persist(user);


        List<Answer> answers = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Answer answer = Answer.builder().build();
            answers.add(answer);
            Comment comment = Comment.builder().build();
            comments.add(comment);
            em.persist(answer);
            em.persist(comment);
        }

        ArticleTag articleTagJava = ArticleTag.builder().tag(JAVA).build();
        Article article
                = Article.builder().title("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~")
                .content("콘탠트입니다. 제발 됬으면 좋겠습니다.")
                .articleTags(List.of(articleTagJava))
                .category(info)
                .vote(Vote.builder().count(10).build())
                .comments(comments)
                .answers(answers)
                .user(user)
                .build();
        info.getArticleList().add(article);
        articleTagJava.injectMappingForArticleAndTagForTesting(article);
        em.persist(article);

    }


    @Test
    @DisplayName("requestUpdateArticleToEntity 매퍼 작동 테스트")
    public void requestUpdateArticleToEntityTest() throws Exception {
        //given
        ArticleDto.RequestUpdateArticle requestUpdateArticle = ArticleTestConstants.REQUEST_UPDATE_ARTICLE;

        //when
        Article article = articleMapper.requestUpdateArticleToEntity(requestUpdateArticle, 1L);
        //then
        assertThat(article.getId()).isEqualTo(1L);
        assertThat(article.getContent()).isEqualTo(requestUpdateArticle.getContent());
        assertThat(article.getThumbnail()).isEqualTo(requestUpdateArticle.getThumbnail());

    }


    @Test
    @DisplayName("articleToResponseSearchResultArticle 매퍼 작동 테스트")
    public void articleToResponseSearchResultArticle() {
        //given
        Article article = articleRepository.findArticleByContent("콘탠트입니다. 제발 됬으면 좋겠습니다.").orElseThrow();

        int comment = article.getComments().size();
        int answer = article.getAnswers().size();
        List<Tag> tags = article.getArticleTags().stream()
                .map(articleTag -> articleTag.getTag()).collect(Collectors.toList());

        //when
        ArticleDto.ResponseListTypeArticle test =
                articleMapper.articleToResponseSearchResultArticle(article, comment, answer, tags);

        //then
        assertThat(test.getArticleId()).isEqualTo(article.getId());
        assertThat(test.getCategory()).isEqualTo(article.getCategory().getName());
        assertThat(test.getTitle()).isEqualTo(article.getTitle());
        assertThat(test.getClicks()).isEqualTo(article.getClicks());
        assertThat(test.getLikes()).isEqualTo(article.getVote().getCount());
        assertThat(test.getIsClosed()).isEqualTo(article.getIsClosed());

        assertThat(test.getCommentCount()).isEqualTo(article.getComments().size());
        assertThat(test.getAnswerCount()).isEqualTo(article.getAnswers().size());
        assertThat(test.getCreatedAt()).isEqualTo(article.getCreatedAt());
        assertThat(test.getLastModifiedAt()).isEqualTo(article.getLastModifiedAt());

        assertThat(test.getUserInfo().getUserId()).isEqualTo(article.getUser().getId());
        assertThat(test.getUserInfo().getNickname()).isEqualTo(article.getUser().getNickname());
        assertThat(test.getUserInfo().getGrade()).isEqualTo(article.getUser().getGrade());

        assertThat(test.getAvatarInfo().getAvatarId()).isEqualTo(article.getUser().getAvatar().getId());
        assertThat(test.getAvatarInfo().getRemotePath()).isEqualTo(article.getUser().getAvatar().getRemotePath());
        assertThat(test.getAvatarInfo().getFileName()).isEqualTo(article.getUser().getAvatar().getOriginalFileName());

    }
}