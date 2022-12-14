package com.morakmorak.morak_back_end.Integration.article.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.dto.AvatarDto;
import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.*;
import com.morakmorak.morak_back_end.repository.CategoryRepository;
import com.morakmorak.morak_back_end.repository.FileRepository;
import com.morakmorak.morak_back_end.repository.TagRepository;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
import com.morakmorak.morak_back_end.repository.article.ArticleTagRepository;
import com.morakmorak.morak_back_end.repository.redis.RedisRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.ArticleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ROLE_USER_LIST;
import static com.morakmorak.morak_back_end.util.TestConstants.EMAIL1;
import static com.morakmorak.morak_back_end.util.TestConstants.NICKNAME1;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
@Transactional
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
public class ArticleGetTest {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @PersistenceContext
    EntityManager em;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RedisRepository<String> mailAuthRedisRepository;

    @Autowired
    ArticleService articleService;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    ArticleTagRepository articleTagRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArticleRepository articleRepository;


    @Test
    @DisplayName("???????????? ??????????????? ??????????????? ?????? ????????? 201????????? Ok??? ????????????.")
    public void searchArticleTest_title() throws Exception {
        Tag JAVA = Tag.builder().name(TagName.JAVA).build();
        em.persist(JAVA);

        Category info = Category.builder().name(CategoryName.INFO).build();
        em.persist(info);

        Avatar avatar = Avatar.builder().remotePath("remotePath")
                .originalFilename("fileName")
                .build();
        em.persist(avatar);

        User user = User.builder().nickname("nickname").grade(Grade.BRONZE).avatar(avatar).build();

        List<ArticleLike> articleLikes = new ArrayList<>();

        ArticleTag articleTagJava = ArticleTag.builder().tag(JAVA).build();

        Article article = Article.builder().title("????????? ??????????????????. ?????????????????????. ?????? ??????!!!~~~~~~~~")
                .content("??????????????????. ?????? ????????? ???????????????.")
                .articleTags(List.of(articleTagJava))
                .category(info)
                .articleLikes(articleLikes)
                .user(user)
                .build();
        info.getArticleList().add(article);
        articleTagJava.injectTo(article);
        em.persist(article);

        user.getArticles().add(article);
        em.persist(user);

        //when
        ResultActions perform = mockMvc.perform(
                get("/articles")
                        .header("User-Agent", "Mozilla 5.0")
                        .param("category", "INFO")
                        .param("keyword", "????????? ??????????????????. ?????????????????????. ?????? ??????!!!~~~~~~~~")
                        .param("target", "title")
                        .param("sort", "desc")
                        .param("page", "1")
                        .param("size", "1")
        );

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0:1].articleId").value(article.getId().intValue()))
                .andExpect(jsonPath("$.data[0:1].category").value("INFO"))
                .andExpect(jsonPath("$.data[0:1].title").value("????????? ??????????????????. ?????????????????????. ?????? ??????!!!~~~~~~~~"))
                .andExpect(jsonPath("$.data[0:1].clicks").value(0))
                .andExpect(jsonPath("$.data[0:1].likes").value(0))
                .andExpect(jsonPath("$.data[0:1].isClosed").value(false))
                .andExpect(jsonPath("$.data[0:1].commentCount").value(0))
                .andExpect(jsonPath("$.data[0:1].answerCount").value(0))
                .andExpect(jsonPath("$.data[0:1].createdAt").exists())
                .andExpect(jsonPath("$.data[0:1].lastModifiedAt").exists())
                .andExpect(jsonPath("$.data[0:1].tags[0:1].tagId").value(JAVA.getId().intValue()))
                .andExpect(jsonPath("$.data[0:1].tags[0:1].name").value("JAVA"))
                .andExpect(jsonPath("$.data[0:1].userInfo.userId").value(user.getId().intValue()))
                .andExpect(jsonPath("$.data[0:1].userInfo.nickname").value("nickname"))
                .andExpect(jsonPath("$.data[0:1].userInfo.grade").value("BRONZE"))
                .andExpect(jsonPath("$.data[0:1].avatar.avatarId").value(avatar.getId().intValue()))
                .andExpect(jsonPath("$.data[0:1].avatar.filename").value("fileName"))
                .andExpect(jsonPath("$.data[0:1].avatar.remotePath").value("remotePath"))
                .andExpect(jsonPath("$.pageInfo.page").value(1))
                .andExpect(jsonPath("$.pageInfo.size").value(1))
                .andExpect(jsonPath("$.pageInfo.totalElements").value(1))
                .andExpect(jsonPath("$.pageInfo.totalPages").value(1))
                .andExpect(jsonPath("$.pageInfo.sort.sorted").value(false));
    }

    @Test
    @DisplayName("???????????? ??????????????? ??????????????? ?????? ????????? ???????????? data ???????????? ???????????? pageInfo Dto???  201????????? Ok??? ????????????.")
    public void searchArticleTest_title2() throws Exception {
        Tag JAVA = Tag.builder().name(TagName.JAVA).build();
        em.persist(JAVA);

        Category info = Category.builder().name(CategoryName.INFO).build();
        em.persist(info);

        Avatar avatar = Avatar.builder().remotePath("remotePath")
                .originalFilename("fileName")
                .build();
        em.persist(avatar);

        User user = User.builder().nickname("nickname").grade(Grade.BRONZE).avatar(avatar).build();

        List<ArticleLike> articleLikes = new ArrayList<>();

        ArticleTag articleTagJava = ArticleTag.builder().tag(JAVA).build();

        Article article = Article.builder().title("????????? ??????????????????. ?????????????????????. ?????? ??????!!!~~~~~~~~")
                .content("??????????????????. ?????? ????????? ???????????????.")
                .articleTags(List.of(articleTagJava))
                .category(info)
                .articleLikes(articleLikes)
                .user(user)
                .build();
        info.getArticleList().add(article);
        articleTagJava.injectTo(article);
        em.persist(article);

        user.getArticles().add(article);
        em.persist(user);

        //when
        ResultActions perform = mockMvc.perform(
                get("/articles")
                        .header("User-Agent", "Mozilla 5.0")
                        .param("category", "INFO")
                        .param("keyword", "???????????? ??????")
                        .param("target", "title")
                        .param("sort", "desc")
                        .param("page", "1")
                        .param("size", "1")
        );

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.pageInfo.page").value(1))
                .andExpect(jsonPath("$.pageInfo.size").value(1))
                .andExpect(jsonPath("$.pageInfo.totalElements").value(0))
                .andExpect(jsonPath("$.pageInfo.totalPages").value(0))
                .andExpect(jsonPath("$.pageInfo.sort.sorted").value(false));
    }


    @Test
    @DisplayName("???????????? ???????????? ??????????????? ?????? ????????? 201????????? Ok??? ????????????.")
    public void searchArticleTest_tag() throws Exception {
        Tag JAVA = Tag.builder().name(TagName.JAVA).build();
        em.persist(JAVA);

        Category info = Category.builder().name(CategoryName.INFO).build();
        em.persist(info);

        Avatar avatar = Avatar.builder().remotePath("remotePath")
                .originalFilename("fileName")
                .build();
        em.persist(avatar);

        User user = User.builder().nickname("nickname").grade(Grade.BRONZE).avatar(avatar).build();

        List<ArticleLike> articleLikes = new ArrayList<>();

        ArticleTag articleTagJava = ArticleTag.builder().tag(JAVA).build();

        Article article = Article.builder().title("????????? ??????????????????. ?????????????????????. ?????? ??????!!!~~~~~~~~")
                .content("??????????????????. ?????? ????????? ???????????????.")
                .articleTags(List.of(articleTagJava))
                .category(info)
                .articleLikes(articleLikes)
                .user(user)
                .build();
        info.getArticleList().add(article);
        articleTagJava.injectTo(article);
        em.persist(article);

        user.getArticles().add(article);
        em.persist(user);

        //when
        ResultActions perform = mockMvc.perform(
                get("/articles")
                        .header("User-Agent", "Mozilla 5.0")
                        .param("category", "INFO")
                        .param("keyword", "JAVA")
                        .param("target", "tag")
                        .param("sort", "desc")
                        .param("page", "1")
                        .param("size", "1")
        );

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0:1].articleId").value(article.getId().intValue()))
                .andExpect(jsonPath("$.data[0:1].category").value("INFO"))
                .andExpect(jsonPath("$.data[0:1].title").value("????????? ??????????????????. ?????????????????????. ?????? ??????!!!~~~~~~~~"))
                .andExpect(jsonPath("$.data[0:1].clicks").value(0))
                .andExpect(jsonPath("$.data[0:1].likes").value(0))
                .andExpect(jsonPath("$.data[0:1].isClosed").value(false))
                .andExpect(jsonPath("$.data[0:1].commentCount").value(0))
                .andExpect(jsonPath("$.data[0:1].answerCount").value(0))
                .andExpect(jsonPath("$.data[0:1].createdAt").exists())
                .andExpect(jsonPath("$.data[0:1].lastModifiedAt").exists())
                .andExpect(jsonPath("$.data[0:1].tags[0:1].tagId").value(JAVA.getId().intValue()))
                .andExpect(jsonPath("$.data[0:1].tags[0:1].name").value("JAVA"))
                .andExpect(jsonPath("$.data[0:1].userInfo.userId").value(user.getId().intValue()))
                .andExpect(jsonPath("$.data[0:1].userInfo.nickname").value("nickname"))
                .andExpect(jsonPath("$.data[0:1].userInfo.grade").value("BRONZE"))
                .andExpect(jsonPath("$.data[0:1].avatar.avatarId").value(avatar.getId().intValue()))
                .andExpect(jsonPath("$.data[0:1].avatar.filename").value("fileName"))
                .andExpect(jsonPath("$.data[0:1].avatar.remotePath").value("remotePath"))
                .andExpect(jsonPath("$.pageInfo.page").value(1))
                .andExpect(jsonPath("$.pageInfo.size").value(1))
                .andExpect(jsonPath("$.pageInfo.totalElements").value(1))
                .andExpect(jsonPath("$.pageInfo.totalPages").value(1))
                .andExpect(jsonPath("$.pageInfo.sort.sorted").value(false));
    }

    @Test
    @DisplayName("????????? ???????????? ??? ??????????????? ???????????? ?????? 403 Forbidden??? ?????????.")
    public void findDetailArticle_failed() throws Exception {
        em.flush();
        em.clear();
        //given
        Article dbArticle = Article.builder()
                .articleStatus(ArticleStatus.REMOVED)
                .build();
        em.persist(dbArticle);

        Avatar avatar = Avatar.builder().originalFilename("filename").remotePath("remotePath").build();
        User dbUser = User.builder().email("test@naver.com").nickname("nickname").grade(Grade.BRONZE).avatar(avatar).build();

        em.persist(avatar);
        em.persist(dbUser);

        String accessToken = jwtTokenUtil.createAccessToken(dbUser.getEmail(), dbUser.getId(), ROLE_USER_LIST, NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(
                get("/articles/{article-id}" , dbArticle.getId())
                        .header("User-Agent", "Mozilla 5.0")
                        .header(JWT_HEADER, accessToken)
        );

        //then
        perform.andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("????????? ???????????? ???????????? ????????? jwt????????? ????????? isBookmarked??? isLiked??? ???????????? true or false??? ?????????.")
    public void findDetailArticle_suc() throws Exception {
        em.flush();
        em.clear();
        //given

        Tag C = Tag.builder().name(TagName.C).build();

        Category category = Category.builder().name(CategoryName.INFO).build();
        em.persist(category);

        Avatar avatar = Avatar.builder().originalFilename("filename").remotePath("remotePath").build();
        User user = User.builder().email("test@naver.com").nickname("nickname").grade(Grade.BRONZE).avatar(avatar).build();
        Article article = Article.builder()
                .title("??????????????? ??????????????????. ?????????????????????. ?????? ???????????? ???????????????.")
                .content("??????????????? ??????????????????. ?????? ???????????? ???????????????.")
                .category(category)
                .clicks(10)
                .user(user)
                .build();
        ArticleTag articleTag = ArticleTag.builder().article(article).tag(C).build();
        article.getArticleTags().add(articleTag);
        category.getArticleList().add(article);
        C.getArticleTags().add(articleTag);
        em.persist(articleTag);
        em.persist(C);

        em.persist(avatar);
        em.persist(user);

        ArticleLike articleLike = ArticleLike.builder().user(user).article(article).build();
        user.getArticleLikes().add(articleLike);
        article.getArticleLikes().add(articleLike);
        em.persist(articleLike);

        Bookmark bookmark = Bookmark.builder().user(user).article(article).build();
        article.getBookmarks().add(bookmark);
        user.getBookmarks().add(bookmark);
        em.persist(bookmark);

        Comment comment = Comment.builder().content("????????? ???????????????. ?????????????????????.????????????????????????").user(user).article(article).build();
        article.getComments().add(comment);
        user.getComments().add(comment);
        em.persist(comment);

        em.persist(article);
        em.persist(user);

        User dbUser = userRepository.findUserByEmail(user.getEmail()).orElseThrow(() -> new RuntimeException("????????????"));
        String accessToken = jwtTokenUtil.createAccessToken(user.getEmail(), dbUser.getId(), ROLE_USER_LIST, NICKNAME1);

        Article dbArticle = articleRepository.findArticleByContent("??????????????? ??????????????????. ?????? ???????????? ???????????????.")
                .orElseThrow(() -> new RuntimeException("????????? ??????"));


        //when
        ResultActions perform = mockMvc.perform(
                get("/articles/" + article.getId())
                        .header("User-Agent", "Mozilla 5.0")
                        .header(JWT_HEADER, accessToken)
        );

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(article.getId()))
                .andExpect(jsonPath("$.category").value("INFO"))
                .andExpect(jsonPath("$.title").value("??????????????? ??????????????????. ?????????????????????. ?????? ???????????? ???????????????."))
                .andExpect(jsonPath("$.content").value("??????????????? ??????????????????. ?????? ???????????? ???????????????."))
                .andExpect(jsonPath("$.clicks").value(article.getClicks()))
                .andExpect(jsonPath("$.likes").value(1))
                .andExpect(jsonPath("$.isClosed").value(false))
                .andExpect(jsonPath("$.isLiked").value(true))
                .andExpect(jsonPath("$.isBookmarked").value(true))
                .andExpect(jsonPath("$.tags[0:1].tagId").value(C.getId().intValue()))
                .andExpect(jsonPath("$.tags[0:1].name").value("C"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty())
                .andExpect(jsonPath("$.expiredDate").isEmpty())
                .andExpect(jsonPath("$.userInfo.userId").value(dbUser.getId()))
                .andExpect(jsonPath("$.userInfo.nickname").value("nickname"))
                .andExpect(jsonPath("$.userInfo.grade").value("BRONZE"))
                .andExpect(jsonPath("$.avatar.avatarId").value(avatar.getId()))
                .andExpect(jsonPath("$.avatar.filename").value("filename"))
                .andExpect(jsonPath("$.avatar.remotePath").value("remotePath"))
                .andExpect(jsonPath("$.comments[0:1].articleId").value(dbArticle.getId().intValue()))
                .andExpect(jsonPath("$.comments[0:1].content").value("????????? ???????????????. ?????????????????????.????????????????????????"))
                .andExpect(jsonPath("$.comments[0:1].commentId").value(comment.getId().intValue()))
                .andExpect(jsonPath("$.comments[0:1].userInfo.userId").value(dbUser.getId().intValue()))
                .andExpect(jsonPath("$.comments[0:1].userInfo.nickname").value("nickname"))
                .andExpect(jsonPath("$.comments[0:1].userInfo.grade").value("BRONZE"))
                .andExpect(jsonPath("$.comments[0:1].avatar.avatarId").value(avatar.getId().intValue()))
                .andExpect(jsonPath("$.comments[0:1].avatar.filename").value("filename"))
                .andExpect(jsonPath("$.comments[0:1].avatar.remotePath").value("remotePath"))
                .andExpect(jsonPath("$.comments[0:1].createdAt").exists())
                .andExpect(jsonPath("$.comments[0:1].lastModifiedAt").exists());


    }

    @Test
    @DisplayName("????????? ???????????? ???????????? ?????? ???????????? 5?????? ????????? ???????????????.")
    public void findDetailBlockArticle_suc() throws Exception {
        em.flush();
        em.clear();
        //given

        Tag C = Tag.builder().name(TagName.C).build();

        Category category = Category.builder().name(CategoryName.INFO).build();
        em.persist(category);

        Avatar avatar = Avatar.builder().originalFilename("filename").remotePath("remotePath").build();
        User user = User.builder().email("test@naver.com").nickname("nickname").grade(Grade.BRONZE).avatar(avatar).build();
        Article article = Article.builder()
                .title("??????????????? ??????????????????. ?????????????????????. ?????? ???????????? ???????????????.")
                .content("??????????????? ??????????????????. ?????? ???????????? ???????????????.")
                .user(user)
                .category(category)
                .clicks(10)
                .build();
        ArticleTag articleTag = ArticleTag.builder().article(article).tag(C).build();
        article.getArticleTags().add(articleTag);
        C.getArticleTags().add(articleTag);
        category.getArticleList().add(article);
        em.persist(articleTag);
        em.persist(C);

        for (int i = 0; i < 5; i++) {
            Report report = Report.builder().reason(ReportReason.BAD_LANGUAGE).build();
            article.getReports().add(report);
            em.persist(report);

        }

        em.persist(avatar);
        em.persist(user);

        ArticleLike articleLike = ArticleLike.builder().user(user).article(article).build();
        user.getArticleLikes().add(articleLike);
        article.getArticleLikes().add(articleLike);
        em.persist(articleLike);

        Bookmark bookmark = Bookmark.builder().user(user).article(article).build();
        article.getBookmarks().add(bookmark);
        user.getBookmarks().add(bookmark);
        em.persist(bookmark);

        Comment comment = Comment.builder().content("????????? ???????????????. ?????????????????????.????????????????????????").user(user).article(article).build();
        article.getComments().add(comment);
        user.getComments().add(comment);
        em.persist(comment);

        em.persist(article);
        em.persist(user);

        User dbUser = userRepository.findUserByEmail(user.getEmail()).orElseThrow(() -> new RuntimeException("????????????"));
        String accessToken = jwtTokenUtil.createAccessToken(user.getEmail(), dbUser.getId(), ROLE_USER_LIST, NICKNAME1);

        Article dbArticle = articleRepository.findArticleByContent("??????????????? ??????????????????. ?????? ???????????? ???????????????.")
                .orElseThrow(() -> new RuntimeException("????????? ??????"));


        //when
        ResultActions perform = mockMvc.perform(
                get("/articles/" + article.getId())
                        .header("User-Agent", "Mozilla 5.0")
                        .header(JWT_HEADER, accessToken)
        );

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(article.getId()))
                .andExpect(jsonPath("$.category").value("INFO"))
                .andExpect(jsonPath("$.title").value("??? ?????? ????????? ???????????? ????????? ???????????? ??? ????????????."))
                .andExpect(jsonPath("$.content").value("??? ?????? ????????? ???????????? ????????? ???????????? ??? ????????????."))
                .andExpect(jsonPath("$.clicks").value(article.getClicks()))
                .andExpect(jsonPath("$.likes").value(1))
                .andExpect(jsonPath("$.isClosed").value(false))
                .andExpect(jsonPath("$.isLiked").value(true))
                .andExpect(jsonPath("$.isBookmarked").value(true))
                .andExpect(jsonPath("$.tags").isEmpty())
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty())
                .andExpect(jsonPath("$.expiredDate").isEmpty())
                .andExpect(jsonPath("$.userInfo.userId").value(dbUser.getId()))
                .andExpect(jsonPath("$.userInfo.nickname").value("nickname"))
                .andExpect(jsonPath("$.userInfo.grade").value("BRONZE"))
                .andExpect(jsonPath("$.avatar.avatarId").value(avatar.getId()))
                .andExpect(jsonPath("$.avatar.filename").value("filename"))
                .andExpect(jsonPath("$.avatar.remotePath").value("remotePath"))
                .andExpect(jsonPath("$.comments").isEmpty());
    }


    @Test
    @DisplayName("????????? ??????????????? jwt ????????? ????????? ????????? isBookmarked??? isLiked??? flase??? ?????????.")
    public void findDetailArticle_suc2() throws Exception {
        em.flush();
        em.clear();
        //given
        Tag JAVA = Tag.builder().name(TagName.JAVA).build();
        em.persist(JAVA);

        Category info = Category.builder().name(CategoryName.INFO).build();
        em.persist(info);

        Avatar avatar = Avatar.builder().remotePath("remotePath")
                .originalFilename("fileName")
                .build();
        em.persist(avatar);

        User user = User.builder().email(EMAIL1).nickname("nickname").grade(Grade.BRONZE).avatar(avatar).build();


        ArticleTag articleTagJava = ArticleTag.builder().tag(JAVA).build();
        JAVA.getArticleTags().add(articleTagJava);


        Article article = Article.builder().title("????????? ??????????????????. ?????????????????????. ?????? ??????!!!~~~~~~~~")
                .content("??????????????????. ?????? ????????? ???????????????.")
                .articleTags(List.of(articleTagJava))
                .category(info)
                .user(user)
                .build();
        info.getArticleList().add(article);
        articleTagJava.injectTo(article);
        em.persist(articleTagJava);
        em.persist(article);

        user.getArticles().add(article);
        em.persist(user);

        Bookmark bookmark = Bookmark.builder().article(article).user(user).build();
        article.getBookmarks().add(bookmark);
        user.getBookmarks().add(bookmark);
        em.persist(bookmark);

        ArticleLike articleLike = ArticleLike.builder().article(article).user(user).build();
        article.getArticleLikes().add(articleLike);
        user.getArticleLikes().add(articleLike);
        em.persist(articleLike);

        Comment comment = Comment.builder().article(article).content("??????????????????????????????????????????")
                .user(user).build();
        em.persist(comment);
        article.getComments().add(comment);


        CommentDto.Response commentDto = CommentDto.Response.builder().avatar(AvatarDto.SimpleResponse.of(avatar))
                .articleId(article.getId())
                .commentId(comment.getId())
                .content(comment.getContent())
                .userInfo(UserDto.ResponseSimpleUserDto.of(user))
                .avatar(AvatarDto.SimpleResponse.of(avatar))
                .build();
        Long id = userRepository.findUserByEmail(EMAIL1).orElseThrow().getId();

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, id, ROLE_USER_LIST, NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(
                get("/articles/" + article.getId())
                        .header("User-Agent", "Mozilla 5.0")

        );

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(article.getId()))
                .andExpect(jsonPath("$.title").value(article.getTitle()))
                .andExpect(jsonPath("$.content").value(article.getContent()))
                .andExpect(jsonPath("$.clicks").value(article.getClicks()))
                .andExpect(jsonPath("$.likes").value(1))
                .andExpect(jsonPath("$.isClosed").value(false))
                .andExpect(jsonPath("$.isLiked").value(false))
                .andExpect(jsonPath("$.isBookmarked").value(false))
                .andExpect(jsonPath("$.tags[0:1].tagId").value(JAVA.getId().intValue()))
                .andExpect(jsonPath("$.tags[0:1].name").value("JAVA"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.lastModifiedAt").exists())
                .andExpect(jsonPath("$.expiredAt").doesNotExist())
                .andExpect(jsonPath("$.userInfo.userId").value(id))
                .andExpect(jsonPath("$.userInfo.nickname").value("nickname"))
                .andExpect(jsonPath("$.userInfo.grade").value("BRONZE"))
                .andExpect(jsonPath("$.avatar.avatarId").value(avatar.getId()))
                .andExpect(jsonPath("$.avatar.filename").value(avatar.getOriginalFilename()))
                .andExpect(jsonPath("$.avatar.remotePath").value(avatar.getRemotePath()))
                .andExpect(jsonPath("$.comments[0:1].articleId").value(article.getId().intValue()))
                .andExpect(jsonPath("$.comments[0:1].commentId").value(comment.getId().intValue()))
                .andExpect(jsonPath("$.comments[0:1].userInfo.userId").value(user.getId().intValue()))
                .andExpect(jsonPath("$.comments[0:1].userInfo.nickname").value("nickname"))
                .andExpect(jsonPath("$.comments[0:1].userInfo.grade").value("BRONZE"))
                .andExpect(jsonPath("$.comments[0:1].avatar.avatarId").value(avatar.getId().intValue()))
                .andExpect(jsonPath("$.comments[0:1].avatar.filename").value(avatar.getOriginalFilename()))
                .andExpect(jsonPath("$.comments[0:1].avatar.remotePath").value(avatar.getRemotePath()))
                .andExpect(jsonPath("$.comments[0:1].createdAt").exists())
                .andExpect(jsonPath("$.comments[0:1].lastModifiedAt").exists());

    }


}
