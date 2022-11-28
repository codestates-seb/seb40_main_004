package com.morakmorak.morak_back_end.Integration.answer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.dto.AnswerDto;
import com.morakmorak.morak_back_end.dto.FileDto;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.ArticleStatus;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.repository.FileRepository;
import com.morakmorak.morak_back_end.repository.answer.AnswerRepository;
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
import java.util.ArrayList;
import java.util.List;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ROLE_USER_LIST;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
public class UpdateAnswerTest {
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    User savedUser;
    File savedFile;
    String accessToken;
    Answer savedAnswer;
    Article validArticle;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @PersistenceContext
    EntityManager em;

    /*
     * 게시글이 있을 때 해당 게시글에 등록했던 답변을 수정 시
     * 내용 혹은 파일을 첨부해 등록할 수 있다.둘 다 null이라면 에러가 발생한다.
     * 수정 성공 시 답변 리스트로 응답이 나간다.
     * 답변이 채택되지 않은 경우, 게시글이 정상 상태인 경우에만 수정 가능하다.
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
        Answer answer = Answer.builder().user(savedUser).content("유효한 15자 이상의 기나긴 내용입니다 아하하").build();
        savedUser.getAnswers().add(answer);
        answerRepository.save(answer);
        this.savedAnswer = answerRepository.findTopByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());
        this.savedFile = fileRepository.findFileByLocalPath("1").orElseThrow(() -> new AssertionError());
        this.accessToken = jwtTokenUtil.createAccessToken(EMAIL1, savedUser.getId(), ROLE_USER_LIST, NICKNAME1);
        articleRepository.save(Article.builder().user(savedUser).category(info).articleStatus(ArticleStatus.POSTING).isClosed(false).build());
        this.validArticle = articleRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new AssertionError());
    }

    @Test
    @DisplayName("수정요청이 null인 경우 400 반환")
    void uppdateAnswer_failed_1() throws Exception {
        //given
        AnswerDto.RequestUpdateAnswer request = AnswerDto.RequestUpdateAnswer.builder().content(null)
                .fileIdList(new ArrayList<>())
                .build();

        String json = objectMapper.writeValueAsString(request);
        //when
        ResultActions perform = mockMvc.perform(patch("/articles/{article-id}/answers/{answer-id}", validArticle.getId(), savedAnswer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, accessToken)
        );
        //then
        perform.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("정상적인 상태에서 답변은 성공적으로 수정 후 반환, 200 ")
    void postAnswer_success_1() throws Exception {
        //given
        File file1 = fileRepository.findFileByLocalPath("1").orElseThrow(() -> new AssertionError());
        File file2 = fileRepository.findFileByLocalPath("2").orElseThrow(() -> new AssertionError());

        AnswerDto.RequestUpdateAnswer request = AnswerDto.RequestUpdateAnswer.builder().content("수정할 내용입니다. 변경 부탁드립니다! 15자 이상 아니어도 됩니다.")
                .fileIdList(List.of(FileDto.RequestFileWithId.builder().fileId(file1.getId()).build(),
                        FileDto.RequestFileWithId.builder().fileId(file2.getId()).build()))
                .build();

        String json = objectMapper.writeValueAsString(request);
        Avatar avatar = Avatar.builder().remotePath("remotePath")
                .originalFilename("fileName")
                .build();
        em.persist(avatar);
        Category qna = Category.builder().name(CategoryName.QNA).build();
        em.persist(qna);
        User user = User.builder().nickname("nickname").grade(Grade.BRONZE).avatar(avatar).build();

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

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, user.getId(), ROLE_USER_LIST, NICKNAME1);
        //when
        ResultActions perform = mockMvc.perform(patch("/articles/{article-id}/answers/{answer-id}", article.getId(), answer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, accessToken)
        );
        //then
        perform.andExpect(status().isOk())
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
                .andExpect(jsonPath("$.pageInfo.sort.sorted").value(true));

    }

}
