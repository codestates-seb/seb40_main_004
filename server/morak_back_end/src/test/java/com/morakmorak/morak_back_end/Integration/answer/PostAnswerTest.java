package com.morakmorak.morak_back_end.Integration.answer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.dto.AnswerDto;
import com.morakmorak.morak_back_end.dto.FileDto;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.ArticleStatus;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
import com.morakmorak.morak_back_end.repository.FileRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
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
     * 게시글이 있을 때 해당 게시글에 대한 답변을 등록 시
     * 15자 이상의 내용과 파일을 첨부해 등록할 수 있다.
     * 등록 성공 시 답변 리스트로 응답이 나간다.
     * 답변을 마친 closed 게시물이라면 작성 실패한다
     * 질문 카테고리의 글에만 답변 작성 가능하다.
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

    /*유저1이 엑세스토큰과 함께 바디에 content와 file을 담아 답변 등록 요청을 한다.
     * 요청 url에는 articleId가 포함되어 있다
     * 아티클을 조회했을 때, 카테고리가 QNA이며, isClosed = false, status = POSTING 이어야만 답변을 등록할 수 있다.
     * */
    @Test
    @DisplayName("카테고리가 question이 아닌 경우 409 예외 반환")
    void postAnswer_failed_1() throws Exception {
        //given
        File file1 = fileRepository.findFileByLocalPath("1").orElseThrow(() -> new AssertionError());
        File file2 = fileRepository.findFileByLocalPath("2").orElseThrow(() -> new AssertionError());

        AnswerDto.RequestPostAnswer request = AnswerDto.RequestPostAnswer.builder().content("10자 이상의 유효한 길이 콘텐츠입니다.")
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
    @DisplayName("정상적인 상태에서 답변은 성공적으로 반환, 201 ")
    void postAnswer_success_1() throws Exception {
        //given
        Category qna = Category.builder().name(CategoryName.QNA).build();
        articleRepository.save(Article.builder().user(savedUser).category(qna).articleStatus(ArticleStatus.POSTING).isClosed(false).build());
        Article validArticle = articleRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());

        File file1 = fileRepository.findFileByLocalPath("1").orElseThrow(() -> new AssertionError());
        File file2 = fileRepository.findFileByLocalPath("2").orElseThrow(() -> new AssertionError());

        AnswerDto.RequestPostAnswer request = AnswerDto.RequestPostAnswer.builder().content("10자이상 유효한 내용의 답변")
                .fileIdList(List.of(FileDto.RequestFileWithId.builder().fileId(file1.getId()).build(),
                        FileDto.RequestFileWithId.builder().fileId(file2.getId()).build()))
                .build();

        String json = objectMapper.writeValueAsString(request);

        Avatar avatar = Avatar.builder().remotePath("remotePath")
                .originalFilename("fileName")
                .build();
        em.persist(avatar);

        User user = User.builder().nickname("nickname").grade(Grade.BRONZE).avatar(avatar).build();

        Article article = Article.builder().title("테스트 타이틀입니다.")
                .content("콘탠트입니다. 질문을 많이 올려주세요.")
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
                .andExpect(jsonPath("$.data[0:1].commentPreview.commentId").isEmpty())
                .andExpect(jsonPath("$.data[0:1].commentPreview.answerId").isEmpty())
                .andExpect(jsonPath("$.data[0:1].commentPreview.content").isEmpty())
                .andExpect(jsonPath("$.data[0:1].commentPreview.userInfo.userId").isEmpty())
                .andExpect(jsonPath("$.data[0:1].commentPreview.userInfo.nickname").isEmpty())
                .andExpect(jsonPath("$.data[0:1].commentPreview.userInfo.grade").isEmpty())
                .andExpect(jsonPath("$.data[0:1].commentPreview.avatar.avatarId").isEmpty())
                .andExpect(jsonPath("$.data[0:1].commentPreview.avatar.filename").isEmpty())
                .andExpect(jsonPath("$.data[0:1].commentPreview.avatar.remotePath").isEmpty())
                .andExpect(jsonPath("$.data[0:1].commentPreview.lastModifiedAt").isEmpty())
                .andExpect(jsonPath("$.data[0:1].commentPreview.createdAt").isEmpty())
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
                .andExpect(jsonPath("$.pageInfo.sort.sorted").value(true));
    }

    @Test
    @DisplayName("답변 작성시 작성자의 포인트가 증가한다.")
    void postAnswer_success_2() throws Exception {
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
                        .content("10자이상 유효한 내용의 답변")
                        .fileIdList(List.of(FileDto.RequestFileWithId.builder().fileId(file1.getId()).build(),
                                FileDto.RequestFileWithId.builder().fileId(file2.getId()).build()))
                        .build();

        String json = objectMapper.writeValueAsString(request);

        User user = User.builder().nickname("nickname").grade(Grade.BRONZE).build();

        Integer beforePoint = user.getPoint();

        Article article = Article.builder().title("테스트 타이틀입니다.")
                .content("콘탠트입니다. 질문을 많이 올려주세요.")
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
    @DisplayName("답변 작성시 질문자에 대한 알림 객체가 생성된다.")
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
                        .content("10자이상 유효한 내용의 답변")
                        .fileIdList(List.of(FileDto.RequestFileWithId.builder().fileId(file1.getId()).build(),
                                FileDto.RequestFileWithId.builder().fileId(file2.getId()).build()))
                        .build();

        String json = objectMapper.writeValueAsString(request);

        User user = User.builder().nickname("nickname").grade(Grade.BRONZE).build();
        User user2 = User.builder().nickname("nickname2").grade(Grade.BRONZE).build();



        Article article = Article.builder().title("테스트 타이틀입니다.")
                .content("콘탠트입니다. 질문을 많이 올려주세요.")
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
        String message = stringBuilder.append("회원님께서 작성하신 ")
                .append("\"")
                .append(article.getTitle())
                .append("\"")
                .append("에 ")
                .append(user.getNickname())
                .append("님께서 답변해주셨어요.")
                .toString();

        assertThat(beforeNotificationSize != afterNotificationSize).isTrue();
        assertThat(article.getUser().getNotifications().get(0).getMessage()).isEqualTo(message);
    }
}
