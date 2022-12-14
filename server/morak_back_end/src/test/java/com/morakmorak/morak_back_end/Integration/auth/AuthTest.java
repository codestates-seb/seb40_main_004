package com.morakmorak.morak_back_end.Integration.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.RedisContainerTest;
import com.morakmorak.morak_back_end.dto.AuthDto;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.UserStatus;
import com.morakmorak.morak_back_end.repository.redis.RedisRepositoryImpl;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.auth_user_service.AuthService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static com.morakmorak.morak_back_end.security.util.SecurityConstants.*;
import static com.morakmorak.morak_back_end.security.util.SecurityConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
public class AuthTest extends RedisContainerTest {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthService authService;

    @Autowired
    RedisRepositoryImpl<User> redisRepositoryImpl;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RedisRepositoryImpl<String> mailAuthRedisRepositoryImpl;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    public void init() {
        redisRepositoryImpl.deleteData(AUTH_KEY);
        redisRepositoryImpl.deleteData(INVALID_AUTH_KEY);
    }

    @Test
    @DisplayName("???????????? ??? DTO ????????? ???????????? BAD_REQUEST(400) ????????? ????????????")
    public void test1() throws Exception {
        //given
        AuthDto.RequestJoin request = AuthDto.RequestJoin.builder()
                .email(INVALID_EMAIL)
                .password(INVALID_PASSWORD)
                .nickname(NICKNAME1)
                .build();

        String json = objectMapper.writeValueAsString(request);
        //when
        ResultActions perform = mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("???????????? ??? ????????? ???????????? ?????? ??????????????? Conflict(409)????????? ????????????.")
    public void test2() throws Exception {
        //given
        User dbUser = User.builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        mailAuthRedisRepositoryImpl.saveData(AUTH_KEY, EMAIL1, VALIDITY_PERIOD_OF_THE_AUTHENTICATION_KEY);
        authService.joinUser(dbUser, AUTH_KEY);
        mailAuthRedisRepositoryImpl.saveData(AUTH_KEY, EMAIL1, VALIDITY_PERIOD_OF_THE_AUTHENTICATION_KEY);

        AuthDto.RequestJoin request = AuthDto.RequestJoin.builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .nickname(NICKNAME1)
                .authKey(AUTH_KEY)
                .build();

        String json = objectMapper.writeValueAsString(request);
        //when
        ResultActions perform = mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isConflict());
    }

