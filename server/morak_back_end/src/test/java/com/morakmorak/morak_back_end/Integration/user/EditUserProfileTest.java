package com.morakmorak.morak_back_end.Integration.user;

import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.JobType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

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

public class EditUserProfileTest extends UserTest {
    @Test
    @DisplayName("유효성 검사에 실패할 경우 400 badRequest")
    public void editUserProfile_failed1() throws Exception {
        //given
        String token = jwtTokenUtil.createAccessToken(EMAIL1, ID1, ROLE_USER_LIST, NICKNAME1);
        UserDto.SimpleEditProfile request = UserDto.SimpleEditProfile.builder()
                .infoMessage(CONTENT1)
                .github(GITHUB_URL)
                .jobType(JobType.DEVELOPER)
                .nickname(INVALID_NICKNAME)
                .blog(TISTORY_URL)
                .build();

        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc.perform(patch("/users/profiles")
                .header(JWT_HEADER, token)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("닉네임이 이미 존재할 경우 409 Conflict")
    public void editUserProfile_failed2() throws Exception {
        //given
        User dbUser = User.builder().build();
        User saved = userRepository.save(dbUser);
        String token = jwtTokenUtil.createAccessToken(EMAIL1, saved.getId(), ROLE_USER_LIST, NICKNAME1);

        entityManager.persist(User.builder().nickname(NICKNAME1).build());

        UserDto.SimpleEditProfile request = UserDto.SimpleEditProfile.builder()
                .infoMessage(CONTENT1)
                .github(GITHUB_URL)
                .jobType(JobType.DEVELOPER)
                .nickname(NICKNAME1)
                .blog(TISTORY_URL)
                .build();

        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc.perform(patch("/users/profiles")
                .header(JWT_HEADER, token)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isConflict());
    }

    @Test
    @DisplayName("아이디가 존재하지 않을 경우 404 NOTFOUND")
    public void editUserProfile_failed3() throws Exception {
        //given
        String token = jwtTokenUtil.createAccessToken(EMAIL1, ID1, ROLE_USER_LIST, NICKNAME1);
        UserDto.SimpleEditProfile request = UserDto.SimpleEditProfile.builder()
                .infoMessage(CONTENT1)
                .github(GITHUB_URL)
                .jobType(JobType.DEVELOPER)
                .nickname(NICKNAME1)
                .blog(TISTORY_URL)
                .build();

        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc.perform(patch("/users/profiles")
                .header(JWT_HEADER, token)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("정상 통과할 경우 유저 정보가 수정되고 200 OK")
    public void editUserProfile_success() throws Exception {
        //given
        User dbUser = User.builder().build();
        User saved = userRepository.save(dbUser);
        String token = jwtTokenUtil.createAccessToken(EMAIL1, saved.getId(), ROLE_USER_LIST, NICKNAME1);
        UserDto.SimpleEditProfile request = UserDto.SimpleEditProfile.builder()
                .infoMessage(CONTENT1)
                .github(GITHUB_URL)
                .jobType(JobType.DEVELOPER)
                .nickname(NICKNAME1)
                .blog(TISTORY_URL)
                .build();

        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc.perform(patch("/users/profiles")
                .header(JWT_HEADER, token)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON));
        User editedUser = entityManager.find(User.class, saved.getId());

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.infoMessage").value(CONTENT1))
                .andExpect(jsonPath("$.github").value(GITHUB_URL))
                .andExpect(jsonPath("$.nickname").value(NICKNAME1))
                .andExpect(jsonPath("$.jobType").value(JobType.DEVELOPER.toString()))
                .andExpect(jsonPath("$.blog").value(TISTORY_URL));

        Assertions.assertThat(editedUser.getInfoMessage()).isEqualTo(CONTENT1);
        Assertions.assertThat(editedUser.getGithub()).isEqualTo(GITHUB_URL);
        Assertions.assertThat(editedUser.getNickname()).isEqualTo(NICKNAME1);
        Assertions.assertThat(editedUser.getJobType()).isEqualTo(JobType.DEVELOPER);
        Assertions.assertThat(editedUser.getBlog()).isEqualTo(TISTORY_URL);
    }
}
