package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.domain.PointCalculator;
import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.*;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.mapper.ArticleMapper;
import com.morakmorak.morak_back_end.repository.*;
import com.morakmorak.morak_back_end.repository.article.ArticleLikeRepository;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
import com.morakmorak.morak_back_end.repository.article.ArticleTagRepository;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import com.morakmorak.morak_back_end.service.file_service.FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.morakmorak.morak_back_end.util.ArticleTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {
    @InjectMocks
    ArticleService articleService;
    @Mock
    ArticleMapper articleMapper;
    @Mock
    UserService userService;
    @Mock
    ArticleRepository articleRepository;
    @Mock
    ArticleLikeRepository articleLikeRepository;
    @Mock
    ReportRepository reportRepository;

    @Mock
    PointCalculator pointCalculator;
    @Mock
    CategoryService categoryService;
    @Mock
    TagService tagService;
    @Mock
    FileService fileService;
    @Mock
    ArticleTagRepository articleTagRepository;

    @Test
    @DisplayName("게시글 등록 서비스로직 성공 테스트")
    public void upload_suc() throws Exception {
        //given

        List<ArticleTag> articleTags = new ArrayList<>();
        articleTags.add(ArticleTag.builder().tag(Tag.builder().name(TagName.JAVA).build()).build());
        List<File> files = new ArrayList<>();
        files.add(File.builder().id(1L).build());
        Article article = Article.builder().category(Category.builder().name(CategoryName.QNA).build())
                .thumbnail(1L)
                .content("내용입니다. ")
                .articleTags(articleTags)
                .files(files)
                .title("제목입니다. ")
                .build();

        ArticleDto.ResponseSimpleArticle responseSimpleArticle =
                ArticleDto.ResponseSimpleArticle.builder().articleId(1L).build();

        given(userService.findVerifiedUserById(anyLong())).willReturn(User.builder().id(1L).build());
        given(categoryService.findVerifiedCategoryByName(any())).willReturn(Category.builder().id(1L).name(CategoryName.QNA).build());
        given(fileService.findVerifiedFileById(any())).willReturn(File.builder().build());
        given(tagService.findVerifiedTagByTagName(any())).willReturn(Tag.builder().id(1L).name(TagName.JAVA).build());
        given(articleRepository.save(any())).willReturn(Article.builder().id(1L).build());
        given(articleMapper.articleToResponseSimpleArticle(any())).willReturn(responseSimpleArticle);

        //when
        ArticleDto.ResponseSimpleArticle upload = articleService.upload(
                article, UserDto.UserInfo.builder().id(1L).build());
        //then
        assertThat(upload.getArticleId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("게시글 등록시 존재하지 않는 유저가 게시글을 등록할때")
    public void upload_fail2() throws Exception{
        //given

        given(userService.findVerifiedUserById(any())).willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        //then
        assertThatThrownBy(() -> articleService.upload(ARTICLE, UserDto.UserInfo.builder().id(1L).build())).isInstanceOf(BusinessLogicException.class);

    }
    @Test
    @DisplayName("게시글 등록시 존재하지 않는 카테고리를 적용할 경우")
    public void upload_fail5() throws Exception{
        //given
        Article article = Article.builder().category(Category.builder().name(CategoryName.QNA).build())
                .thumbnail(1L)
                .content("내용입니다. ")
                .title("제목입니다. ")
                .build();
        given(userService.findVerifiedUserById(anyLong())).willReturn(User.builder().id(1L).build());
        given(categoryService.findVerifiedCategoryByName(any())).willThrow(new BusinessLogicException(ErrorCode.CATEGORY_NOT_FOUND));

        //when

        //then
        assertThatThrownBy(() -> articleService.upload(article, UserDto.UserInfo.builder().id(1L).build())).isInstanceOf(BusinessLogicException.class);

    }
    @Test
    @DisplayName("게시글 등록시 존재하지 않는 file을 올릴경우")
    public void upload_fail3() throws Exception{
        //given

        List<ArticleTag> articleTags = new ArrayList<>();
        articleTags.add(ArticleTag.builder().tag(Tag.builder().name(TagName.JAVA).build()).build());
        List<File> files = new ArrayList<>();
        files.add(File.builder().id(1L).build());
        Article article = Article.builder().category(Category.builder().name(CategoryName.QNA).build())
                .thumbnail(1L)
                .content("내용입니다. ")
                .articleTags(articleTags)
                .files(files)
                .title("제목입니다. ")
                .build();

        ArticleDto.ResponseSimpleArticle responseSimpleArticle =
                ArticleDto.ResponseSimpleArticle.builder().articleId(1L).build();

        given(userService.findVerifiedUserById(anyLong())).willReturn(User.builder().id(1L).build());
        given(categoryService.findVerifiedCategoryByName(any())).willReturn(Category.builder().id(1L).name(CategoryName.QNA).build());
        given(fileService.findVerifiedFileById(any())).willReturn(File.builder().build());
        given(tagService.findVerifiedTagByTagName(any())).willThrow(new BusinessLogicException(ErrorCode.TAG_NOT_FOUND));

        //when

        //then
        assertThatThrownBy(() -> articleService.upload(article, UserDto.UserInfo.builder().id(1L).build())).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("게시글 등록시 존재하지 않는 Tag를 올릴경우")
    public void upload_fail4() throws Exception{
        //given

        List<File> files = new ArrayList<>();
        files.add(File.builder().id(1L).build());
        Article article = Article.builder().category(Category.builder().name(CategoryName.QNA).build())
                .thumbnail(1L)
                .content("내용입니다. ")
                .files(files)
                .title("제목입니다. ")
                .build();


        given(userService.findVerifiedUserById(anyLong())).willReturn(User.builder().id(1L).build());
        given(categoryService.findVerifiedCategoryByName(any())).willReturn(Category.builder().id(1L).name(CategoryName.QNA).build());
        given(fileService.findVerifiedFileById(any())).willThrow(new BusinessLogicException(ErrorCode.FILE_NOT_FOUND));
        //when

        //then
        assertThatThrownBy(() -> articleService.upload(article, UserDto.UserInfo.builder().id(1L).build())).isInstanceOf(BusinessLogicException.class);
    }
      @Test
      @DisplayName("게시글을 삭제할때 Article의 상태가 REMOVED로 변하는게 성공할때 ")
      public void deleteArticle_suc() throws Exception{
          //given
          Article article = Article.builder().id(1L).user(User.builder().id(1L).build()).build();
          UserDto.UserInfo userInfo = UserDto.UserInfo.builder().id(1L).build();
          given(articleRepository.findById(article.getId())).willReturn(Optional.of(article));
          //when
          Boolean result = articleService.deleteArticle(article.getId(), userInfo);
          //then
          assertThat(result).isTrue();
       }
    @Test
    @DisplayName("게시글 수정 성공 테스트")
    public void updateArticle_suc(){
    //given
        List<ArticleTag> articleTags = new ArrayList<>();

        List<File> files = new ArrayList<>();

        Article article = Article.builder().category(Category.builder().name(CategoryName.QNA).build())
                .id(1L)
                .thumbnail(1L)
                .content("내용입니다. ")
                .articleTags(articleTags)
                .files(files)
                .title("제목입니다. ")
                .user(User.builder().id(1L).build())
                .build();
        Tag tag = Tag.builder().name(TagName.JAVA).build();
        ArticleTag articleTag = ArticleTag.builder().tag(tag).article(article).build();
        articleTags.add(articleTag);
        tag.getArticleTags().add(articleTag);
        File file = File.builder().id(1L).article(article).build();
        files.add(file);


        List<ArticleTag> changeArticleTags = new ArrayList<>();
        changeArticleTags.add(ArticleTag.builder().tag(Tag.builder().name(TagName.NODE).build()).build());
        List<File> changeFiles = new ArrayList<>();
        changeFiles.add(File.builder().id(2L).build());
        Article changeArticle = Article.builder().category(Category.builder().name(CategoryName.QNA).build())
                .id(1L)
                .thumbnail(1L)
                .content("수정한 내용입니다. ")
                .articleTags(changeArticleTags)
                .files(changeFiles)
                .title("수정한 제목입니다. ")
                .build();
        ArticleDto.ResponseSimpleArticle responseSimpleArticle =
                ArticleDto.ResponseSimpleArticle.builder().articleId(1L).build();

        given(articleRepository.findArticleRelationWithUser(any())).willReturn(Optional.of(article));
        given(fileService.findVerifiedFileById(any())).willReturn(File.builder().id(2L).build());
        given(tagService.findVerifiedTagByTagName(any())).willReturn(Tag.builder().name(TagName.NODE).build());
        given(articleMapper.articleToResponseSimpleArticle(any())).willReturn(responseSimpleArticle);

    //when
        ArticleDto.ResponseSimpleArticle update =
                articleService.update(changeArticle, UserDto.UserInfo.builder().id(1L).build());
        //then
        verify(articleTagRepository, times(1)).delete(any());
        assertThat(update.getArticleId()).isEqualTo(1L);
        assertThat(article.getTitle()).isEqualTo(changeArticle.getTitle());
        assertThat(article.getContent()).isEqualTo(changeArticle.getContent());
        assertThat(article.getFiles().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("게시글 수정시 존재하지 않는 파일을 보낼시 NotFound 404에러를 던진다. ")
    public void updateArticle_fail1(){
        //given
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().id(1L).build();
        List<ArticleTag> articleTags = new ArrayList<>();

        List<File> files = new ArrayList<>();

        Article article = Article.builder().category(Category.builder().name(CategoryName.QNA).build())
                .id(1L)
                .thumbnail(1L)
                .content("내용입니다. ")
                .articleTags(articleTags)
                .files(files)
                .title("제목입니다. ")
                .user(User.builder().id(1L).build())
                .build();
        Tag tag = Tag.builder().name(TagName.JAVA).build();
        ArticleTag articleTag = ArticleTag.builder().tag(tag).article(article).build();
        articleTags.add(articleTag);
        tag.getArticleTags().add(articleTag);
        File file = File.builder().id(1L).article(article).build();
        files.add(file);


        List<ArticleTag> changeArticleTags = new ArrayList<>();
        changeArticleTags.add(ArticleTag.builder().tag(Tag.builder().name(TagName.NODE).build()).build());

        List<File> changeFiles = new ArrayList<>();
        changeFiles.add(File.builder().id(2L).build());

        Article changeArticle = Article.builder().category(Category.builder().name(CategoryName.QNA).build())
                .id(1L)
                .thumbnail(1L)
                .content("수정한 내용입니다. ")
                .articleTags(changeArticleTags)
                .files(changeFiles)
                .title("수정한 제목입니다. ")
                .user(User.builder().id(1L).build())
                .build();
        ArticleDto.ResponseSimpleArticle responseSimpleArticle =
                ArticleDto.ResponseSimpleArticle.builder().articleId(1L).build();

        given(articleRepository.findArticleRelationWithUser(any())).willReturn(Optional.of(article));
        given(fileService.findVerifiedFileById(any())).willThrow(new BusinessLogicException(ErrorCode.FILE_NOT_FOUND));

        //when
        //then
        assertThatThrownBy(() -> articleService.update(changeArticle, userInfo))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("게시글 수정시 작성자가 아닌 유저가 해당글을 수정할때 401 예외를 터트린다.")
    public void updateArticle_fail2(){
        //given
        List<ArticleTag> articleTags = new ArrayList<>();

        List<File> files = new ArrayList<>();

        Article article = Article.builder().category(Category.builder().name(CategoryName.QNA).build())
                .id(1L)
                .thumbnail(1L)
                .content("내용입니다. ")
                .articleTags(articleTags)
                .files(files)
                .title("제목입니다. ")
                .user(User.builder().id(1L).build())
                .build();
        Tag tag = Tag.builder().name(TagName.JAVA).build();
        ArticleTag articleTag = ArticleTag.builder().tag(tag).article(article).build();
        articleTags.add(articleTag);
        tag.getArticleTags().add(articleTag);
        File file = File.builder().id(1L).article(article).build();
        files.add(file);


        List<ArticleTag> changeArticleTags = new ArrayList<>();
        changeArticleTags.add(ArticleTag.builder().tag(Tag.builder().name(TagName.NODE).build()).build());
        List<File> changeFiles = new ArrayList<>();
        changeFiles.add(File.builder().id(2L).build());
        Article changeArticle = Article.builder().category(Category.builder().name(CategoryName.QNA).build())
                .id(1L)
                .thumbnail(1L)
                .content("수정한 내용입니다. ")
                .articleTags(changeArticleTags)
                .files(changeFiles)
                .title("수정한 제목입니다. ")
                .build();
        ArticleDto.ResponseSimpleArticle responseSimpleArticle =
                ArticleDto.ResponseSimpleArticle.builder().articleId(1L).build();

        given(articleRepository.findArticleRelationWithUser(any())).willReturn(Optional.of(article));


        //when
        //then
        assertThatThrownBy(() -> articleService.update(changeArticle, UserDto.UserInfo.builder().id(2L).build()))
                .isInstanceOf(BusinessLogicException.class);
    }

        @Test
        @DisplayName("게시글의 좋아요를 누를때 회원이 좋아요를 처음누르는거면 201코드와 json을 리턴한다.")
        public void pressLikeButton_suc(){
        //given
            Long articleId = 1L;
            UserDto.UserInfo userInfo = UserDto.UserInfo.builder().id(1L).build();
            ArticleLike articleLike = ArticleLike.builder().build();

            Article dbArticle = Article.builder().id(1L).articleLikes(List.of(articleLike)).build();

            ArticleDto.ResponseArticleLike responseArticleLike =
                    ArticleDto.ResponseArticleLike.builder().userId(1L)
                            .articleId(1L).isLiked(true).likeCount(1).build();

            given(articleRepository.findById(anyLong())).willReturn(Optional.of(dbArticle));
            given(userService.findVerifiedUserById(anyLong())).willReturn(User.builder().id(1L).build());
            given(articleLikeRepository.checkUserLiked(1L, 1L)).willReturn(Optional.empty());
            given(articleLikeRepository.checkUserLiked(1L, 1L)).willReturn(Optional.of(ArticleLike.builder().build()));
            given(articleMapper.makingResponseArticleLikeDto(articleId, userInfo.getId(), Boolean.TRUE, 1))
                    .willReturn(responseArticleLike);

        //when
            ArticleDto.ResponseArticleLike result = articleService.pressLikeButton(articleId, userInfo);
            //then
            assertThat(result.getLikeCount()).isEqualTo(1);
            assertThat(result.getIsLiked()).isTrue();
            assertThat(result.getArticleId()).isEqualTo(articleId);
            assertThat(result.getUserId()).isEqualTo(userInfo.getId());
        }

        @Test
        @DisplayName("게시글의 좋아요를 누를때 회원이 좋아요를 두번째 누르는거면 (취소) 201코드와 json을 리턴한다.")
        public void pressLikeButton_suc2(){
        //given
            Long articleId = 1L;
            UserDto.UserInfo userInfo = UserDto.UserInfo.builder().id(1L).build();

            Article dbArticle = Article.builder().id(1L).build();

            ArticleDto.ResponseArticleLike responseArticleLike =
                    ArticleDto.ResponseArticleLike.builder().userId(1L)
                            .articleId(1L).isLiked(false).likeCount(0).build();

            given(articleRepository.findById(anyLong())).willReturn(Optional.of(dbArticle));
            given(userService.findVerifiedUserById(anyLong())).willReturn(User.builder().id(1L).build());
            given(articleLikeRepository.checkUserLiked(1L, 1L)).willReturn(Optional.empty());
            given(articleLikeRepository.checkUserLiked(1L, 1L)).willReturn(Optional.of(ArticleLike.builder().build()));
            given(articleMapper.makingResponseArticleLikeDto(articleId, userInfo.getId(), Boolean.TRUE, 0))
                    .willReturn(responseArticleLike);

            //when
            ArticleDto.ResponseArticleLike result = articleService.pressLikeButton(articleId, userInfo);
            //then
            assertThat(result.getLikeCount()).isEqualTo(0);
            assertThat(result.getIsLiked()).isFalse();
            assertThat(result.getArticleId()).isEqualTo(articleId);
            assertThat(result.getUserId()).isEqualTo(userInfo.getId());
        }

    @Test
    @DisplayName("게시글의 좋아요를 해당 게시글이 존재하지 않을경우 Article Not Found를 던지고 404 예외코드를 던진다.")
    public void pressLikeButton_fail1(){
        //given
        Long articleId = 1L;
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().id(1L).build();

        given(articleRepository.findById(anyLong()))
                .willThrow(new BusinessLogicException(ErrorCode.ARTICLE_NOT_FOUND));

        //when
        //then
        assertThatThrownBy(() -> articleService.pressLikeButton(articleId, userInfo))
                .isInstanceOf(BusinessLogicException.class);

    }
    @Test
    @DisplayName("게시글의 좋아요를 로그인한 회원이 아닐경우 User Not Found를 던지고 404 예외코드를 던진다.")
    public void pressLikeButton_fail2(){
        //given
        Long articleId = 1L;
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().id(1L).build();
        Article dbArticle = Article.builder().id(1L).build();

        given(articleRepository.findById(anyLong())).willReturn(Optional.of(dbArticle));
        given(userService.findVerifiedUserById(anyLong()))
                .willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        //then
        assertThatThrownBy(() -> articleService.pressLikeButton(articleId, userInfo))
                .isInstanceOf(BusinessLogicException.class);

    }

    @Test
    @DisplayName("게시글을 신고할때 모든 요소가 정확하게 다 존재하고 report객채가 잘 생성되었을때")
    public void reportArticle_suc1(){
    //given
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().email("test@naver.com").id(1L).build();

        Report report = Report.builder().reason(ReportReason.BAD_LANGUAGE).content("이유").build();

        Article dbArticle = Article.builder().id(1L).title("제목입니다.제목입니다.제목입니다.제목입니다.").content("본문 입니다.본문 입니다.본문 입니다.")
                .build();

        User dbUser = User.builder().id(1L).email("test@naver.com").nickname("nickname").grade(Grade.BRONZE).build();

        Report dbReport = Report.builder().id(1L).article(dbArticle).user(dbUser)
                .reason(ReportReason.BAD_LANGUAGE).content("이유").build();

        ArticleDto.ResponseReportArticle resultDto = ArticleDto.ResponseReportArticle.builder().reportId(1L).build();

        given(articleRepository.findById(any())).willReturn(Optional.of(dbArticle));
        given(userService.findVerifiedUserById(any())).willReturn(dbUser);
        given(reportRepository.save(any())).willReturn(dbReport);
        given(articleMapper.reportToResponseArticle(any())).willReturn(resultDto);


        //when
        ArticleDto.ResponseReportArticle result = articleService.reportArticle(1L, userInfo, report);
        //then

        assertThat(result.getReportId()).isEqualTo(1L);
    }
    @Test
    @DisplayName("게시글을 신고할때 존재하지 않는 게시글을 신고할때 404예외를 터트린다.")
    public void reportArticle_fail1(){
        //given
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().email("test@naver.com").id(1L).build();

        Report report = Report.builder().reason(ReportReason.BAD_LANGUAGE).content("이유").build();

        given(articleRepository.findById(any())).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> articleService.reportArticle(1L, userInfo, report))
                .isInstanceOf(BusinessLogicException.class);

    }

    @Test
    @DisplayName("게시글을 신고할때 존재하지 않는 유저가 신고를 한다면 404예외를 터트린다.")
    public void reportArticle_fail2(){
        //given
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().email("test@naver.com").id(1L).build();

        Report report = Report.builder().reason(ReportReason.BAD_LANGUAGE).content("이유").build();

        Article dbArticle = Article.builder().id(1L).title("제목입니다.제목입니다.제목입니다.제목입니다.").content("본문 입니다.본문 입니다.본문 입니다.")
                .build();


        given(articleRepository.findById(any())).willReturn(Optional.of(dbArticle));
        given(userService.findVerifiedUserById(any())).willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        //when
        //then
        assertThatThrownBy(() -> articleService.reportArticle(1L, userInfo, report))
                .isInstanceOf(BusinessLogicException.class);
    }
}