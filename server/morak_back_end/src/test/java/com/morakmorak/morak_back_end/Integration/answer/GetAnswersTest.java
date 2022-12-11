package com.morakmorak.morak_back_end.Integration.answer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.repository.CategoryRepository;
import com.morakmorak.morak_back_end.repository.FileRepository;
import com.morakmorak.morak_back_end.repository.redis.RedisRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.ArticleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
public class GetAnswersTest {

    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired

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



    @Test
    @DisplayName("비로그인유저가 답변 리스트를 조회 성공 시 200이 반환된다.")
    void getAllAnswers_success_1() throws Exception {
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
        em.persist(user);
        List<Answer> list = user.getAnswers();

        //when
        ResultActions perform = mockMvc.perform(
                get("/articles/{article-id}/answers", article.getId())
                        .header("User-Agent", "Mozilla 5.0")
                .param("page","1")
                .param("size","5")
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].answerId").value(answer.getId().intValue()))
                .andExpect(jsonPath("$.data[0].content").exists())
                .andExpect(jsonPath("$.data[0].answerLikeCount").exists())
                .andExpect(jsonPath("$.data[0].isPicked").value(false))
                .andExpect(jsonPath("$.data[0].isLiked").value(false))
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
                .andExpect(jsonPath("$.pageInfo.sort.sorted").value(false));
    }
    @Test
    @DisplayName("로그인 유저의 답변목록 조회 성공 시 200이 반환된다.")
    void getAllForUser_success_1() throws Exception {
        //given
        Category qna = Category.builder().name(CategoryName.QNA).build();
        em.persist(qna);

        Avatar avatar = Avatar.builder().remotePath("remotePath")
                .originalFilename("fileName")
                .build();
        em.persist(avatar);

        User user = User.builder().nickname(NICKNAME1).grade(Grade.BRONZE).avatar(avatar).build();

        Article article = Article.builder().title("테스트 타이틀입니다.")
                .content("콘탠트입니다. 질문을 많이 올려주세요.")
                .category(qna)
                .user(user)
                .build();

        qna.getArticleList().add(article);
        em.persist(article);
        List<AnswerLike> likeList = new ArrayList<>();

        Answer likedAnswer = Answer.builder()
                .content("15글자 이상의 유효한 답변내용입니다.")
                .user(user)
                .isPicked(false)
                .answerLike(likeList)
                .article(article).build();

        AnswerLike like = AnswerLike.builder().answer(likedAnswer).user(user).build();
        likeList.add(like);
        //유저랑 앤서에 저장이 되어야 함
        user.getAnswers().add(likedAnswer);
        likedAnswer.getAnswerLike().add(like);
        user.getAnswerLikes().add(like);

        Answer answerNotLiked = Answer.builder()
                .content("좋아요하지 않은 답변입니다.")
                .user(user)
                .isPicked(false)
                .article(article).build();


        em.persist(like);

        em.persist(answerNotLiked);
        String accessToken =jwtTokenUtil.createAccessToken(EMAIL1, user.getId(), ROLE_USER_LIST, NICKNAME1);
        //when
        ResultActions perform = mockMvc.perform(
                get("/articles/{article-id}/answers", article.getId())
                        .header("User-Agent", "Mozilla 5.0")
                        .param("page", "1")
                        .param("size", "5")
                        .header(JWT_HEADER, accessToken)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].answerId").value(answerNotLiked.getId().intValue()))
                .andExpect(jsonPath("$.data[0].content").exists())
                .andExpect(jsonPath("$.data[0].answerLikeCount").exists())
                .andExpect(jsonPath("$.data[0].isPicked").value(false))
                .andExpect(jsonPath("$.data[0].isLiked").value(false))
                .andExpect(jsonPath("$.data[0].commentCount").value(0))
                .andExpect(jsonPath("$.data[0].createdAt").exists())
                .andExpect(jsonPath("$.data[0].userInfo.userId").value(user.getId().intValue()))
                .andExpect(jsonPath("$.data[0].userInfo.nickname").exists())
                .andExpect(jsonPath("$.data[0].userInfo.grade").exists())
                .andExpect(jsonPath("$.data[0].avatar.avatarId").value(avatar.getId().intValue()))
                .andExpect(jsonPath("$.data[0].avatar.filename").exists())
                .andExpect(jsonPath("$.data[0].avatar.remotePath").exists())
                .andExpect(jsonPath("$.data[1].answerId").value(likedAnswer.getId().intValue()))
                .andExpect(jsonPath("$.data[1].content").exists())
                .andExpect(jsonPath("$.data[1].answerLikeCount").exists())
                .andExpect(jsonPath("$.data[1].isPicked").value(false))
                .andExpect(jsonPath("$.data[1].isLiked").value(true))
                .andExpect(jsonPath("$.data[1].commentCount").value(0))
                .andExpect(jsonPath("$.data[1].createdAt").exists())
                .andExpect(jsonPath("$.data[1].userInfo.userId").value(user.getId().intValue()))
                .andExpect(jsonPath("$.data[1].userInfo.nickname").exists())
                .andExpect(jsonPath("$.data[1].userInfo.grade").exists())
                .andExpect(jsonPath("$.data[1].avatar.avatarId").value(avatar.getId().intValue()))
                .andExpect(jsonPath("$.data[1].avatar.filename").exists())
                .andExpect(jsonPath("$.data[1].avatar.remotePath").exists())
                .andExpect(jsonPath("$.pageInfo.page").value(1))
                .andExpect(jsonPath("$.pageInfo.size").value(5))
                .andExpect(jsonPath("$.pageInfo.totalElements").value(2))
                .andExpect(jsonPath("$.pageInfo.totalPages").value(1))
                .andExpect(jsonPath("$.pageInfo.sort.sorted").value(false));
    }
}
