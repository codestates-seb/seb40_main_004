package com.morakmorak.morak_back_end.Integration.article.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.dto.ArticleDto;
import com.morakmorak.morak_back_end.dto.FileDto;
import com.morakmorak.morak_back_end.dto.TagDto;
import com.morakmorak.morak_back_end.entity.Category;
import com.morakmorak.morak_back_end.entity.File;
import com.morakmorak.morak_back_end.entity.Tag;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.entity.enums.TagName;
import com.morakmorak.morak_back_end.repository.CategoryRepository;
import com.morakmorak.morak_back_end.repository.FileRepository;
import com.morakmorak.morak_back_end.repository.TagRepository;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
import com.morakmorak.morak_back_end.repository.redis.RedisRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.ArticleService;
import org.assertj.core.api.Assertions;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
public class ArticleUploadTest {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;


    @BeforeEach
    public void originallySavedElements() throws Exception {
        //needs save

        User user = User.builder().email(EMAIL1).name(NAME1).build();
        userRepository.save(user);

        File file1 = fileRepository.save(File.builder().localPath("1").build());

        File file2 = fileRepository.save(File.builder().localPath("2").build());

        Category category = categoryRepository.save(Category.builder().name(CategoryName.INFO).build());

        Tag tag = tagRepository.save(Tag.builder().name(TagName.JAVA).build());

    }

    @Test
    @DisplayName("게시글 작성시 title이 15자 이상이고 content가 5자 이상이며, 정보글을 작성할때 성공하면 201코드와 DB 시퀀스를 반환한다.")
    public void upload_suc_1() throws Exception {
        //given
        Tag tag = tagRepository.findTagByName(TagName.JAVA).orElseThrow();
        Category category = categoryRepository.findCategoryByName(CategoryName.INFO).orElseThrow();
        File file1 = fileRepository.findFileByLocalPath("1").orElseThrow();
        File file2 = fileRepository.findFileByLocalPath("2").orElseThrow();


        ArticleDto.RequestUploadArticle requestUploadArticle = ArticleDto.RequestUploadArticle.builder()
                .title("안녕하세요 타이틀입니다. 잘 부탁드립니다. 타이틀은 신경씁니다.").content("콘텐트입니다. 잘부탁드립니다.")
                .tags(List.of(TagDto.SimpleTag.builder()
                        .tagId(tag.getId()).name(TagName.JAVA).build()))
                .fileId(List.of(FileDto.RequestFileWithId.builder()
                        .fileId(file1.getId()).build(), FileDto.RequestFileWithId.builder()
                        .fileId(file2.getId()).build()))
                .category(category.getName())
                .thumbnail(1L)
                .build();

        String content = objectMapper.writeValueAsString(requestUploadArticle);
        //id
        Long id = userRepository.findUserByEmail(EMAIL1).orElseThrow().getId();

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, id, ROLE_USER_LIST, NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(
                post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JWT_HEADER, accessToken)
                        .content(content)
        );
    }


    @Test
    @DisplayName("게시글 작성시 존재하지 않는 파일를 작성할 경우 FILE_NOT_FOUND 예외를 던지고 404 에러코드를 반환한다. ")
    public void upload_fail2() throws Exception {
        //given
        Tag tag = tagRepository.findTagByName(TagName.JAVA).orElseThrow();


        ArticleDto.RequestUploadArticle requestUploadArticle = ArticleDto.RequestUploadArticle.builder()
                .title("안녕하세요 타이틀입니다. 잘 부탁드립니다. 타이틀은 신경씁니다.").content("콘텐트입니다. 잘부탁드립니다.")
                .fileId(List.of(FileDto.RequestFileWithId.builder()
                        .fileId(100L).build(), FileDto.RequestFileWithId.builder()
                        .fileId(111L).build()))
                .tags(List.of(TagDto.SimpleTag.builder().tagId(tag.getId()).name(TagName.JAVA).build()))
                .category(CategoryName.INFO)
                .thumbnail(1L)
                .build();
        String content = objectMapper.writeValueAsString(requestUploadArticle);
        //id
        Long id = userRepository.findUserByEmail(EMAIL1).orElseThrow().getId();

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, id, ROLE_USER_LIST, NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(
                post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JWT_HEADER, accessToken)
                        .content(content)
        );

        //then
        perform.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("게시글 작성시 존재하지 않는 태그를 작성할 경우 TAG_NOT_FOUND 예외를 던지고 404 에러코를 반환한다. ")
    public void upload_fail1() throws Exception {
        //given
        File file1 = fileRepository.findFileByLocalPath("1").orElseThrow();
        File file2 = fileRepository.findFileByLocalPath("2").orElseThrow();


        ArticleDto.RequestUploadArticle requestUploadArticle = ArticleDto.RequestUploadArticle.builder()
                .title("안녕하세요 타이틀입니다. 잘 부탁드립니다. 타이틀은 신경씁니다.").content("콘텐트입니다. 잘부탁드립니다.")
                .tags(List.of(TagDto.SimpleTag.builder()
                        .tagId(2L).build()))
                .fileId(List.of(FileDto.RequestFileWithId.builder()
                        .fileId(file1.getId()).build(), FileDto.RequestFileWithId.builder()
                        .fileId(file2.getId()).build()))
                .category(CategoryName.INFO)
                .thumbnail(1L)
                .build();
        String content = objectMapper.writeValueAsString(requestUploadArticle);
        //id
        Long id = userRepository.findUserByEmail(EMAIL1).orElseThrow().getId();

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, id, ROLE_USER_LIST, NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(
                post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JWT_HEADER, accessToken)
                        .content(content)
        );

        //then
        perform.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("게시글 작성시 작성한 유저의 포인트가 증가한다.")
    public void upload_suc2() throws Exception {
        //given
        Tag tag = tagRepository.findTagByName(TagName.JAVA).orElseThrow();
        Category category = categoryRepository.findCategoryByName(CategoryName.INFO).orElseThrow();
        File file1 = fileRepository.findFileByLocalPath("1").orElseThrow();
        File file2 = fileRepository.findFileByLocalPath("2").orElseThrow();


        ArticleDto.RequestUploadArticle requestUploadArticle = ArticleDto.RequestUploadArticle.builder()
                .title("안녕하세요 타이틀입니다. 잘 부탁드립니다. 타이틀은 신경씁니다.").content("콘텐트입니다. 잘부탁드립니다.")
                .tags(List.of(TagDto.SimpleTag.builder()
                        .tagId(tag.getId()).name(TagName.JAVA).build()))
                .fileId(List.of(FileDto.RequestFileWithId.builder()
                        .fileId(file1.getId()).build(), FileDto.RequestFileWithId.builder()
                        .fileId(file2.getId()).build()))
                .category(category.getName())
                .thumbnail(1L)
                .build();

        String content = objectMapper.writeValueAsString(requestUploadArticle);
        //id
        Long id = userRepository.findUserByEmail(EMAIL1).orElseThrow().getId();
        User user = userRepository.findById(id).get();
        Integer pointBefore = user.getPoint();

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, id, ROLE_USER_LIST, NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(
                post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JWT_HEADER, accessToken)
                        .content(content)
        );

        //then
        Integer pointAfter = user.getPoint();
        Assertions.assertThat(pointBefore < pointAfter).isTrue();
    }
}
