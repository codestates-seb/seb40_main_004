package com.morakmorak.morak_back_end.Integration.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.RedisContainerTest;
import com.morakmorak.morak_back_end.dto.AuthDto;
import com.morakmorak.morak_back_end.entity.User;
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
    @DisplayName("회원가입 시 DTO 검증에 실패하면 BAD_REQUEST(400) 예외가 발생한다")
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
    @DisplayName("회원가입 시 동일한 이메일이 이미 존재한다면 Conflict(409)코드를 반환한다.")
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
    @DisplayName("회원가입 시 동일한 이메일이 존재하지 않으며 DTO 검증을 통과하면 201코드와 DB 시퀀스를 반환한다.")
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
    @DisplayName("로그인하는 유저의 정보가 존재하지 않는다면 404 NOT FOUND를 반환한다.")
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
    @DisplayName("로그인을 요청할 때 입력된 패스워드와 DB의 패스워드값이 일치하지 않는다면 401 UnAuthorized를 반횐")
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
    @DisplayName("로그인하려는 계정의 이메일이 존재하며 패스워드가 일치한다면 201 Created 및 리프레시/액세스 토큰 반환")
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
    @DisplayName("로그아웃을 요청했을 때 삭제하려는 토큰이 존재하지 않으면 404 NOT FOUND 반환")
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
    @DisplayName("로그아웃을 요청했을 때 로직이 정상적으로 수행되었을 경우 204 No Content 반환")
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
    @DisplayName("토큰 재발행을 요청했을 때 해당 토큰이 데이터베이스에 존재하지 않을 경우 404 Not Found 반환")
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
    @DisplayName("토큰 재발행을 요청했을 때 해당 토큰이 유효하지 않은 형태의 토큰이라면 401 UnAuthorized 반환")
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
    @DisplayName("토큰 재발행을 요청했을 때 해당 토큰이 데이터베이스에 존재하고 유효한 형태인 경우 201 스테이터스 코드와 AccessToken/RefreshToken 반환")
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
    @DisplayName("유효하지 않은 액세스 토큰을 받은 경우 401 UnAuthorized Exception을 반환한다.")
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
    @DisplayName("회원가입 요청 시 인증한 이메일이 만료되었으면 404 NotFound를 반환한다.")
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
    @DisplayName("가입 신청한 닉네임과 중복되는 닉네임이 데이터베이스에 존재할 경우 409 Confilct 반환")
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
    @DisplayName("패스워드 변경 시 입력된 패스워드와 db 유저의 패스워드가 일치하지 않으면 409 코드 반환")
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
    @DisplayName("패스워드 변경 시 입력된 패스워드와 db 유저의 패스워드가 일치한다면 200ok 반환하고 유저 패스워드 변경")
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
    @DisplayName("회원 탈퇴시 요청한 회원의 아이디와 비밀번호가 매치되지 않는다면 409 코드 반환")
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
    @DisplayName("회원 탈퇴시 요청한 회원의 아이디와 비밀번호가 매치된다면 해당 유저를 삭제하고 204 NO CONTENT 반환")
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

        Assertions.assertThat(result).isEmpty();
    }
}
