package com.morakmorak.morak_back_end.mapper;

import com.morakmorak.morak_back_end.dto.ArticleDto;
import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.dto.TagDto;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.entity.enums.TagName;
import com.morakmorak.morak_back_end.repository.ArticleRepository;
import com.morakmorak.morak_back_end.util.ArticleTestConstants;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ArticleMapperTest {
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    TagMapper tagMapper;

    @Autowired
    EntityManager em;

    @BeforeEach
    public void data() {
        Tag JAVA = Tag.builder().name(TagName.JAVA).build();
        em.persist(JAVA);

        Category info = Category.builder().name(CategoryName.INFO).build();
        em.persist(info);


        Avatar avatar = Avatar.builder().remotePath("remotePath").originalFilename("originalFileName").build();
        em.persist(avatar);
        User user = User.builder().avatar(avatar).nickname("testNickname").grade(Grade.BRONZE).build();
        em.persist(user);


        List<Answer> answers = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Answer answer = Answer.builder().build();
            answers.add(answer);
            Comment comment = Comment.builder().user(user).content("댓글입니다.").build();
            comments.add(comment);
            em.persist(answer);
            em.persist(comment);
        }

        ArticleTag articleTagJava = ArticleTag.builder().tag(JAVA).build();
        List<ArticleLike> articleLikes = new ArrayList<>();
        ArticleLike articleLike = ArticleLike.builder().build();
        articleLikes.add(articleLike);
        em.persist(articleLike);
        Article article
                = Article.builder().title("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~")
                .content("콘탠트입니다. 제발 됬으면 좋겠습니다.")
                .articleTags(List.of(articleTagJava))
                .category(info)
                .articleLikes(articleLikes)
                .comments(comments)
                .answers(answers)
                .user(user)
                .build();
        info.getArticleList().add(article);
        articleTagJava.injectMappingForArticleAndTag(article);
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
        List<TagDto.SimpleTag> tags = article.getArticleTags().stream()
                .map(articleTag -> tagMapper.tagEntityToTagDto(articleTag.getTag())).collect(Collectors.toList());
        Integer likes = article.getArticleLikes().size();
        //when
        ArticleDto.ResponseListTypeArticle test =
                articleMapper.articleToResponseSearchResultArticle(article, comment, answer, tags, likes);

        //then
        assertThat(test.getArticleId()).isEqualTo(article.getId());
        assertThat(test.getCategory()).isEqualTo(article.getCategory().getName());
        assertThat(test.getTitle()).isEqualTo(article.getTitle());
        assertThat(test.getClicks()).isEqualTo(article.getClicks());
        assertThat(test.getLikes()).isEqualTo(article.getArticleLikes().size());
        assertThat(test.getIsClosed()).isEqualTo(article.getIsClosed());

        assertThat(test.getCommentCount()).isEqualTo(article.getComments().size());
        assertThat(test.getAnswerCount()).isEqualTo(article.getAnswers().size());
        assertThat(test.getCreatedAt()).isEqualTo(article.getCreatedAt());
        assertThat(test.getLastModifiedAt()).isEqualTo(article.getLastModifiedAt());

        assertThat(test.getTags().get(0).getTagId()).isEqualTo(tags.get(0).getTagId());
        assertThat(test.getTags().get(0).getName()).isEqualTo(TagName.JAVA);

        assertThat(test.getUserInfo().getUserId()).isEqualTo(article.getUser().getId());
        assertThat(test.getUserInfo().getNickname()).isEqualTo(article.getUser().getNickname());
        assertThat(test.getUserInfo().getGrade()).isEqualTo(article.getUser().getGrade());

        assertThat(test.getAvatar().getAvatarId()).isEqualTo(article.getUser().getAvatar().getId());
        assertThat(test.getAvatar().getRemotePath()).isEqualTo(article.getUser().getAvatar().getRemotePath());
        assertThat(test.getAvatar().getFilename()).isEqualTo(article.getUser().getAvatar().getOriginalFilename());

    }

    @Test
    @DisplayName("articleToResponseDetailArticle 매퍼 작동 테스트")
    public void articleToResponseDetailArticle_test(){
    //given
        Article article = articleRepository.findArticleByContent("콘탠트입니다. 제발 됬으면 좋겠습니다.").orElseThrow();

        Boolean isLiked = true;
        Boolean isBookmarked = true;

        List<TagDto.SimpleTag> tags = article.getArticleTags().stream()
                .map(articleTag -> tagMapper.tagEntityToTagDto(articleTag.getTag())).collect(Collectors.toList());

        List<CommentDto.Response> comments = article.getComments().stream()
                .map(comment -> commentMapper.commentToCommentDto(comment)).collect(Collectors.toList());

        Integer likes = article.getArticleLikes().size();


        //when
        ArticleDto.ResponseDetailArticle test = articleMapper
                .articleToResponseDetailArticle(article, isLiked, isBookmarked, tags, comments, likes);

        //then
        assertThat(test.getArticleId()).isEqualTo(article.getId());
        assertThat(test.getCategory()).isEqualTo(article.getCategory().getName());
        assertThat(test.getTitle()).isEqualTo(article.getTitle());
        assertThat(test.getClicks()).isEqualTo(article.getClicks());
        assertThat(test.getLikes()).isEqualTo(article.getArticleLikes().size());
        assertThat(test.getIsClosed()).isEqualTo(article.getIsClosed());

        assertThat(test.getIsLiked()).isTrue();
        assertThat(test.getIsBookmarked()).isTrue();

        assertThat(test.getCreatedAt()).isEqualTo(article.getCreatedAt());
        assertThat(test.getLastModifiedAt()).isEqualTo(article.getLastModifiedAt());

        assertThat(test.getTags().get(0).getTagId()).isEqualTo(tags.get(0).getTagId());
        assertThat(test.getTags().get(0).getName()).isEqualTo(TagName.JAVA);

        assertThat(test.getUserInfo().getUserId()).isEqualTo(article.getUser().getId());
        assertThat(test.getUserInfo().getNickname()).isEqualTo(article.getUser().getNickname());
        assertThat(test.getUserInfo().getGrade()).isEqualTo(article.getUser().getGrade());

        assertThat(test.getAvatar().getAvatarId()).isEqualTo(article.getUser().getAvatar().getId());
        assertThat(test.getAvatar().getRemotePath()).isEqualTo(article.getUser().getAvatar().getRemotePath());
        assertThat(test.getAvatar().getFilename()).isEqualTo(article.getUser().getAvatar().getOriginalFilename());

        assertThat(test.getComments()).isNotEmpty();

    }
}