package com.morakmorak.morak_back_end.Integration.bookmark;

import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.Bookmark;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
import com.morakmorak.morak_back_end.repository.BookmarkRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.BookmarkService;
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

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ROLE_USER_LIST;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc

public class BookmarkTest {

    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    BookmarkService bookmarkService;

    @Autowired
    MockMvc mockMvc;



    @Autowired
    UserRepository userRepository;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    BookmarkRepository bookmarkRepository;

    User savedUser1;
    User savedUser2;
    String accessTokenForUser1;
    String accessTokenForUser2;
    Article savedArticle1;
    Article notScrappedArticle;
    Bookmark savedBookmark1;


    @BeforeEach
    public void setUp() {
        User dbUser1 = User.builder()
                .email(EMAIL1)
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .build();
        userRepository.save(dbUser1);
        this.savedUser1 = userRepository.findUserByEmail(EMAIL1).orElseThrow(() -> new AssertionError());
        this.accessTokenForUser1 = jwtTokenUtil.createAccessToken(EMAIL1, savedUser1.getId(), ROLE_USER_LIST, NICKNAME1);

        Article dbArticle1 = Article.builder().user(savedUser1).build();
        articleRepository.save(dbArticle1);
        this.savedArticle1 = articleRepository.findByUserId(savedUser1.getId()).orElseThrow(() -> new AssertionError());

        Bookmark dbBookmark1 = Bookmark.builder().user(savedUser1).article(savedArticle1).build();
        savedUser1.getBookmarks().add(dbBookmark1);
        bookmarkRepository.save(dbBookmark1);
        bookmarkRepository.save(dbBookmark1);
        this.savedBookmark1 = bookmarkRepository.findByArticleId(savedArticle1.getId()).orElseThrow(() -> new AssertionError());

        User dbUser2 = User.builder()
                .email(EMAIL2)
                .nickname(NICKNAME2)
                .password(PASSWORD2)
                .build();
        User save=userRepository.save(dbUser2);

        this.savedUser2 = userRepository.findUserByEmail(EMAIL2).orElseThrow(() -> new AssertionError());
        this.accessTokenForUser2 = jwtTokenUtil.createAccessToken(EMAIL2, savedUser2.getId(), ROLE_USER_LIST, NICKNAME1);
        Article dbArticle2 = Article.builder().user(savedUser2).build();
        savedUser2.getArticles().add(dbArticle2);
        articleRepository.save(dbArticle2);
        this.notScrappedArticle = articleRepository.findByUserId(savedUser2.getId()).orElseThrow(() -> new AssertionError());
    }

    @Test
    @DisplayName("북마크 등록 시 탈퇴한 유저인 경우 401가 반환된다.")
    public void postBookmark_fail_1() throws Exception {
        ResultActions perform = mockMvc
                .perform(post("/articles/{article-id}/bookmarks", savedArticle1.getId())
                        .contentType(MediaType.APPLICATION_JSON));
        perform.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("북마크 등록 시 기존에 북마크되지 않았던 경우 북마크가 생성된다.")
    public void postBookmark_success_1() throws Exception {

        ResultActions perform = mockMvc
                .perform(post("/articles/{article-id}/bookmark", notScrappedArticle.getId())
                        .header(JWT_HEADER,accessTokenForUser2));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").exists())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.scrappedByThisUser").value(true))
                .andExpect(jsonPath("$.createdAt").isString())
                .andExpect(jsonPath("$.lastModifiedAt").isString());
    }


}
