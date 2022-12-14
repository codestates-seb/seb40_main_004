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
    @DisplayName("????????? ?????? ??????????????? ?????? ?????????")
    public void upload_suc() throws Exception {
        //given

        List<ArticleTag> articleTags = new ArrayList<>();
        articleTags.add(ArticleTag.builder().tag(Tag.builder().name(TagName.JAVA).build()).build());
        List<File> files = new ArrayList<>();
        files.add(File.builder().id(1L).build());
        Article article = Article.builder().category(Category.builder().name(CategoryName.QNA).build())
                .thumbnail(1L)
                .content("???????????????. ")
                .articleTags(articleTags)
                .files(files)
                .title("???????????????. ")
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
    @DisplayName("????????? ????????? ???????????? ?????? ????????? ???????????? ????????????")
    public void upload_fail2() throws Exception{
        //given

        given(userService.findVerifiedUserById(any())).willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        //then
        assertThatThrownBy(() -> articleService.upload(ARTICLE, UserDto.UserInfo.builder().id(1L).build())).isInstanceOf(BusinessLogicException.class);

    }
    @Test
    @DisplayName("????????? ????????? ???????????? ?????? ??????????????? ????????? ??????")
    public void upload_fail5() throws Exception{
        //given
        Article article = Article.builder().category(Category.builder().name(CategoryName.QNA).build())
                .thumbnail(1L)
                .content("???????????????. ")
                .title("???????????????. ")
                .build();
        given(userService.findVerifiedUserById(anyLong())).willReturn(User.builder().id(1L).build());
        given(categoryService.findVerifiedCategoryByName(any())).willThrow(new BusinessLogicException(ErrorCode.CATEGORY_NOT_FOUND));

        //when

        //then
        assertThatThrownBy(() -> articleService.upload(article, UserDto.UserInfo.builder().id(1L).build())).isInstanceOf(BusinessLogicException.class);

    }
    @Test
    @DisplayName("????????? ????????? ???????????? ?????? file??? ????????????")
    public void upload_fail3() throws Exception{
        //given

        List<ArticleTag> articleTags = new ArrayList<>();
        articleTags.add(ArticleTag.builder().tag(Tag.builder().name(TagName.JAVA).build()).build());
        List<File> files = new ArrayList<>();
        files.add(File.builder().id(1L).build());
        Article article = Article.builder().category(Category.builder().name(CategoryName.QNA).build())
                .thumbnail(1L)
                .content("???????????????. ")
                .articleTags(articleTags)
                .files(files)
                .title("???????????????. ")
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
    @DisplayName("????????? ????????? ???????????? ?????? Tag??? ????????????")
    public void upload_fail4() throws Exception{
        //given

        List<File> files = new ArrayList<>();
        files.add(File.builder().id(1L).build());
        Article article = Article.builder().category(Category.builder().name(CategoryName.QNA).build())
                .thumbnail(1L)
                .content("???????????????. ")
                .files(files)
                .title("???????????????. ")
                .build();


        given(userService.findVerifiedUserById(anyLong())).willReturn(User.builder().id(1L).build());
        given(categoryService.findVerifiedCategoryByName(any())).willReturn(Category.builder().id(1L).name(CategoryName.QNA).build());
        given(fileService.findVerifiedFileById(any())).willThrow(new BusinessLogicException(ErrorCode.FILE_NOT_FOUND));
        //when

        //then
        assertThatThrownBy(() -> articleService.upload(article, UserDto.UserInfo.builder().id(1L).build())).isInstanceOf(BusinessLogicException.class);
    }
      @Test
      @DisplayName("???????????? ???????????? Article??? ????????? REMOVED??? ???????????? ???????????? ")
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
    @DisplayName("????????? ?????? ?????? ?????????")
    public void updateArticle_suc(){
    //given
        List<ArticleTag> articleTags = new ArrayList<>();

        List<File> files = new ArrayList<>();

        Article article = Article.builder().category(Category.builder().name(CategoryName.QNA).build())
                .id(1L)
                .thumbnail(1L)
                .content("???????????????. ")
                .articleTags(articleTags)
                .files(files)
                .title("???????????????. ")
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
                .content("????????? ???????????????. ")
                .articleTags(changeArticleTags)
                .files(changeFiles)
                .title("????????? ???????????????. ")
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
    @DisplayName("????????? ????????? ???????????? ?????? ????????? ????????? NotFound 404????????? ?????????. ")
    public void updateArticle_fail1(){
        //given
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().id(1L).build();
        List<ArticleTag> articleTags = new ArrayList<>();

        List<File> files = new ArrayList<>();

        Article article = Article.builder().category(Category.builder().name(CategoryName.QNA).build())
                .id(1L)
                .thumbnail(1L)
                .content("???????????????. ")
                .articleTags(articleTags)
                .files(files)
                .title("???????????????. ")
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
                .content("????????? ???????????????. ")
                .articleTags(changeArticleTags)
                .files(changeFiles)
                .title("????????? ???????????????. ")
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
    @DisplayName("????????? ????????? ???????????? ?????? ????????? ???????????? ???????????? 401 ????????? ????????????.")
    public void updateArticle_fail2(){
        //given
        List<ArticleTag> articleTags = new ArrayList<>();

        List<File> files = new ArrayList<>();

        Article article = Article.builder().category(Category.builder().name(CategoryName.QNA).build())
                .id(1L)
                .thumbnail(1L)
                .content("???????????????. ")
                .articleTags(articleTags)
                .files(files)
                .title("???????????????. ")
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
                .content("????????? ???????????????. ")
                .articleTags(changeArticleTags)
                .files(changeFiles)
                .title("????????? ???????????????. ")
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
        @DisplayName("???????????? ???????????? ????????? ????????? ???????????? ????????????????????? 201????????? json??? ????????????.")
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
        @DisplayName("???????????? ???????????? ????????? ????????? ???????????? ????????? ??????????????? (??????) 201????????? json??? ????????????.")
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
    @DisplayName("???????????? ???????????? ?????? ???????????? ???????????? ???????????? Article Not Found??? ????????? 404 ??????????????? ?????????.")
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
    @DisplayName("???????????? ???????????? ???????????? ????????? ???????????? User Not Found??? ????????? 404 ??????????????? ?????????.")
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
    @DisplayName("???????????? ???????????? ?????? ????????? ???????????? ??? ???????????? report????????? ??? ??????????????????")
    public void reportArticle_suc1(){
    //given
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().email("test@naver.com").id(1L).build();

        Report report = Report.builder().reason(ReportReason.BAD_LANGUAGE).content("??????").build();

        Article dbArticle = Article.builder().id(1L).title("???????????????.???????????????.???????????????.???????????????.").content("?????? ?????????.?????? ?????????.?????? ?????????.")
                .build();

        User dbUser = User.builder().id(1L).email("test@naver.com").nickname("nickname").grade(Grade.BRONZE).build();

        Report dbReport = Report.builder().id(1L).article(dbArticle).user(dbUser)
                .reason(ReportReason.BAD_LANGUAGE).content("??????").build();

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
    @DisplayName("???????????? ???????????? ???????????? ?????? ???????????? ???????????? 404????????? ????????????.")
    public void reportArticle_fail1(){
        //given
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().email("test@naver.com").id(1L).build();

        Report report = Report.builder().reason(ReportReason.BAD_LANGUAGE).content("??????").build();

        given(articleRepository.findById(any())).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> articleService.reportArticle(1L, userInfo, report))
                .isInstanceOf(BusinessLogicException.class);

    }

    @Test
    @DisplayName("???????????? ???????????? ???????????? ?????? ????????? ????????? ????????? 404????????? ????????????.")
    public void reportArticle_fail2(){
        //given
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().email("test@naver.com").id(1L).build();

        Report report = Report.builder().reason(ReportReason.BAD_LANGUAGE).content("??????").build();

        Article dbArticle = Article.builder().id(1L).title("???????????????.???????????????.???????????????.???????????????.").content("?????? ?????????.?????? ?????????.?????? ?????????.")
                .build();


        given(articleRepository.findById(any())).willReturn(Optional.of(dbArticle));
        given(userService.findVerifiedUserById(any())).willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        //when
        //then
        assertThatThrownBy(() -> articleService.reportArticle(1L, userInfo, report))
                .isInstanceOf(BusinessLogicException.class);
    }
}