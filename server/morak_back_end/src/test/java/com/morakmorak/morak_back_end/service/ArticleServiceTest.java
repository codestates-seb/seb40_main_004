package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.domain.PointCalculator;
import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.entity.enums.ReportReason;
import com.morakmorak.morak_back_end.entity.enums.TagName;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.mapper.ArticleMapper;
import com.morakmorak.morak_back_end.repository.ReportRepository;
import com.morakmorak.morak_back_end.repository.article.ArticleLikeRepository;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
import com.morakmorak.morak_back_end.repository.CategoryRepository;
import com.morakmorak.morak_back_end.repository.FileRepository;
import com.morakmorak.morak_back_end.repository.TagRepository;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import com.morakmorak.morak_back_end.util.ArticleTestConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.morakmorak.morak_back_end.util.ArticleTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {
    @InjectMocks
    ArticleService articleService;

    @Mock
    ArticleMapper articleMapper;

    @Mock
    UserService userService;

    @Mock
    TagRepository tagRepository;
    @Mock
    FileRepository fileRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    ArticleRepository articleRepository;

    @Mock
    ArticleLikeRepository articleLikeRepository;

    @Mock
    ReportRepository reportRepository;

    @Mock
    PointCalculator pointCalculator;

    @Test
    @DisplayName("게시글 등록 서비스로직 성공 테스트")
    public void upload_suc() throws Exception {
        //given
        Article article = Article.builder().build();
        ArticleDto.ResponseSimpleArticle responseSimpleArticle =
                ArticleDto.ResponseSimpleArticle.builder().articleId(1L).build();

        given(userService.findVerifiedUserById(anyLong())).willReturn(User.builder().id(1L).build());
        given(fileRepository.findById(any())).willReturn(Optional.of(File.builder().build()));
        given(tagRepository.findTagByName(any())).willReturn(Optional.of(Tag.builder().build()));
        given(categoryRepository.findCategoryByName(any())).willReturn(Optional.of(Category.builder().build()));
        given(articleRepository.save(article)).willReturn(article);
        given(articleMapper.articleToResponseSimpleArticle(article.getId())).willReturn(responseSimpleArticle);

        //when
        ArticleDto.ResponseSimpleArticle upload = articleService.upload(
                article, UserDto.UserInfo.builder().id(1L).build(),
                REQUEST_TAG_WITH_ID_AND_NAMES,
                REQUEST_FILE_WITH_IDS,
                REQUEST_STRING_CATEGORY);
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
        assertThatThrownBy(() -> articleService.upload(ARTICLE, UserDto.UserInfo.builder().id(1L).build(),
                REQUEST_TAG_WITH_ID_AND_NAMES,
                REQUEST_FILE_WITH_IDS,
                REQUEST_STRING_CATEGORY)).isInstanceOf(BusinessLogicException.class);

    }
    @Test
    @DisplayName("게시글 등록시 존재하지 않는 file을 올릴경우")
    public void upload_fail3() throws Exception{
        //given
        given(userService.findVerifiedUserById(any())).willReturn(User.builder().build());
        given(fileRepository.findById(any())).willReturn(Optional.empty());
        //when

        //then
        assertThatThrownBy(() -> articleService.upload(ARTICLE, UserDto.UserInfo.builder().id(1L).build(),
                REQUEST_TAG_WITH_ID_AND_NAMES,
                REQUEST_FILE_WITH_IDS,
                REQUEST_STRING_CATEGORY)).isInstanceOf(BusinessLogicException.class);
    }

     @Test
     @DisplayName("게시글 등록시 존재하지 않는 카테고리를 적용할 경우")
     public void upload_fail5() throws Exception{
         //given
         Article ARTICLE =
                 Article.builder().title("안녕하세요 타이틀입니다. 잘 부탁드립니다. 타이틀은 신경씁니다.")
                         .content("콘텐트입니다. 잘부탁드립니다.")
                         .thumbnail(1L)
                         .build();


         given(userService.findVerifiedUserById(any())).willReturn(User.builder().build());
         given(fileRepository.findById(any())).willReturn(Optional.of(File.builder().build()));
         given(tagRepository.findTagByName(any()))
                 .willReturn(Optional.of(Tag.builder().id(1L).build()));
         given(categoryRepository.findCategoryByName(any())).willReturn(Optional.empty());
         //when

         //then
         assertThatThrownBy(() -> articleService.upload(ARTICLE, UserDto.UserInfo.builder().id(1L).build(),
                 REQUEST_TAG_WITH_ID_AND_NAMES,
                 REQUEST_FILE_WITH_IDS,
                 REQUEST_STRING_CATEGORY)).isInstanceOf(BusinessLogicException.class);

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
       @DisplayName("파일과 게시글의 연관관계를 맺는 메서드 통과 테스트")
       public void fusionFileDtoWithArticle_suc() throws Exception{
           //given
           List<FileDto.RequestFileWithId> fileDto = List.of(FileDto.RequestFileWithId.builder().fileId(1L).build());
           Article article = Article.builder().build();

           given(fileRepository.findById(anyLong())).willReturn(Optional.of(File.builder().build()));
           //when
           articleService.findDbFilesAndInjectWithArticle(article, fileDto);
           //then
           assertThat(article.getFiles().size()).isEqualTo(1);
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