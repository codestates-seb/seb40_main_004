package com.morakmorak.morak_back_end.Integration.article.patch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.dto.ArticleDto;
import com.morakmorak.morak_back_end.dto.FileDto;
import com.morakmorak.morak_back_end.dto.TagDto;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.ArticleStatus;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.entity.enums.TagName;
import com.morakmorak.morak_back_end.repository.CategoryRepository;
import com.morakmorak.morak_back_end.repository.FileRepository;
import com.morakmorak.morak_back_end.repository.TagRepository;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
import com.morakmorak.morak_back_end.repository.article.ArticleTagRepository;
import com.morakmorak.morak_back_end.repository.redis.RedisRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.ArticleService;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
public class ArticleUpdateTest {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @PersistenceContext
    EntityManager em;

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
    ArticleTagRepository articleTagRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArticleRepository articleRepository;


    @BeforeEach
    public void originallySavedElements() throws Exception {
        //needs save

        User user = User.builder().email(EMAIL1).name(NAME1).build();
        User saveUser = userRepository.save(user);

        File file1 = fileRepository.save(File.builder().localPath("1").build());

        File file2 = fileRepository.save(File.builder().localPath("2").build());
        File file3 = fileRepository.save(File.builder().localPath("11").build());

        File file4 = fileRepository.save(File.builder().localPath("22").build());

        Category category = categoryRepository.save(Category.builder().name(CategoryName.INFO).build());

        tagRepository.save(Tag.builder().name(TagName.C).build());

        Tag tag = tagRepository.save(Tag.builder().name(TagName.JAVA).build());

        ArrayList<File> files = new ArrayList<>();
        files.add(file3);
        files.add(file4);


        Article article = Article.builder().title("안녕하세요 타이틀입니다.  잘 부탁드립니다. 타이틀은 신경씁니다.")
                .content("콘텐트입니다. 잘부탁드립니다.")
                .thumbnail(2L)
                .category(category)
                .files(files)
                .user(saveUser)
                .build();


        ArticleTag articleTag = ArticleTag.builder().article(article).tag(tag).build();
        article.getArticleTags().add(articleTag);
        articleRepository.save(article);
        articleTagRepository.save(articleTag);
    }

