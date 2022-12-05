package com.morakmorak.morak_back_end.Integration.article.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.dto.ArticleDto;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.Report;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.ReportReason;
import com.morakmorak.morak_back_end.repository.*;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.EMAIL1;
import static com.morakmorak.morak_back_end.util.TestConstants.NICKNAME1;
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
public class ReportArticleTest {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

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
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    EntityManager em;

    @Autowired
    ReportRepository reportRepository;

        @Test
        @DisplayName("로그인한 회원이 실제 존재하는 게시글을 신고할경우 201코드와 reportId를 던진다. ")
        public void reportArticle_suc1() throws Exception {
        //given

            Article article = Article.builder().title("제목입니다.제목입니다.제목입니다.제목입니다.").content("본문 입니다.본문 입니다.본문 입니다.")
                    .build();
            em.persist(article);
            User user = User.builder().email(EMAIL1).nickname("nickname").build();
            em.persist(user);

            Article dbArticle = articleRepository.findArticleByContent("본문 입니다.본문 입니다.본문 입니다.").orElseThrow();
            User dbUser = userRepository.findUserByEmail(EMAIL1).orElseThrow();

            String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, dbUser.getId(), ROLE_USER_LIST, NICKNAME1);

            ArticleDto.RequestReportArticle requestReportArticle =
                    ArticleDto.RequestReportArticle.builder().reason(ReportReason.BAD_LANGUAGE).content("이유").build();

            String content = objectMapper.writeValueAsString(requestReportArticle);

            //when
            ResultActions perform = mockMvc.perform(
                    post("/articles/{article-id}/reports", dbArticle.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(JWT_HEADER, accessToken)
                            .content(content)
            );

            //then
            Report dbReport = reportRepository.findReportByContent("이유").orElseThrow();
            perform.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.reportId").value(dbReport.getId()));
        }

    @Test
    @DisplayName("로그인한 회원이 실제 존재지 않는 게시글을 신고할경우 404코드를 던진다. ")
    public void reportArticle_fail1() throws Exception {
        //given
        User user = User.builder().email(EMAIL1).nickname("nickname").build();
        em.persist(user);

        User dbUser = userRepository.findUserByEmail(EMAIL1).orElseThrow();

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, dbUser.getId(), ROLE_USER_LIST, NICKNAME1);

        ArticleDto.RequestReportArticle requestReportArticle =
                ArticleDto.RequestReportArticle.builder().reason(ReportReason.BAD_LANGUAGE).content("이유").build();

        String content = objectMapper.writeValueAsString(requestReportArticle);

        //when
        ResultActions perform = mockMvc.perform(
                post("/articles/{article-id}/reports", 500L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JWT_HEADER, accessToken)
                        .content(content)
        );
        //then
        perform.andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("로그인하지않은 회원이 실제 존재하는 게시글을 신고할경우 401. ")
    public void reportArticle_suc2() throws Exception {
        //given
        Article article = Article.builder().title("제목입니다.제목입니다.제목입니다.제목입니다.").content("본문 입니다.본문 입니다.본문 입니다.")
                .build();
        em.persist(article);
        User user = User.builder().email(EMAIL1).nickname("nickname").build();
        em.persist(user);

        Article dbArticle = articleRepository.findArticleByContent("본문 입니다.본문 입니다.본문 입니다.").orElseThrow();

        ArticleDto.RequestReportArticle requestReportArticle =
                ArticleDto.RequestReportArticle.builder().reason(ReportReason.BAD_LANGUAGE).content("이유").build();

        String content = objectMapper.writeValueAsString(requestReportArticle);

        //when
        ResultActions perform = mockMvc.perform(
                post("/articles/{article-id}/reports", dbArticle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        perform.andExpect(status().isUnauthorized());
    }
}