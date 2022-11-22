package com.morakmorak.morak_back_end.Integration.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.repository.CommentRepository;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
import com.morakmorak.morak_back_end.repository.user.AvatarRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import static com.morakmorak.morak_back_end.util.CommentTestConstants.VALID_COMMENT;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.PASSWORD1;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
public class CommentOnAnswerTest {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    AvatarRepository avatarRepository;
    @Autowired
    ObjectMapper objectMapper;
    @PersistenceContext
    EntityManager em;
    User user;
    Article article;
    Answer answer;


    @BeforeEach
    void setup() {
        Category qna = Category.builder().name(CategoryName.QNA).build();
        em.persist(qna);

        Avatar avatar = Avatar.builder().remotePath("remotePath")
                .originalFilename("fileName")
                .build();
        em.persist(avatar);

        this.user = User.builder().email(EMAIL1).nickname("nickname").grade(Grade.BRONZE).avatar(avatar).build();

        this.article = Article.builder().title("테스트 타이틀입니다.")
                .content("콘탠트입니다. 질문을 많이 올려주세요.")
                .category(qna)
                .user(user)
                .build();

        qna.getArticleList().add(article);
        em.persist(article);

        this.answer = Answer.builder()
                .content("15글자 이상의 유효한 답변내용입니다.")
                .user(user)
                .isPicked(false)
                .article(article).build();
        user.getAnswers().add(answer);
        article.getAnswers().add(answer);
        em.persist(answer);
    }

