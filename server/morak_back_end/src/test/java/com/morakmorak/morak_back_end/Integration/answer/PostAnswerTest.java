package com.morakmorak.morak_back_end.Integration.answer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.dto.AnswerDto;
import com.morakmorak.morak_back_end.dto.FileDto;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.ArticleStatus;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.repository.FileRepository;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ROLE_USER_LIST;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
public class PostAnswerTest {
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    User savedUser;
    File savedFile;
    String accessToken;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @PersistenceContext
    EntityManager em;

    /*
     * ???????????? ?????? ??? ?????? ???????????? ?????? ????????? ?????? ???
     * 15??? ????????? ????????? ????????? ????????? ????????? ??? ??????.
     * ?????? ?????? ??? ?????? ???????????? ????????? ?????????.
     * ????????? ?????? closed ?????????????????? ?????? ????????????
     * ?????? ??????????????? ????????? ?????? ?????? ????????????.
     */
    @BeforeEach
    void setUp() {
        Avatar dbAvatar = Avatar.builder().originalFilename("randomfilename").remotePath("randomremotepath").build();
        Category qna = Category.builder().name(CategoryName.QNA).build();
        Category info = Category.builder().name(CategoryName.INFO).build();

        userRepository.save(User.builder().email(EMAIL1).name(NAME1).nickname(NICKNAME1).grade(Grade.GOLD).avatar(dbAvatar).build());
        fileRepository.save(File.builder().localPath("1").build());
        fileRepository.save(File.builder().localPath("2").build());


        this.savedUser = userRepository.findUserByEmail(EMAIL1).orElseThrow(() -> new AssertionError());
        this.savedFile = fileRepository.findFileByLocalPath("1").orElseThrow(() -> new AssertionError());
        this.accessToken = jwtTokenUtil.createAccessToken(EMAIL1, savedUser.getId(), ROLE_USER_LIST, NICKNAME1);

    }

