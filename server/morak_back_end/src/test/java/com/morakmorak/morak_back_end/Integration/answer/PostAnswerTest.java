package com.morakmorak.morak_back_end.Integration.answer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.dto.AnswerDto;
import com.morakmorak.morak_back_end.dto.FileDto;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.ArticleStatus;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.repository.ArticleRepository;
import com.morakmorak.morak_back_end.repository.FileRepository;
import com.morakmorak.morak_back_end.repository.UserRepository;
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

import java.util.List;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ROLE_USER_LIST;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        this.accessToken = jwtTokenUtil.createAccessToken(EMAIL1, savedUser.getId(), ROLE_USER_LIST);

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
        //when
        ResultActions perform = mockMvc.perform(post("/articles/{article-id}/answers/", validArticle.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, accessToken)
        );
        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.userInfo.userId").exists())
                .andExpect(jsonPath("$.userInfo.nickname").exists())
                .andExpect(jsonPath("$.avatar.avatarId").exists())
                .andExpect(jsonPath("$.avatar.filename").exists())
                .andExpect(jsonPath("$.avatar.remotePath").exists())
                .andExpect(jsonPath("$.answerId").exists())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.lastModifiedAt").exists());
    }

}
