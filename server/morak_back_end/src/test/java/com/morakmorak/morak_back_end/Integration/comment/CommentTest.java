package com.morakmorak.morak_back_end.Integration.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.Avatar;
import com.morakmorak.morak_back_end.entity.Comment;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.repository.CommentRepository;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
import com.morakmorak.morak_back_end.repository.user.AvatarRepository;
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

import static com.morakmorak.morak_back_end.entity.enums.Grade.*;
import static com.morakmorak.morak_back_end.util.CommentTestConstants.VALID_COMMENT;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
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
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
public class CommentTest {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    AvatarRepository avatarRepository;
    @PersistenceContext
    EntityManager em;

    User savedUser;
    Article savedArticle;
    String accessToken;

    @BeforeEach
    public void setUp() throws Exception {
        Avatar dbAvatar = Avatar.builder()
                .originalFilename("randomfilename")
                .remotePath("randomremotepath")
                .build();

        User dbUser = User.builder()
                .email(EMAIL1)
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .avatar(dbAvatar)
                .build();
        em.persist(dbUser);

        this.savedUser = dbUser;
        this.accessToken = jwtTokenUtil.createAccessToken(EMAIL1, savedUser.getId(), ROLE_USER_LIST, NICKNAME1);

        avatarRepository.save(dbAvatar);
        Avatar savedAvatar = avatarRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());