    @Test
    @DisplayName("답변에 댓글 등록 시 201 반환")
    void postCommentOnAnswer_success_1() throws Exception {


        CommentDto.Request request = CommentDto.Request.builder()
                .content(VALID_COMMENT).build();

        String json = objectMapper.writeValueAsString(request);
        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, user.getId(), ROLE_USER_LIST);
        ResultActions perform = mockMvc.perform(post("/answers/{answer-id}/comments", answer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, accessToken)
        );
        //then 201 created 반환
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].userInfo.userId").exists())
                .andExpect(jsonPath("$[0].userInfo.nickname").exists())
                .andExpect(jsonPath("$[0].avatar.avatarId").exists())
                .andExpect(jsonPath("$[0].avatar.filename").exists())
                .andExpect(jsonPath("$[0].avatar.remotePath").exists())
                .andExpect(jsonPath("$[0].answerId").exists())
                .andExpect(jsonPath("$[0].content").exists())
                .andExpect(jsonPath("$[0].commentId").exists())
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].lastModifiedAt").exists());
    }

    @Test
    @DisplayName("답변에 댓글 작성 시 DTO검증에 실패하면 400 예외를 반환한다.")
    void postComment_Answer_failed_1() throws Exception {
        //given blank인 댓글 dto 준비
        CommentDto.Request request = CommentDto.Request.builder()
                .content(null).build();
        String json = objectMapper.writeValueAsString(request);
        //when blank인 댓글 인입 시
        ResultActions perform = mockMvc.perform(post("/answers/{answer-id}/comments", answer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, ACCESS_TOKEN)
        );
        //then bad request 반환
        perform.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("답변에 댓글 수정 시 200 반환")
    void editCommentOnAnswer_success_1() throws Exception {
        Category qna = Category.builder().name(CategoryName.QNA).build();
        em.persist(qna);

        Avatar avatar = Avatar.builder().remotePath("remotePath")
                .originalFilename("fileName")
                .build();
        em.persist(avatar);

        User user = User.builder().email(EMAIL1).nickname("nickname").grade(Grade.BRONZE).avatar(avatar).build();

        Article article = Article.builder().title("테스트 타이틀입니다.")
                .content("콘탠트입니다. 질문을 많이 올려주세요.")
                .category(qna)
                .user(user)
                .build();

        qna.getArticleList().add(article);
        em.persist(article);

        Answer answer = Answer.builder()
                .content("15글자 이상의 유효한 답변내용입니다.")
                .user(user)
                .isPicked(false)
                .article(article).build();
        user.getAnswers().add(answer);
        article.getAnswers().add(answer);
        em.persist(answer);
        Comment comment = Comment.builder().user(user).answer(answer).content("댓글을 달았을 것입니다요.").build();
        answer.getComments().add(comment);
        user.getComments().add(comment);
        em.persist(comment);
        CommentDto.Request request = CommentDto.Request.builder()
                .content("수정할 내용의 댓글입니다.").build();

        String json = objectMapper.writeValueAsString(request);
        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, user.getId(), ROLE_USER_LIST);

        ResultActions perform = mockMvc.perform(patch("/answers/{answer-id}/comments/{comment-id}", answer.getId(), comment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, accessToken)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userInfo.userId").exists())
                .andExpect(jsonPath("$[0].userInfo.nickname").exists())
                .andExpect(jsonPath("$[0].avatar.avatarId").exists())
                .andExpect(jsonPath("$[0].avatar.filename").exists())
                .andExpect(jsonPath("$[0].avatar.remotePath").exists())
                .andExpect(jsonPath("$[0].answerId").exists())
                .andExpect(jsonPath("$[0].content").exists())
                .andExpect(jsonPath("$[0].commentId").exists())
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].lastModifiedAt").exists());
    }

    @Test
    @DisplayName("답변의 댓글 수정 시 수정 권한이 없다면 409를 반환한다.")
    void updateComment_Answer_failed_1() throws Exception {
        //given 새로운 유저 등장
        CommentDto.Request request = CommentDto.Request.builder()
                .content(VALID_COMMENT).build();
        Comment comment = Comment.builder().user(user).answer(answer).build();
        em.persist(comment);
        User newUser = User.builder()
                .email(EMAIL2)
                .nickname(NICKNAME2)
                .password(PASSWORD1)
                .build();
        em.persist(newUser);
        String accessTokenForNewUser = jwtTokenUtil.createAccessToken(EMAIL2, newUser.getId(), ROLE_USER_LIST);
        String json = objectMapper.writeValueAsString(request);

        //when 권한 없는 유저가 수정 요청을 보냈을 때
        ResultActions perform = mockMvc.perform(patch("/answers/{answer-id}/comments/{comment-id}", answer.getId(), comment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, accessTokenForNewUser)
        );
        //then 409 conflict 발생
        perform
                .andExpect(status().isConflict());
    }


    @Test
    @DisplayName("답변의 댓글 삭제 시 유효한 데이터가 인입되었다면 200을 반환한다.")
    void deleteComment_success_1() throws Exception {
        Category qna = Category.builder().name(CategoryName.QNA).build();
        em.persist(qna);

        Avatar avatar = Avatar.builder().remotePath("remotePath")
                .originalFilename("fileName")
                .build();
        em.persist(avatar);

        User user = User.builder().email(EMAIL1).nickname("nickname").grade(Grade.BRONZE).avatar(avatar).build();
        User user1 = User.builder().email(EMAIL2).nickname("nickname1").grade(Grade.BRONZE).avatar(avatar).build();

        Article article = Article.builder().title("테스트 타이틀입니다.")
                .content("콘탠트입니다. 질문을 많이 올려주세요.")
                .category(qna)
                .user(user)
                .build();

        qna.getArticleList().add(article);
        em.persist(article);
        em.persist(user1);
        em.persist(user);

        Answer answer = Answer.builder()
                .content("15글자 이상의 유효한 답변내용입니다.")
                .user(user)
                .isPicked(false)
                .article(article).build();
        user.getAnswers().add(answer);
        article.getAnswers().add(answer);

        em.persist(answer);
        Comment comment = Comment.builder().user(user).answer(answer).content("댓글을 달았을 것입니다요.").build();
        Comment comment1 = Comment.builder().user(user1).answer(answer).content("다른 유저의 댓글입니다.").build();
        answer.getComments().add(comment);
        answer.getComments().add(comment1);
        user.getComments().add(comment);
        user1.getComments().add(comment1);

        em.persist(comment);
        em.persist(comment1);

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, user.getId(), ROLE_USER_LIST);
        //when 유효한 input
        ResultActions perform = mockMvc.perform(delete("/answers/{answer-id}/comments/{comment-id}", answer.getId(), comment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(JWT_HEADER, accessToken)
        );
        //then 200 ok 반환
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commentId").value(comment1.getId().intValue()))
                .andExpect(jsonPath("$[0].answerId", is(answer.getId().intValue())))
                .andExpect(jsonPath("$[0].userInfo.userId", is(user1.getId().intValue())))
                .andExpect(jsonPath("$[0].userInfo.nickname").exists())
                .andExpect(jsonPath("$[0].userInfo.grade").exists())
                .andExpect(jsonPath("$[0].avatar.avatarId").exists())
                .andExpect(jsonPath("$[0].avatar.filename").exists())
                .andExpect(jsonPath("$[0].avatar.remotePath").exists())
                .andExpect(jsonPath("$[0].content").value("다른 유저의 댓글입니다."))
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].lastModifiedAt").exists());
    }


}