    @Test
    @DisplayName("???????????? ??? ????????? ???????????? ???????????? ????????? DTO ????????? ???????????? 201????????? DB ???????????? ????????????.")
    public void test3() throws Exception {
        //given
        AuthDto.RequestJoin request = AuthDto.RequestJoin.builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .nickname(NICKNAME1)
                .authKey(AUTH_KEY)
                .build();

        mailAuthRedisRepositoryImpl.saveData(AUTH_KEY, EMAIL1, VALIDITY_PERIOD_OF_THE_AUTHENTICATION_KEY);
        String json = objectMapper.writeValueAsString(request);
        //when
        ResultActions perform = mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isCreated())
                .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("??????????????? ????????? ????????? ???????????? ???????????? 404 NOT FOUND??? ????????????.")
    public void test4() throws Exception {
        //given
        AuthDto.RequestLogin request = AuthDto
                .RequestLogin
                .builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc
                .perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));

        //then
        perform.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("???????????? ????????? ??? ????????? ??????????????? DB??? ?????????????????? ???????????? ???????????? 401 UnAuthorized??? ??????")
    public void test5() throws Exception {
        //given
        User dbUser = User.builder()
                .email(EMAIL1)
                .password(PASSWORD2)
                .build();

        mailAuthRedisRepositoryImpl.saveData(AUTH_KEY, EMAIL1, VALIDITY_PERIOD_OF_THE_AUTHENTICATION_KEY);
        authService.joinUser(dbUser, AUTH_KEY);

        AuthDto.RequestLogin request = AuthDto
                .RequestLogin
                .builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc
                .perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));

        //then
        perform.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("?????????????????? ????????? ???????????? ???????????? ??????????????? ??????????????? 201 Created ??? ????????????/????????? ?????? ??????")
    public void test6() throws Exception {
        //given
        User dbUser = User.builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        mailAuthRedisRepositoryImpl.saveData(AUTH_KEY, EMAIL1, VALIDITY_PERIOD_OF_THE_AUTHENTICATION_KEY);
        authService.joinUser(dbUser, AUTH_KEY);

        AuthDto.RequestLogin request = AuthDto
                .RequestLogin
                .builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc
                .perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));

        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").isString());
    }

    @Test
    @DisplayName("??????????????? ???????????? ??? ??????????????? ????????? ???????????? ????????? 404 NOT FOUND ??????")
    public void test7() throws Exception {
        //given
        String refreshToken = jwtTokenUtil.createRefreshToken(EMAIL1, ID1, List.of(ROLE_USER), NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(delete("/auth/token")
                .header(REFRESH_HEADER, refreshToken));

        //then
        perform.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("??????????????? ???????????? ??? ????????? ??????????????? ??????????????? ?????? 204 No Content ??????")
    public void test8() throws Exception {
        //given
        String bearerToken = jwtTokenUtil.createRefreshToken(EMAIL1, ID1, List.of(ROLE_USER), NICKNAME1);
        String tokenValue = bearerToken.split(" ")[1];
        redisRepositoryImpl.saveData(tokenValue, User.builder().build(), REFRESH_TOKEN_EXPIRE_COUNT);

        //when
        ResultActions perform = mockMvc
                .perform(delete("/auth/token")
                        .header(REFRESH_HEADER, bearerToken));

        //then
        perform.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("?????? ???????????? ???????????? ??? ?????? ????????? ????????????????????? ???????????? ?????? ?????? 404 Not Found ??????")
    public void test9() throws Exception {
        //given
        String bearerToken = jwtTokenUtil.createRefreshToken(EMAIL1, ID1, List.of(ROLE_USER), NICKNAME1);

        //when
        ResultActions perform = mockMvc.perform(put("/auth/token")
                .header(REFRESH_HEADER, bearerToken));

        //then
        perform
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("?????? ???????????? ???????????? ??? ?????? ????????? ???????????? ?????? ????????? ??????????????? 401 UnAuthorized ??????")
    public void test10() throws Exception {
        //given
        String invalid_bearer_token = jwtTokenUtil.createRefreshToken(null, ID1, List.of(ROLE_USER), NICKNAME1);
        String invalid_token = invalid_bearer_token.split(" ")[1];
        redisRepositoryImpl.saveData(invalid_token, User.builder().build(), REFRESH_TOKEN_EXPIRE_COUNT);
        //when
        ResultActions perform = mockMvc.perform(put("/auth/token")
                .header(REFRESH_HEADER, invalid_bearer_token));

        //then
        perform
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("?????? ???????????? ???????????? ??? ?????? ????????? ????????????????????? ???????????? ????????? ????????? ?????? 201 ??????????????? ????????? AccessToken/RefreshToken ??????")
    public void test11() throws Exception {
        //given
        User user = User.builder()
                .id(ID1)
                .email(EMAIL1)
                .build();

        String bearerToken = jwtTokenUtil.createRefreshToken(EMAIL1, ID1, List.of(ROLE_USER), NICKNAME1);
        String tokenValue = bearerToken.split(" ")[1];
        redisRepositoryImpl.saveData(tokenValue, user, REFRESH_TOKEN_EXPIRE_COUNT);

        //when
        ResultActions perform = mockMvc.perform(put("/auth/token")
                .header(REFRESH_HEADER, bearerToken));

        //then
        perform
                .andExpect(status().isCreated())
                .andExpect(jsonPath("accessToken").isString())
                .andExpect(jsonPath("refreshToken").isString());
    }

    @Test
    @DisplayName("???????????? ?????? ????????? ????????? ?????? ?????? 401 UnAuthorized Exception??? ????????????.")
    public void test12() throws Exception {
        //given
        String invalid_token = "Bearer invalid_token";

        //when
        ResultActions perform = mockMvc.perform(post("/auth")
                .header(JWT_HEADER, invalid_token));

        //then
        perform.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("???????????? ?????? ??? ????????? ???????????? ?????????????????? 404 NotFound??? ????????????.")
    public void test13() throws Exception {
        //given
        AuthDto.RequestJoin request = AuthDto.RequestJoin.builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .nickname(NICKNAME1)
                .authKey(AUTH_KEY)
                .build();
        Long expiration = 1L;

        mailAuthRedisRepositoryImpl.saveData(AUTH_KEY, EMAIL1, expiration);
        Thread.sleep(10);
        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isNotFound())
                .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("?????? ????????? ???????????? ???????????? ???????????? ????????????????????? ????????? ?????? 409 Confilct ??????")
    public void requestJoin_failed() throws Exception {
        //given
        AuthDto.RequestJoin request = AuthDto.RequestJoin.builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .nickname(NICKNAME1)
                .authKey(AUTH_KEY)
                .build();

        User sameNicknameUser = User.builder().nickname(NICKNAME1).build();
        mailAuthRedisRepositoryImpl.saveData(AUTH_KEY, EMAIL1, VALIDITY_PERIOD_OF_THE_AUTHENTICATION_KEY);
        userRepository.save(sameNicknameUser);
        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isConflict())
                .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("???????????? ?????? ??? ????????? ??????????????? db ????????? ??????????????? ???????????? ????????? 409 ?????? ??????")
    public void changePassword_failed() throws Exception {
        //given
        User dbUser = User.builder()
                .email(EMAIL1)
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .build();

        mailAuthRedisRepositoryImpl.saveData(AUTH_KEY, EMAIL1, VALIDITY_PERIOD_OF_THE_AUTHENTICATION_KEY);
        User savedUser = userRepository.save(dbUser);

        AuthDto.RequestChangePassword request = AuthDto.RequestChangePassword.builder()
                .newPassword(PASSWORD2)
                .originalPassword(PASSWORD1)
                .build();

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, savedUser.getId(), ROLE_USER_LIST, NICKNAME1);

        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc.perform(patch("/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JWT_HEADER, accessToken)
                .content(json));

        //then
        perform.andExpect(status().isConflict());
    }

    @Test
    @DisplayName("???????????? ?????? ??? ????????? ??????????????? db ????????? ??????????????? ??????????????? 200ok ???????????? ?????? ???????????? ??????")
    public void changePassword_success() throws Exception {
        //given
        User dbUser = User.builder()
                .email(EMAIL1)
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .build();

        mailAuthRedisRepositoryImpl.saveData(AUTH_KEY, EMAIL1, VALIDITY_PERIOD_OF_THE_AUTHENTICATION_KEY);
        authService.joinUser(dbUser, AUTH_KEY);
        User savedUser = userRepository.findUserByEmail(EMAIL1).orElseThrow(() -> new AssertionError());

        AuthDto.RequestChangePassword request = AuthDto.RequestChangePassword.builder()
                .newPassword(PASSWORD2)
                .originalPassword(PASSWORD1)
                .build();

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, savedUser.getId(), ROLE_USER_LIST, NICKNAME1);

        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc.perform(patch("/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JWT_HEADER, accessToken)
                .content(json));

        //then
        perform.andExpect(status().isOk())
                .andExpect(content().string(Boolean.TRUE.toString()));

        Assertions.assertThat(savedUser.comparePassword(passwordEncoder, dbUser.getPassword())).isFalse();
    }

    @Test
    @DisplayName("?????? ????????? ????????? ????????? ???????????? ??????????????? ???????????? ???????????? 409 ?????? ??????")
    public void delete_account_failed_409() throws Exception {
        //given
        User dbUser = User.builder()
                .email(EMAIL1)
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .build();

        mailAuthRedisRepositoryImpl.saveData(AUTH_KEY, EMAIL1, VALIDITY_PERIOD_OF_THE_AUTHENTICATION_KEY);
        authService.joinUser(dbUser, AUTH_KEY);
        User savedUser = userRepository.findUserByEmail(EMAIL1).orElseThrow(() -> new AssertionError());

        AuthDto.RequestWithdrawal request = AuthDto.RequestWithdrawal.builder()
                .password(PASSWORD2)
                .build();

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, savedUser.getId(), ROLE_USER_LIST, NICKNAME1);

        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc.perform(delete("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, accessToken));

        //then
        perform
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("?????? ????????? ????????? ????????? ???????????? ??????????????? ??????????????? ?????? ????????? status??? DELETED??? ??????")
    public void delete_account_success_204() throws Exception {
        //given
        User dbUser = User.builder()
                .email(EMAIL1)
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .build();

        mailAuthRedisRepositoryImpl.saveData(AUTH_KEY, EMAIL1, VALIDITY_PERIOD_OF_THE_AUTHENTICATION_KEY);
        authService.joinUser(dbUser, AUTH_KEY);
        User savedUser = userRepository.findUserByEmail(EMAIL1).orElseThrow(() -> new AssertionError());

        AuthDto.RequestWithdrawal request = AuthDto.RequestWithdrawal.builder()
                .password(PASSWORD1)
                .build();

        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, savedUser.getId(), ROLE_USER_LIST, NICKNAME1);

        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc.perform(delete("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, accessToken));

        Optional<User> result = userRepository.findById(savedUser.getId());

        //then
        perform
                .andExpect(status().isNoContent());

        Assertions.assertThat(result.get().getUserStatus()).isEqualTo(UserStatus.DELETED);
    }
}
