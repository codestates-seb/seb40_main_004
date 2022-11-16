package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.entity.enums.TagName;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.mapper.ArticleMapper;
import com.morakmorak.morak_back_end.repository.ArticleRepository;
import com.morakmorak.morak_back_end.repository.CategoryRepository;
import com.morakmorak.morak_back_end.repository.FileRepository;
import com.morakmorak.morak_back_end.repository.TagRepository;
import com.morakmorak.morak_back_end.util.ArticleTestConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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


    @Test
    @DisplayName("게시글 등록 서비스로직 성공 테스트")
    public void upload_suc() throws Exception {
        //given
        Article article = Article.builder().build();
        ArticleDto.ResponseSimpleArticle responseSimpleArticle =
                ArticleDto.ResponseSimpleArticle.builder().articleId(1L).build();

        given(userService.findVerifiedUserById(anyLong())).willReturn(User.builder().id(1L).build());
        given(fileRepository.findById(any())).willReturn(Optional.of(File.builder().build()));
        given(tagRepository.findById(any())).willReturn(Optional.of(Tag.builder().build()));
        given(categoryRepository.findCategoryByName(any())).willReturn(Optional.of(Category.builder().build()));
        given(articleRepository.save(article)).willReturn(article);
        given(articleMapper.articleToResponseSimpleArticle(article.getId())).willReturn(responseSimpleArticle);

        //when
        ArticleDto.ResponseSimpleArticle upload = articleService.upload(
                article, UserDto.UserInfo.builder().id(1L).build(),
                ArticleTestConstants.REQUEST_TAG_WITH_ID_AND_NAMES,
                ArticleTestConstants.REQUEST_FILE_WITH_IDS,
                ArticleTestConstants.REQUEST_STRING_CATEGORY);
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
        assertThatThrownBy(() -> articleService.upload(ArticleTestConstants.ARTICLE, UserDto.UserInfo.builder().id(1L).build(),
                ArticleTestConstants.REQUEST_TAG_WITH_ID_AND_NAMES,
                ArticleTestConstants.REQUEST_FILE_WITH_IDS,
                ArticleTestConstants.REQUEST_STRING_CATEGORY)).isInstanceOf(BusinessLogicException.class);

    }
    @Test
    @DisplayName("게시글 등록시 존재하지 않는 file을 올릴경우")
    public void upload_fail3() throws Exception{
        //given
        given(userService.findVerifiedUserById(any())).willReturn(User.builder().build());
        given(fileRepository.findById(any())).willReturn(Optional.empty());
        //when

        //then
        assertThatThrownBy(() -> articleService.upload(ArticleTestConstants.ARTICLE, UserDto.UserInfo.builder().id(1L).build(),
                ArticleTestConstants.REQUEST_TAG_WITH_ID_AND_NAMES,
                ArticleTestConstants.REQUEST_FILE_WITH_IDS,
                ArticleTestConstants.REQUEST_STRING_CATEGORY)).isInstanceOf(BusinessLogicException.class);
    }
    @Test
    @DisplayName("게시글 등록시 존재하지 않는 태그를 적용할 경우")
    public void upload_fail4() throws Exception{
        Article ARTICLE =
                Article.builder().title("안녕하세요 타이틀입니다. 잘 부탁드립니다. 타이틀은 신경씁니다.")
                        .content("콘텐트입니다. 잘부탁드립니다.")
                        .thumbnail(1L)
                        .build();
        //given
        TagDto.SimpleTag java = TagDto.SimpleTag.builder().tagId(1L).name("java").build();
        given(userService.findVerifiedUserById(any())).willReturn(User.builder().build());
        given(fileRepository.findById(anyLong())).willReturn(Optional.of(File.builder().article(Article.builder().build()).build()));
        given(tagRepository.findById(any())).willReturn(Optional.empty());
        //when          //무슨 값이 들어가야하는건지 모르겠다....


        //then
        assertThatThrownBy(() -> articleService.upload(ARTICLE, UserDto.UserInfo.builder().id(1L).build(),
                ArticleTestConstants.REQUEST_TAG_WITH_ID_AND_NAMES,
                ArticleTestConstants.REQUEST_FILE_WITH_IDS,
                ArticleTestConstants.REQUEST_STRING_CATEGORY)).isInstanceOf(BusinessLogicException.class);
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
         given(tagRepository.findById(any()))
                 .willReturn(Optional.of(Tag.builder().id(1L).build()));
         given(categoryRepository.findCategoryByName(anyString())).willReturn(Optional.empty());
         //when

         //then
         assertThatThrownBy(() -> articleService.upload(ARTICLE, UserDto.UserInfo.builder().id(1L).build(),
                 ArticleTestConstants.REQUEST_TAG_WITH_ID_AND_NAMES,
                 ArticleTestConstants.REQUEST_FILE_WITH_IDS,
                 ArticleTestConstants.REQUEST_STRING_CATEGORY)).isInstanceOf(BusinessLogicException.class);

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
           Boolean aBoolean = articleService.fusionFileDtoWithArticle(article, fileDto);
           //then
           assertThat(aBoolean).isTrue();
        }