        Article dbArticle = Article.builder().user(savedUser).build();
        articleRepository.save(dbArticle);
        this.savedArticle = articleRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());
    }

    @Test
    @DisplayName("?????? ?????? ??? DTO????????? ???????????? 400 ????????? ????????????.")
    void postComment_failed_1() throws Exception {
        //given blank??? ?????? dto ??????
        CommentDto.Request request = CommentDto.Request.builder()
                .content(null).build();
        String json = objectMapper.writeValueAsString(request);
        //when blank??? ?????? ?????? ???
        ResultActions perform = mockMvc.perform(post("/articles/{article-id}/comments", savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, accessToken)
        );
        //then bad request ??????
        perform.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("?????? ?????? ??? ????????? ???????????? ?????????????????? 201??? ????????????.")
    void postComment_success_1() throws Exception {
        //given ????????? ??????
        CommentDto.Request request = CommentDto.Request.builder()
                .content(VALID_COMMENT).build();

        String json = objectMapper.writeValueAsString(request);
        //when ????????? input
        ResultActions perform = mockMvc.perform(post("/articles/{article-id}/comments", savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, accessToken)
        );
        //then 201 created ??????
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].userInfo.userId").exists())
                .andExpect(jsonPath("$[0].userInfo.nickname").exists())
                .andExpect(jsonPath("$[0].userInfo.grade", is(MATCH.toString())))
                .andExpect(jsonPath("$[0].avatar.avatarId").exists())
                .andExpect(jsonPath("$[0].avatar.filename").exists())
                .andExpect(jsonPath("$[0].avatar.remotePath").exists())
                .andExpect(jsonPath("$[0].articleId").exists())
                .andExpect(jsonPath("$[0].content").exists())
                .andExpect(jsonPath("$[0].commentId").exists());
    }
    @Test
    @DisplayName("?????? ?????? ??? ????????? ???????????? ?????????????????? 200??? ????????????.")
    void updateComment_success_1() throws Exception {
        //given ????????? ?????? ?????? ??????
        CommentDto.Request request = CommentDto.Request.builder()
                .content(VALID_COMMENT).build();
        commentRepository.save(Comment.builder().user(savedUser).article(savedArticle).build());
        Comment savedComment = commentRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());
        String json = objectMapper.writeValueAsString(request);

        //when ????????? input
        ResultActions perform = mockMvc.perform(patch("/articles/{article-id}/comments/{comment-id}", savedArticle.getId(),savedComment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, accessToken)
        );
        //then 200 ok ??????
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commentId").value(savedComment.getId().intValue()))
                .andExpect(jsonPath("$[0].articleId",is(savedArticle.getId().intValue())))
                .andExpect(jsonPath("$[0].userInfo.userId",is(savedUser.getId().intValue())))
                .andExpect(jsonPath("$[0].userInfo.nickname").exists())
                .andExpect(jsonPath("$[0].userInfo.grade", is(CANDLE.toString())))
                .andExpect(jsonPath("$[0].avatar.avatarId").exists())
                .andExpect(jsonPath("$[0].avatar.filename").exists())
                .andExpect(jsonPath("$[0].avatar.remotePath").exists())
                .andExpect(jsonPath("$[0].content").exists())
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].lastModifiedAt").exists());
    }
    @Test
    @DisplayName("?????? ?????? ??? ?????? ????????? ????????? 401 ????????????.")
    void updateComment_failed_1() throws Exception {
        //given ????????? ?????? ??????
        CommentDto.Request request = CommentDto.Request.builder()
                .content(VALID_COMMENT).build();

        commentRepository.save(Comment.builder().user(savedUser).article(savedArticle).build());
        Comment savedComment = commentRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());

        User newUser = User.builder()
                .email(EMAIL2)
                .nickname(NICKNAME2)
                .password(PASSWORD1)
                .build();
        userRepository.save(newUser);
        User savedNewUser = userRepository.findUserByEmail(EMAIL2).orElseThrow(() -> new AssertionError());
        String accessTokenForNewUser = jwtTokenUtil.createAccessToken(EMAIL2, savedNewUser.getId(), ROLE_USER_LIST, NICKNAME1);
        String json = objectMapper.writeValueAsString(request);

        //when ?????? ?????? ????????? ?????? ????????? ????????? ???
        ResultActions perform = mockMvc.perform(patch("/articles/{article-id}/comments/{comment-id}", savedArticle.getId(),savedComment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, accessTokenForNewUser)
        );
        //then 409 conflict ??????
        perform
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("?????? ?????? ??? ????????? ????????? 401 ????????????.")
    void deleteComment_failed_1() throws Exception {
        //given ????????? ?????? ??????
        commentRepository.save(Comment.builder().user(savedUser).article(savedArticle).build());
        Comment savedComment = commentRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());

        User newUser = User.builder()
                .email(EMAIL2)
                .nickname(NICKNAME2)
                .password(PASSWORD1)
                .build();
        userRepository.save(newUser);
        User savedNewUser = userRepository.findUserByEmail(EMAIL2).orElseThrow(() -> new AssertionError());
        String accessTokenForNewUser = jwtTokenUtil.createAccessToken(EMAIL2, savedNewUser.getId(), ROLE_USER_LIST, NICKNAME1);

        //when ?????? ?????? ????????? ?????? ????????? ????????? ???
        ResultActions perform = mockMvc.perform(delete("/articles/{article-id}/comments/{comment-id}", savedArticle.getId(),savedComment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(JWT_HEADER, accessTokenForNewUser)
        );
        //then 409 conflict ??????
        perform
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("?????? ?????? ??? ????????? ???????????? ?????????????????? 200??? ????????????.")
    void deleteComment_success_1() throws Exception {
        //given ????????? ?????? ?????? ??????

        commentRepository.save(Comment.builder().user(savedUser).article(savedArticle).content("????????? ???????????????.").build());
        Comment savedComment = commentRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());
        commentRepository.save(Comment.builder().user(savedUser).article(savedArticle).content("???????????? ???????????????.").build());

        //when ????????? input
        ResultActions perform = mockMvc.perform(delete("/articles/{article-id}/comments/{comment-id}", savedArticle.getId(),savedComment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(JWT_HEADER, accessToken)
        );
        Comment survivedComment = commentRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());
        //then 200 ok ??????
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commentId").value(survivedComment.getId().intValue()))
                .andExpect(jsonPath("$[0].articleId",is(savedArticle.getId().intValue())))
                .andExpect(jsonPath("$[0].userInfo.userId",is(savedUser.getId().intValue())))
                .andExpect(jsonPath("$[0].userInfo.nickname").exists())
                .andExpect(jsonPath("$[0].userInfo.grade", is(MATCH.toString())))
                .andExpect(jsonPath("$[0].avatar.avatarId").exists())
                .andExpect(jsonPath("$[0].avatar.filename").exists())
                .andExpect(jsonPath("$[0].avatar.remotePath").exists())
                .andExpect(jsonPath("$[0].content").value("???????????? ???????????????."))
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].lastModifiedAt").exists());
    }

    @Test
    @DisplayName("?????? ?????? ??? ????????? ???????????? ????????????.")
    void postComment_success_2() throws Exception {
        //given
        CommentDto.Request request = CommentDto.Request.builder()
                .content(VALID_COMMENT).build();
        Integer beforePoint = savedUser.getPoint();

        String json = objectMapper.writeValueAsString(request);
        //when
        ResultActions perform = mockMvc.perform(post("/articles/{article-id}/comments", savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, accessToken)
        );
        //then
        Integer afterPoint = savedUser.getPoint();
        assertThat(beforePoint < afterPoint).isTrue();
    }

    @Test
    @DisplayName("?????? ?????? ??? ????????? ??????????????? ????????? ????????????")
    void postComment_success_3() throws Exception {
        //given
        CommentDto.Request request = CommentDto.Request.builder()
                .content(VALID_COMMENT).build();

        String json = objectMapper.writeValueAsString(request);
        //when
        ResultActions perform = mockMvc.perform(post("/articles/{article-id}/comments", savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, accessToken)
        );
        //then
        StringBuilder stringBuilder = new StringBuilder();
        String message = stringBuilder.append("??????????????? ???????????? ")
                .append("\"")
                .append(savedArticle.getTitle())
                .append("\"")
                .append("??? ")
                .append(savedUser.getNickname())
                .append("????????? ????????? ???????????????.")
                .toString();

        assertThat(savedArticle.getUser().getNotifications().size()).isEqualTo(1);
        assertThat(savedArticle.getUser().getNotifications().get(0).getMessage()).isEqualTo(message);
    }
}
