package com.morakmorak.morak_back_end.Integration.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.Avatar;
import com.morakmorak.morak_back_end.entity.Comment;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
import com.morakmorak.morak_back_end.repository.user.AvatarRepository;
import com.morakmorak.morak_back_end.repository.CommentRepository;
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
import org.springframework.transaction.annotation.Transactional;
import static org.hamcrest.Matchers.*;
import static com.morakmorak.morak_back_end.util.CommentTestConstants.VALID_COMMENT;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
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
        userRepository.save(dbUser);
        this.savedUser = userRepository.findUserByEmail(EMAIL1).orElseThrow(() -> new AssertionError());
        this.accessToken = jwtTokenUtil.createAccessToken(EMAIL1, savedUser.getId(), ROLE_USER_LIST);

        avatarRepository.save(dbAvatar);
        Avatar savedAvatar = avatarRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());

        Article dbArticle = Article.builder().user(savedUser).build();
        articleRepository.save(dbArticle);
        this.savedArticle = articleRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());
    }

    @Test
    @DisplayName("댓글 작성 시 DTO검증에 실패하면 400 예외를 반환한다.")
    void postComment_failed_1() throws Exception {
        //given blank인 댓글 dto 준비
        CommentDto.Request request = CommentDto.Request.builder()
                .content(null).build();
        String json = objectMapper.writeValueAsString(request);
        //when blank인 댓글 인입 시
        ResultActions perform = mockMvc.perform(post("/articles/{article-id}/comments", savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, ACCESS_TOKEN)
        );
        //then bad request 반환
        perform.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 작성 시 유효한 데이터가 인입되었다면 201을 반환한다.")
    void postComment_success_1() throws Exception {
        //given 유효한 댓글
        CommentDto.Request request = CommentDto.Request.builder()
                .content(VALID_COMMENT).build();

        String json = objectMapper.writeValueAsString(request);
        //when 유효한 input
        ResultActions perform = mockMvc.perform(post("/articles/{article-id}/comments", savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, accessToken)
        );
        //then 201 created 반환
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.userInfo.userId").exists())
                .andExpect(jsonPath("$.userInfo.nickname").exists())
                .andExpect(jsonPath("$.avatar.avatarId").exists())
                .andExpect(jsonPath("$.avatar.filename").exists())
                .andExpect(jsonPath("$.avatar.remotePath").exists())
                .andExpect(jsonPath("$.articleId").exists())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.commentId").exists());
    }
    @Test
    @DisplayName("댓글 수정 시 유효한 데이터가 인입되었다면 200을 반환한다.")
    void updateComment_success_1() throws Exception {
        //given 유효한 댓글 수정 요청
        CommentDto.Request request = CommentDto.Request.builder()
                .content(VALID_COMMENT).build();
        commentRepository.save(Comment.builder().user(savedUser).article(savedArticle).build());
        Comment savedComment = commentRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());
        String json = objectMapper.writeValueAsString(request);

        //when 유효한 input
        ResultActions perform = mockMvc.perform(patch("/articles/{article-id}/comments/{comment-id}", savedArticle.getId(),savedComment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, accessToken)
        );
        //then 200 ok 반환
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commentId").value(savedComment.getId().intValue()))
                .andExpect(jsonPath("$[0].articleId",is(savedArticle.getId().intValue())))
                .andExpect(jsonPath("$[0].userInfo.userId",is(savedUser.getId().intValue())))
                .andExpect(jsonPath("$[0].userInfo.nickname").exists())
                .andExpect(jsonPath("$[0].userInfo.grade").isEmpty())
                .andExpect(jsonPath("$[0].avatar.avatarId").exists())
                .andExpect(jsonPath("$[0].avatar.filename").exists())
                .andExpect(jsonPath("$[0].avatar.remotePath").exists())
                .andExpect(jsonPath("$[0].content").exists())
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].lastModifiedAt").exists());
    }
    @Test
    @DisplayName("댓글 수정 시 수정 권한이 없다면 409를 반환한다.")
    void updateComment_failed_1() throws Exception {
        //given 새로운 유저 등장
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
        String accessTokenForNewUser = jwtTokenUtil.createAccessToken(EMAIL2, savedNewUser.getId(), ROLE_USER_LIST);
        String json = objectMapper.writeValueAsString(request);

        //when 권한 없는 유저가 수정 요청을 보냈을 때
        ResultActions perform = mockMvc.perform(patch("/articles/{article-id}/comments/{comment-id}", savedArticle.getId(),savedComment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, accessTokenForNewUser)
        );
        //then 409 conflict 발생
        perform
                .andExpect(status().isConflict());
    }
    @Test
    @DisplayName("댓글 삭제 시 권한이 없다면 409를 반환한다.")
    void deleteComment_failed_1() throws Exception {
        //given 새로운 유저 등장
        commentRepository.save(Comment.builder().user(savedUser).article(savedArticle).build());
        Comment savedComment = commentRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());

        User newUser = User.builder()
                .email(EMAIL2)
                .nickname(NICKNAME2)
                .password(PASSWORD1)
                .build();
        userRepository.save(newUser);
        User savedNewUser = userRepository.findUserByEmail(EMAIL2).orElseThrow(() -> new AssertionError());
        String accessTokenForNewUser = jwtTokenUtil.createAccessToken(EMAIL2, savedNewUser.getId(), ROLE_USER_LIST);

        //when 권한 없는 유저가 삭제 요청을 보냈을 때
        ResultActions perform = mockMvc.perform(delete("/articles/{article-id}/comments/{comment-id}", savedArticle.getId(),savedComment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(JWT_HEADER, accessTokenForNewUser)
        );
        //then 409 conflict 발생
        perform
                .andExpect(status().isConflict());
    }
    @Test
    @DisplayName("댓글 수정 시 유효한 데이터가 인입되었다면 200을 반환한다.")
    void deleteComment_success_1() throws Exception {
        //given 유효한 댓글 수정 요청

        commentRepository.save(Comment.builder().user(savedUser).article(savedArticle).content("지워질 댓글입니다.").build());
        Comment savedComment = commentRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());
        commentRepository.save(Comment.builder().user(savedUser).article(savedArticle).content("살아남을 댓글입니다.").build());

        //when 유효한 input
        ResultActions perform = mockMvc.perform(delete("/articles/{article-id}/comments/{comment-id}", savedArticle.getId(),savedComment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(JWT_HEADER, accessToken)
        );
        Comment survivedComment = commentRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());
        //then 200 ok 반환
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commentId").value(survivedComment.getId().intValue()))
                .andExpect(jsonPath("$[0].articleId",is(savedArticle.getId().intValue())))
                .andExpect(jsonPath("$[0].userInfo.userId",is(savedUser.getId().intValue())))
                .andExpect(jsonPath("$[0].userInfo.nickname").exists())
                .andExpect(jsonPath("$[0].userInfo.grade").isEmpty())
                .andExpect(jsonPath("$[0].avatar.avatarId").exists())
                .andExpect(jsonPath("$[0].avatar.filename").exists())
                .andExpect(jsonPath("$[0].avatar.remotePath").exists())
                .andExpect(jsonPath("$[0].content").value("살아남을 댓글입니다."))
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].lastModifiedAt").exists());
    }
}