    /*??????1??? ?????????????????? ?????? ????????? content??? file??? ?????? ?????? ?????? ????????? ??????.
     * ?????? url?????? articleId??? ???????????? ??????
     * ???????????? ???????????? ???, ??????????????? QNA??????, isClosed = false, status = POSTING ???????????? ????????? ????????? ??? ??????.
     * */
    @Test
    @DisplayName("??????????????? question??? ?????? ?????? 409 ?????? ??????")
    void postAnswer_failed_1() throws Exception {
        //given
        File file1 = fileRepository.findFileByLocalPath("1").orElseThrow(() -> new AssertionError());
        File file2 = fileRepository.findFileByLocalPath("2").orElseThrow(() -> new AssertionError());

        AnswerDto.RequestPostAnswer request = AnswerDto.RequestPostAnswer.builder().content("10??? ????????? ????????? ?????? ??????????????????.")
                .fileIdList(List.of(FileDto.RequestFileWithId.builder().fileId(file1.getId()).build(),
                        FileDto.RequestFileWithId.builder().fileId(file2.getId()).build()))
                .build();

        String json = objectMapper.writeValueAsString(request);
        Category info = Category.builder().name(CategoryName.INFO).build();
        articleRepository.save(Article.builder().user(savedUser).category(info).articleStatus(ArticleStatus.POSTING).isClosed(false).build());
        Article invalidArticle_info = articleRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());
        //when
        ResultActions perform = mockMvc.perform(post("/articles/{article-id}/answers/", invalidArticle_info.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, accessToken)
        );
        //then
        perform.andExpect(status().isConflict());
    }

    @Test
    @DisplayName("???????????? ???????????? ????????? ??????????????? ??????, 201 ")
    void postAnswer_success_1() throws Exception {
        //given
        Category qna = Category.builder().name(CategoryName.QNA).build();
        articleRepository.save(Article.builder().user(savedUser).category(qna).articleStatus(ArticleStatus.POSTING).isClosed(false).build());
        Article validArticle = articleRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());

        File file1 = fileRepository.findFileByLocalPath("1").orElseThrow(() -> new AssertionError());
        File file2 = fileRepository.findFileByLocalPath("2").orElseThrow(() -> new AssertionError());

        AnswerDto.RequestPostAnswer request = AnswerDto.RequestPostAnswer.builder().content("10????????? ????????? ????????? ??????")
                .fileIdList(List.of(FileDto.RequestFileWithId.builder().fileId(file1.getId()).build(),
                        FileDto.RequestFileWithId.builder().fileId(file2.getId()).build()))
                .build();

        String json = objectMapper.writeValueAsString(request);

        Avatar avatar = Avatar.builder().remotePath("remotePath")
                .originalFilename("fileName")
                .build();
        em.persist(avatar);

        User user = User.builder().nickname("nickname").grade(Grade.CANDLE).avatar(avatar).build();

        Article article = Article.builder().title("????????? ??????????????????.")
                .content("??????????????????. ????????? ?????? ???????????????.")
                .category(qna)
                .user(user)
                .build();

        qna.getArticleList().add(article);
        em.persist(article);
        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, user.getId(), ROLE_USER_LIST, NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(
                post("/articles/{article-id}/answers", article.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header(JWT_HEADER, accessToken)
        );
        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data[0:1].answerId").exists())
                .andExpect(jsonPath("$.data[0:1].content").exists())
                .andExpect(jsonPath("$.data[0:1].answerLikeCount").exists())
                .andExpect(jsonPath("$.data[0:1].isPicked").value(false))
                .andExpect(jsonPath("$.data[0:1].commentCount").value(0))
                .andExpect(jsonPath("$.data[0:1].createdAt").exists())
                .andExpect(jsonPath("$.data[0:1].userInfo.userId").value(user.getId().intValue()))
                .andExpect(jsonPath("$.data[0:1].userInfo.nickname").exists())
                .andExpect(jsonPath("$.data[0:1].userInfo.grade").exists())
                .andExpect(jsonPath("$.data[0:1].avatar.avatarId").value(avatar.getId().intValue()))
                .andExpect(jsonPath("$.data[0:1].avatar.filename").exists())
                .andExpect(jsonPath("$.data[0:1].avatar.remotePath").exists())
                .andExpect(jsonPath("$.pageInfo.page").value(1))
                .andExpect(jsonPath("$.pageInfo.size").value(5))
                .andExpect(jsonPath("$.pageInfo.totalElements").value(1))
                .andExpect(jsonPath("$.pageInfo.totalPages").value(1))
                .andExpect(jsonPath("$.pageInfo.sort.sorted").value(false));
    }
    @Test
    @DisplayName("?????? ???????????? ????????? ????????? ??????????????? ??????, 201 ")
    void postAnswer_success_2() throws Exception {
        //given
        Category qna = Category.builder().name(CategoryName.QNA).build();
        articleRepository.save(Article.builder().user(savedUser).category(qna).articleStatus(ArticleStatus.POSTING).isClosed(false).build());
        Article validArticle = articleRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());

        AnswerDto.RequestPostAnswer request = AnswerDto.RequestPostAnswer.builder().content("10????????? ????????? ????????? ??????").build();

        String json = objectMapper.writeValueAsString(request);

        Avatar avatar = Avatar.builder().remotePath("remotePath")
                .originalFilename("fileName")
                .build();
        em.persist(avatar);

        User user = User.builder().nickname("nickname").grade(Grade.CANDLE).avatar(avatar).build();

        Article article = Article.builder().title("????????? ??????????????????.")
                .content("??????????????????. ????????? ?????? ???????????????.")
                .category(qna)
                .user(user)
                .build();

        qna.getArticleList().add(article);
        em.persist(article);
        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, user.getId(), ROLE_USER_LIST, NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(
                post("/articles/{article-id}/answers", article.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header(JWT_HEADER, accessToken)
        );
        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data[0:1].answerId").exists())
                .andExpect(jsonPath("$.data[0:1].content").exists())
                .andExpect(jsonPath("$.data[0:1].answerLikeCount").exists())
                .andExpect(jsonPath("$.data[0:1].isPicked").value(false))
                .andExpect(jsonPath("$.data[0:1].commentCount").value(0))
                .andExpect(jsonPath("$.data[0:1].createdAt").exists())
                .andExpect(jsonPath("$.data[0:1].userInfo.userId").value(user.getId().intValue()))
                .andExpect(jsonPath("$.data[0:1].userInfo.nickname").exists())
                .andExpect(jsonPath("$.data[0:1].userInfo.grade").exists())
                .andExpect(jsonPath("$.data[0:1].avatar.avatarId").value(avatar.getId().intValue()))
                .andExpect(jsonPath("$.data[0:1].avatar.filename").exists())
                .andExpect(jsonPath("$.data[0:1].avatar.remotePath").exists())
                .andExpect(jsonPath("$.pageInfo.page").value(1))
                .andExpect(jsonPath("$.pageInfo.size").value(5))
                .andExpect(jsonPath("$.pageInfo.totalElements").value(1))
                .andExpect(jsonPath("$.pageInfo.totalPages").value(1))
                .andExpect(jsonPath("$.pageInfo.sort.sorted").value(false));
    }

    @Test
    @DisplayName("?????? ????????? ???????????? ???????????? ????????????.")
    void postAnswer_success_3() throws Exception {
        //given
        articleRepository.save(Article.builder()
                .user(savedUser)
                .category(new Category(CategoryName.QNA))
                .articleStatus(ArticleStatus.POSTING)
                .isClosed(false).build());

        Article validArticle = articleRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());

        File file1 = fileRepository.findFileByLocalPath("1").orElseThrow(() -> new AssertionError());
        File file2 = fileRepository.findFileByLocalPath("2").orElseThrow(() -> new AssertionError());

        AnswerDto.RequestPostAnswer request =
                AnswerDto.RequestPostAnswer.builder()
                        .content("10????????? ????????? ????????? ??????")
                        .fileIdList(List.of(FileDto.RequestFileWithId.builder().fileId(file1.getId()).build(),
                                FileDto.RequestFileWithId.builder().fileId(file2.getId()).build()))
                        .build();

        String json = objectMapper.writeValueAsString(request);

        User user = User.builder().nickname("nickname").grade(Grade.BRONZE).build();

        Integer beforePoint = user.getPoint();

        Article article = Article.builder().title("????????? ??????????????????.")
                .content("??????????????????. ????????? ?????? ???????????????.")
                .category(new Category(CategoryName.QNA))
                .user(user)
                .files(List.of(file1, file2))
                .build();

        em.persist(article);
        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, user.getId(), ROLE_USER_LIST, NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(
                post("/articles/{article-id}/answers", article.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header(JWT_HEADER, accessToken)
        );
        Integer afterPoint = user.getPoint();

        //then
        assertThat(beforePoint < afterPoint).isTrue();
    }

    @Test
    @DisplayName("?????? ????????? ???????????? ?????? ?????? ????????? ????????????.")
    void postAnswer_success_4() throws Exception {
        //given
        articleRepository.save(Article.builder()
                .user(savedUser)
                .category(new Category(CategoryName.QNA))
                .articleStatus(ArticleStatus.POSTING)
                .isClosed(false).build());

        Article validArticle = articleRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());

        File file1 = fileRepository.findFileByLocalPath("1").orElseThrow(() -> new AssertionError());
        File file2 = fileRepository.findFileByLocalPath("2").orElseThrow(() -> new AssertionError());

        AnswerDto.RequestPostAnswer request =
                AnswerDto.RequestPostAnswer.builder()
                        .content("10????????? ????????? ????????? ??????")
                        .fileIdList(List.of(FileDto.RequestFileWithId.builder().fileId(file1.getId()).build(),
                                FileDto.RequestFileWithId.builder().fileId(file2.getId()).build()))
                        .build();

        String json = objectMapper.writeValueAsString(request);

        User user = User.builder().nickname("nickname").grade(Grade.BRONZE).build();
        User user2 = User.builder().nickname("nickname2").grade(Grade.BRONZE).build();



        Article article = Article.builder().title("????????? ??????????????????.")
                .content("??????????????????. ????????? ?????? ???????????????.")
                .category(new Category(CategoryName.QNA))
                .user(user2)
                .files(List.of(file1, file2))
                .build();

        int beforeNotificationSize = article.getUser().getNotifications().size();
        em.persist(article);
        em.persist(user);
        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, user.getId(), ROLE_USER_LIST, NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(
                post("/articles/{article-id}/answers", article.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header(JWT_HEADER, accessToken)
        );
        int afterNotificationSize = article.getUser().getNotifications().size();

        //then

        StringBuilder stringBuilder = new StringBuilder();
        String message = stringBuilder.append("??????????????? ???????????? ")
                .append("\"")
                .append(article.getTitle())
                .append("\"")
                .append("??? ")
                .append(user.getNickname())
                .append("????????? ?????????????????????.")
                .toString();

        assertThat(beforeNotificationSize != afterNotificationSize).isTrue();
        assertThat(article.getUser().getNotifications().get(0).getMessage()).isEqualTo(message);
    }
}
