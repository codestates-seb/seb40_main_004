package com.morakmorak.morak_back_end.Integration.article.delete;

import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.Category;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ROLE_USER_LIST;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
public class ArticleDeleteTest {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;

    @Autowired
    ArticleRepository articleRepository;

    @BeforeEach
    public void beforeEach() {
        User user = User.builder().email(EMAIL1).name(NAME1).build();
        User saveUser = userRepository.save(user);

        Article article = Article.builder()
                .title("삭제할 title입니다. 삭제가 되는지 확인해 보도록  하겠습니다.")
                .content("삭제할 content입니다. 삭제가 되는지 확인해 보도록  하겠습니다. ")
                .user(saveUser)
                .category(new Category(CategoryName.QNA))
                .build();

        articleRepository.save(article);

    }
    @Test
    @DisplayName("게시글 삭제시 해당 게시글의 상태를 REMOVED로 변경하고 NO_CONTENT를 반환한다. ")
    public void delete_suc() throws Exception{
        //given
        Article article = articleRepository.findArticleByContent("삭제할 content입니다. 삭제가 되는지 확인해 보도록  하겠습니다. ").orElseThrow();

        Long id = userRepository.findUserByEmail(EMAIL1).orElseThrow().getId();

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, id, ROLE_USER_LIST,NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/articles/" + article.getId())

                        .header(JWT_HEADER, accessToken)
        );
        //then
        perform.andExpect(status().isNoContent());
     }

    @Test
    @DisplayName("게시글 삭제시 해당 게시글의 주인이 아닌 다른이가 삭제를한다면 INVALID_USER 예외를 던지고 401에러코드를 반환한다. ")
    public void delete_fail() throws Exception{
        //given
        Article article = articleRepository.findArticleByContent("삭제할 content입니다. 삭제가 되는지 확인해 보도록  하겠습니다. ").orElseThrow();

        Long id = userRepository.findUserByEmail(EMAIL1).orElseThrow().getId();

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL2, ID2, ROLE_USER_LIST,NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/articles/" + article.getId())

                        .header(JWT_HEADER, accessToken)
        );
        //then
        perform.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("게시글 삭제시 해당 게시글이 존재하지 않는다면 ARTICLE_NOT_FOUND 예외를 던지고 404에러코드를 반환한다. ")
    public void delete_fail2() throws Exception{
        //given
        Article article = articleRepository.findArticleByContent("삭제할 content입니다. 삭제가 되는지 확인해 보도록  하겠습니다. ").orElseThrow();
        articleRepository.delete(article);

        Long id = userRepository.findUserByEmail(EMAIL1).orElseThrow().getId();

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, id, ROLE_USER_LIST, NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/articles/" + article.getId())

                        .header(JWT_HEADER, accessToken)
        );
        //then
        perform.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("게시글 삭제시 글 작성자의 포인트가 감소한다. ")
    public void delete_suc1() throws Exception{
        //given
        Article article = articleRepository.findArticleByContent("삭제할 content입니다. 삭제가 되는지 확인해 보도록  하겠습니다. ").orElseThrow();

        Long id = userRepository.findUserByEmail(EMAIL1).orElseThrow().getId();
        User user = userRepository.findById(id).get();
        Integer beforePoint = user.getPoint();

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, id, ROLE_USER_LIST,NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/articles/" + article.getId())

                        .header(JWT_HEADER, accessToken)
        );
        Integer afterPoint = user.getPoint();
        //then
        Assertions.assertThat(beforePoint > afterPoint).isTrue();
    }
}
