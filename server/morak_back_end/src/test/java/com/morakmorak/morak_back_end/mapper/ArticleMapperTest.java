package com.morakmorak.morak_back_end.mapper;

import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.entity.enums.ReportReason;
import com.morakmorak.morak_back_end.entity.enums.TagName;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
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
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
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
        articleTagJava.injectTo(article);
        em.persist(article);

    }

    @Test
    @DisplayName("requestUploadArticleToEntity 매퍼 작동 테스트")
    public void requestUploadArticleToEntityTest() throws Exception {
        //given
        List<FileDto.RequestFileWithId> files = new ArrayList<>();
        FileDto.RequestFileWithId fileDto = FileDto.RequestFileWithId.builder().fileId(1L).build();
        files.add(fileDto);

        TagDto.SimpleTag tagDto = TagDto.SimpleTag.builder().tagId(1L).name(TagName.JAVA).build();
        List<TagDto.SimpleTag> tags = new ArrayList<>();
        tags.add(tagDto);

        ArticleDto.RequestUploadArticle requestUploadArticle = ArticleDto.RequestUploadArticle.builder()
                .title("타이틀입니다.")
                .content("내용입니다.")
                .thumbnail(1L)
                .category(CategoryName.QNA)
                .fileId(files)
                .tags(tags)
                .build();

        //when
        Article article = articleMapper.requestUploadArticleToEntity(requestUploadArticle);
        //then
        assertThat(article.getContent()).isEqualTo(requestUploadArticle.getContent());
        assertThat(article.getArticleTags().get(0).getTag().getName()).isEqualTo(requestUploadArticle.getTags().get(0).getName());
        assertThat(article.getFiles().get(0).getId()).isEqualTo(requestUploadArticle.getFileId().get(0).getFileId());
        assertThat(article.getThumbnail()).isEqualTo(requestUploadArticle.getThumbnail());

    }


    @Test
    @DisplayName("requestUpdateArticleToEntity 매퍼 작동 테스트")
    public void requestUpdateArticleToEntityTest() throws Exception {
        //given
        List<FileDto.RequestFileWithId> files = new ArrayList<>();
        FileDto.RequestFileWithId fileDto = FileDto.RequestFileWithId.builder().fileId(1L).build();
        files.add(fileDto);

        TagDto.SimpleTag tagDto = TagDto.SimpleTag.builder().tagId(1L).name(TagName.JAVA).build();
        List<TagDto.SimpleTag> tags = new ArrayList<>();
        tags.add(tagDto);

        ArticleDto.RequestUpdateArticle requestUpdateArticle = ArticleDto.RequestUpdateArticle.builder()
                .title("타이틀입니다.")
                .content("내용입니다.")
                .thumbnail(1L)
                .fileId(files)
                .tags(tags)
                .build();

        //when
        Article article = articleMapper.requestUpdateArticleToEntity(requestUpdateArticle, 1L);
        //then
        assertThat(article.getId()).isEqualTo(1L);
        assertThat(article.getTitle()).isEqualTo(requestUpdateArticle.getTitle());
        assertThat(article.getContent()).isEqualTo(requestUpdateArticle.getContent());
        assertThat(article.getThumbnail()).isEqualTo(requestUpdateArticle.getThumbnail());
        assertThat(article.getArticleTags().get(0).getTag().getName()).isEqualTo(requestUpdateArticle.getTags().get(0).getName());
        assertThat(article.getFiles().get(0).getId()).isEqualTo(requestUpdateArticle.getFileId().get(0).getFileId());
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
    public void articleToResponseDetailArticle_test() {
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

    @Test
    @DisplayName("articleToResponseBlockedArticle 매퍼 신고 5번 당했을경우 작동 테스트")
    public void articleToResponseBlockedArticle_test() {
        //given
        Article article = articleRepository.findArticleByContent("콘탠트입니다. 제발 됬으면 좋겠습니다.").orElseThrow();

        for (int i = 0; i < 5; i++) {
            Report report = Report.builder().article(article).reason(ReportReason.BAD_LANGUAGE).build();
            em.persist(report);
            article.getReports().add(report);
        }

        Boolean isLiked = true;
        Boolean isBookmarked = true;

        List<TagDto.SimpleTag> tags = new ArrayList<>();

        List<CommentDto.Response> comments = new ArrayList<>();

        Integer likes = article.getArticleLikes().size();
        String report = "이 글은 신고가 누적되 더이상 확인하실 수 없습니다.";
        //when
        ArticleDto.ResponseDetailArticle test = articleMapper
                .articleToResponseBlockedArticle(article, isLiked, isBookmarked,report, tags, comments, likes);

        //then
        assertThat(test.getArticleId()).isEqualTo(article.getId());
        assertThat(test.getCategory()).isEqualTo(article.getCategory().getName());
        assertThat(test.getTitle()).isEqualTo("이 글은 신고가 누적되 더이상 확인하실 수 없습니다.");
        assertThat(test.getContent()).isEqualTo("이 글은 신고가 누적되 더이상 확인하실 수 없습니다.");
        assertThat(test.getClicks()).isEqualTo(article.getClicks());
        assertThat(test.getLikes()).isEqualTo(article.getArticleLikes().size());
        assertThat(test.getIsClosed()).isEqualTo(article.getIsClosed());

        assertThat(test.getIsLiked()).isTrue();
        assertThat(test.getIsBookmarked()).isTrue();

        assertThat(test.getCreatedAt()).isEqualTo(article.getCreatedAt());
        assertThat(test.getLastModifiedAt()).isEqualTo(article.getLastModifiedAt());

        assertThat(test.getTags().size()).isEqualTo(0);

        assertThat(test.getUserInfo().getUserId()).isEqualTo(article.getUser().getId());
        assertThat(test.getUserInfo().getNickname()).isEqualTo(article.getUser().getNickname());
        assertThat(test.getUserInfo().getGrade()).isEqualTo(article.getUser().getGrade());

        assertThat(test.getAvatar().getAvatarId()).isEqualTo(article.getUser().getAvatar().getId());
        assertThat(test.getAvatar().getRemotePath()).isEqualTo(article.getUser().getAvatar().getRemotePath());
        assertThat(test.getAvatar().getFilename()).isEqualTo(article.getUser().getAvatar().getOriginalFilename());

        assertThat(test.getComments().size()).isEqualTo(0);

    }
        @Test
    @DisplayName("makingResponseArticleLikeDto 메퍼 작동 테스트")
    public void makingResponseArticleLikeDto() {
        //given
        Long articleId = 1L;
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().id(1L).build();
        //when
        ArticleDto.ResponseArticleLike responseArticleLike = articleMapper.makingResponseArticleLikeDto(articleId, userInfo.getId(), true, 1);
        //then
        assertThat(responseArticleLike.getArticleId()).isEqualTo(1L);
        assertThat(responseArticleLike.getUserId()).isEqualTo(1L);
        assertThat(responseArticleLike.getIsLiked()).isTrue();
        assertThat(responseArticleLike.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("RequestReportArticleToReport 매퍼 작동 테스트")
    public void RequestReportArticleToReport_test() {
        //given
        ArticleDto.RequestReportArticle dto =
                ArticleDto.RequestReportArticle.builder().reason(ReportReason.BAD_LANGUAGE).content("내용").build();

        //when
        Report report = articleMapper.requestReportArticleToReport(dto);
        //then
        assertThat(report.getContent()).isEqualTo(dto.getContent());
        assertThat(report.getReason()).isEqualTo(dto.getReason());
    }

    @Test
    @DisplayName("reportToResponseArticle 매퍼 작동 테스트")
    public void reportToResponseArticle_test() {
        //given
        Report report = Report.builder().id(1L).build();

        //when
        ArticleDto.ResponseReportArticle result = articleMapper.reportToResponseArticle(report);
        //then
        assertThat(result.getReportId()).isEqualTo(report.getId());
    }
}