//        @Test
//        @DisplayName("게시글을 레포지토리에서 가져오는데 페이징으로 가져온다음 리턴값으로 정확한 값을 리턴하는지 테스트")
//        public void searchArticleAsPaging_suc(){
//
//            Tag JAVA = Tag.builder().name(TagName.JAVA).build();
//            Category info = Category.builder().name("info").build();
//
//
//
//            Avatar avatar = Avatar.builder().remotePath("remotePath").originalFileName("originalFileName").build();
//            User user = User.builder().avatar(avatar).nickname("testNickname").grade(Grade.BRONZE).build();
//
//            List<Answer> answers = new ArrayList<>();
//            List<Comment> comments = new ArrayList<>();
//
//            for (int i = 0; i < 3; i++) {
//                Answer answer = Answer.builder().build();
//                answers.add(answer);
//                Comment comment = Comment.builder().build();
//                comments.add(comment);
//
//            }
//
//            ArticleTag articleTagJava = ArticleTag.builder().tag(JAVA).build();
//            Article article
//                    = Article.builder().title("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~")
//                    .content("콘탠트입니다. 제발 됬으면 좋겠습니다.")
//                    .articleTags(List.of(articleTagJava))
//                    .category(info)
//                    .vote(Vote.builder().count(10).build())
//                    .Comments(comments)
//                    .answers(answers)
//                    .user(user)
//                    .build();
//            info.getArticleList().add(article);
//            articleTagJava.injectMappingForArticleAndTagForTesting(article);
//
//            List<Article> articles = new ArrayList<>();
//            for (int i = 0; i < 3; i++) {
//                articles.add(article);
//            }
//
//            //given
//            PageRequest pageRequest = PageRequest.of(0, 3);
//            String category = "info";
//            String keyword = "안녕하세요";
//            String target = "content";
//            String sort = null;
//
//            List<TagDto.SimpleTag> tags = new ArrayList<>();
//
//            for (int i = 1; i < 3; i++) {
//            TagDto.SimpleTag tag = TagDto.SimpleTag.builder()
//                    .tagId(Long.parseLong(String.valueOf(i))).name("JAVA").build();
//                tags.add(tag);
//            }
//
//            UserDto.ResponseSimpleUserDto userInfo =
//                    UserDto.ResponseSimpleUserDto.builder().userId(1L)
//                            .nickname("nickname").grade(Grade.BRONZE).build();
//
//            AvatarDto.ResponseAvatarInfo avatarInfo =
//                    AvatarDto.ResponseAvatarInfo.builder().avatarId(1L)
//                            .remotePath("localPath").fileName("fileName").build();
//
//
////
////            List<ArticleDto.ResponseListTypeArticle> result = new ArrayList<>();
////            for (int i = 1; i < 11; i++) {
//                ArticleDto.ResponseListTypeArticle responseListTypeArticleBuilder =
//                        ArticleDto.ResponseListTypeArticle.builder()
//                                .articleId(Long.parseLong(String.valueOf(1L)))
//                                .category(category)
//                                .title("타이틀입니다.타이틀입니다.타이틀입니다.타이틀입니다.")
//                                .clicks(10)
//                                .likes(10)
//                                .answerCount(20)
//                                .commentCount(15)
//                                .isClosed(false)
//                                .tags(tags)
//                                .createdAt(LocalDateTime.now())
//                                .lastModifiedAt(LocalDateTime.now())
//                                .userInfo(userInfo)
//                                .avatarInfo(avatarInfo)
//                                .build();
//
////                result.add(responseListTypeArticleBuilder);
//
////            }
//            Page<Article> search = new PageImpl<>(articles);
//
//            given(articleRepository.search(category, keyword, target, null, pageRequest)).willReturn(search);
//
//            given(articleMapper.articleToResponseSearchResultArticle(any(), any(), any(), any()))
//                    .willReturn(responseListTypeArticleBuilder);
//            given(new ResponseMultiplePaging<>(any(), search)).willReturn(ResponseMultiplePaging.builder().build());
//
//        //when
//            ResponseMultiplePaging<ArticleDto.ResponseListTypeArticle> responseListTypeArticleResponseMultiplePaging =
//                    articleService.searchArticleAsPaging(category,keyword
//                    ,target,null, 1, 10);
//            //then
//
//        }

}