    @Test
    @DisplayName("게시글 수정시 유효성 검증에 통과하고 json을 올바르게 작성했을 경우 200코드를 반환한다.")
    public void update_suc() throws Exception {
        //given
        Tag tag = tagRepository.findTagByName(TagName.C).orElseThrow();
        File dbFile1 = fileRepository.findFileByLocalPath("11").orElseThrow();
        File dbFile2 = fileRepository.findFileByLocalPath("22").orElseThrow();
        Article article = articleRepository.findArticleByContent("콘텐트입니다. 잘부탁드립니다.").orElseThrow(() -> new RuntimeException("뭐지?"));

        ArticleDto.RequestUpdateArticle requestUpdateArticle = ArticleDto.RequestUpdateArticle.builder()
                .title("안녕하세요 새로운 타이틀입니다. 수정부탁드립니다. 타이틀은 신경씁니다.").content("콘텐트입니다. 잘부탁드립니다.")
                .tags(List.of(TagDto.SimpleTag.builder()
                        .tagId(tag.getId()).name(TagName.C).build()))
                .fileId(List.of(FileDto.RequestFileWithId.builder()
                        .fileId(dbFile1.getId()).build(), FileDto.RequestFileWithId.builder()
                        .fileId(dbFile2.getId()).build()))
                .thumbnail(5555L)

                .build();

        String content = objectMapper.writeValueAsString(requestUpdateArticle);
        //id
        Long id = userRepository.findUserByEmail(EMAIL1).orElseThrow().getId();

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, id, ROLE_USER_LIST,NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(
                patch("/articles/" + article.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JWT_HEADER, accessToken)
                        .content(content)
        );

        //then

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(article.getId()));

    }


    @Test
    @DisplayName("게시글 수정 시 삭제처리된 게시글의 경우 403 Forbidden을 보낸다.")
    public void findDetailArticle_failed() throws Exception {
        em.flush();
        em.clear();
        //given
        Article dbArticle = Article.builder()
                .articleStatus(ArticleStatus.REMOVED)
                .build();
        em.persist(dbArticle);

        ArticleDto.RequestUpdateArticle requestUpdateArticle = ArticleDto.RequestUpdateArticle.builder()
                .title("안녕하세요 새로운 타이틀입니다. 수정부탁드립니다. 타이틀은 신경씁니다.").content("콘텐트입니다. 잘부탁드립니다.")
                .thumbnail(5555L)
                .build();

        String content = objectMapper.writeValueAsString(requestUpdateArticle);

        Avatar avatar = Avatar.builder().originalFilename("filename").remotePath("remotePath").build();
        User dbUser = User.builder().email("test@naver.com").nickname("nickname").grade(Grade.BRONZE).avatar(avatar).build();

        em.persist(avatar);
        em.persist(dbUser);

        String accessToken = jwtTokenUtil.createAccessToken(dbUser.getEmail(), dbUser.getId(), ROLE_USER_LIST, NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(
                get("/articles/{article-id}" , dbArticle.getId())
                        .header("User-Agent", "Mozilla 5.0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JWT_HEADER, accessToken)
                        .content(content)
        );

        //then
        perform.andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("게시글 수정시 존재하지 않는 파일을 작성할 경우 FILE_NOT_FOUND 예외를 던지고 404 에러코드를 반환한다. ")
    public void update_fail1() throws Exception{
        //given
        Tag tag = tagRepository.findTagByName(TagName.C).orElseThrow();
        Article article = articleRepository.findArticleByContent("콘텐트입니다. 잘부탁드립니다.").orElseThrow(() -> new RuntimeException("뭐지?"));

        ArticleDto.RequestUpdateArticle requestUpdateArticle = ArticleDto.RequestUpdateArticle.builder()
                .title("안녕하세요 새로운 타이틀입니다. 수정부탁드립니다. 타이틀은 신경씁니다.").content("콘텐트입니다. 잘부탁드립니다.")
                .tags(List.of(TagDto.SimpleTag.builder()
                        .tagId(tag.getId()).name(TagName.C).build()))
                .fileId(List.of(FileDto.RequestFileWithId.builder()
                        .fileId(1212L).build(), FileDto.RequestFileWithId.builder()
                        .fileId(12312L).build()))
                .thumbnail(5555L)

                .build();

        String content = objectMapper.writeValueAsString(requestUpdateArticle);
        //id
        Long id = userRepository.findUserByEmail(EMAIL1).orElseThrow().getId();

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, id, ROLE_USER_LIST,NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(
                patch("/articles/"+article.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JWT_HEADER, accessToken)
                        .content(content)
        );

        //then
        perform.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("게시글 수정시 존재하지 않는 태그를 작성할 경우 TAG_NOT_FOUND 예외를 던지고 404 에러코드를 반환한다. ")
    public void update_fail2() throws Exception{
        //given
        File dbFile1 = fileRepository.findFileByLocalPath("11").orElseThrow();
        File dbFile2 = fileRepository.findFileByLocalPath("22").orElseThrow();
        Article article = articleRepository.findArticleByContent("콘텐트입니다. 잘부탁드립니다.").orElseThrow(() -> new RuntimeException("뭐지?"));

        ArticleDto.RequestUpdateArticle requestUpdateArticle = ArticleDto.RequestUpdateArticle.builder()
                .title("안녕하세요 새로운 타이틀입니다. 수정부탁드립니다. 타이틀은 신경씁니다.").content("콘텐트입니다. 잘부탁드립니다.")
                .tags(List.of(TagDto.SimpleTag.builder()
                        .tagId(555L).build()))
                .fileId(List.of(FileDto.RequestFileWithId.builder()
                        .fileId(dbFile1.getId()).build(), FileDto.RequestFileWithId.builder()
                        .fileId(dbFile2.getId()).build()))
                .thumbnail(5555L)

                .build();

        String content = objectMapper.writeValueAsString(requestUpdateArticle);
        //id
        Long id = userRepository.findUserByEmail(EMAIL1).orElseThrow().getId();

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, id, ROLE_USER_LIST,NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(
                patch("/articles/" +article.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JWT_HEADER, accessToken)
                        .content(content)
        );

        //then
        perform.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("게시글 수정시 유효성 검증에 통과하지 못하면 BAD_REQUEST 예외를 던지고 400 에러코드를 반환한다. ")
    public void update_fail3() throws Exception{
        //given
        Article article = articleRepository.findArticleByContent("콘텐트입니다. 잘부탁드립니다.").orElseThrow(() -> new RuntimeException("뭐지?"));

        ArticleDto.RequestUpdateArticle requestUpdateArticle = ArticleDto.RequestUpdateArticle.builder()


                .build();

        String content = objectMapper.writeValueAsString(requestUpdateArticle);
        //id
        Long id = userRepository.findUserByEmail(EMAIL1).orElseThrow().getId();

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, id, ROLE_USER_LIST,NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(
                patch("/articles/" +article.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JWT_HEADER, accessToken)
                        .content(content)
        );
        //then
        perform.andExpect(status().isBadRequest());
    }
}

