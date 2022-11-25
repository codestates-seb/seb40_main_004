package com.morakmorak.morak_back_end.Integration.answer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.repository.BookmarkRepository;
import com.morakmorak.morak_back_end.repository.CategoryRepository;
import com.morakmorak.morak_back_end.repository.FileRepository;
import com.morakmorak.morak_back_end.repository.answer.AnswerRepository;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
import com.morakmorak.morak_back_end.repository.redis.RedisRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.ArticleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
@Rollback(value = true)
public class GetAnswersTest {

    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @PersistenceContext
    EntityManager em;

    @Autowired
    RedisRepository<String> mailAuthRedisRepository;

    @Autowired
    ArticleService articleService;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    CategoryRepository categoryRepository;





    /*
     * 게시글이 있을 때
     * 해당 게시글에 여러 답변이 등록됨
     * 이때 게시글을 조회한다면,
     * 유저에게는 북마크 표시된 답변 리스트가
     * 비로그인유저에게는 북마크 표시 데이터 없는 답변 리스트가 나간다.
     *
     */
    @Test
    @DisplayName("답변 리스트를 조회 성공 시 200이 반환된다.")
    void getAllForUser_success_1() throws Exception {
        //given
        Category qna = Category.builder().name(CategoryName.QNA).build();
        em.persist(qna);

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

        Answer answer = Answer.builder()
                .content("15글자 이상의 유효한 답변내용입니다.")
                .user(user)
                .isPicked(false)
                .article(article).build();
        user.getAnswers().add(answer);
        article.getAnswers().add(answer);
        em.persist(answer);

        //when
        ResultActions perform = mockMvc.perform(
                get("/articles/{article-id}/answers", article.getId())
                .param("page","1")
                .param("size","5")
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].answerId").value(answer.getId().intValue()))
                .andExpect(jsonPath("$.data[0].content").exists())
                .andExpect(jsonPath("$.data[0].answerLikeCount").exists())
                .andExpect(jsonPath("$.data[0].isPicked").value(false))
                .andExpect(jsonPath("$.data[0].commentCount").value(0))
                .andExpect(jsonPath("$.data[0].createdAt").exists())
                .andExpect(jsonPath("$.data[0].userInfo.userId").value(user.getId().intValue()))
                .andExpect(jsonPath("$.data[0].userInfo.nickname").exists())
                .andExpect(jsonPath("$.data[0].userInfo.grade").exists())
                .andExpect(jsonPath("$.data[0].avatar.avatarId").value(avatar.getId().intValue()))
                .andExpect(jsonPath("$.data[0].avatar.filename").exists())
                .andExpect(jsonPath("$.data[0].avatar.remotePath").exists())
                .andExpect(jsonPath("$.pageInfo.page").value(1))
                .andExpect(jsonPath("$.pageInfo.size").value(5))
                .andExpect(jsonPath("$.pageInfo.totalElements").value(1))
                .andExpect(jsonPath("$.pageInfo.totalPages").value(1))
                .andExpect(jsonPath("$.pageInfo.sort.sorted").value(true));
    }
}
