package com.morakmorak.morak_back_end.Integration;

import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"}
)
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
public class TestControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    String userToken;
    String managerToken;
    String adminToken;
    String user_manager_token;
    String user_admin_token;
    String user_manager_admin_token;

    public String createToken(List<String> roles) {
        return jwtTokenUtil.createAccessToken(EMAIL1, ID1, roles,NICKNAME1);
    }

    @BeforeEach
    public void init() {
        userToken = createToken(ROLE_USER_LIST);
        managerToken = createToken(ROLE_MANAGER_LIST);
        adminToken = createToken(ROLE_ADMIN_LIST);
        user_admin_token = createToken(ROLE_USER_ADMIN_LIST);
        user_manager_token = createToken(ROLE_USER_MANAGER_LIST);
        user_manager_admin_token = createToken(ROLE_USER_MANAGER_ADMIN_LIST);
    }

    @Test
    @DisplayName("ROLE_USER || ROLE_MANAGER || ROLE_ADMIN이 없다면 인가에 실패한다.")
    public void test1() throws Exception {
        //given //when
        ResultActions perform = mockMvc
                .perform(get("/test/user")
                        .header("User-Agent", "Mozilla 5.0"));

        //then
        perform.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /test/user ROLE_USER 권한이 있다면 인가에 성공한다.")
    public void test2() throws Exception {
        //given //when
        ResultActions perform = mockMvc
                .perform(get("/test/user")
                        .header("User-Agent", "Mozilla 5.0")
                        .header(JWT_HEADER, userToken));

        //then
        perform.andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /test/user ROLE_USER 이상의 권한을 가지고 있다면 인가에 성공한다(1)")
    public void test3() throws Exception {
        //given //when
        ResultActions perform = mockMvc
                .perform(get("/test/user")
                        .header("User-Agent", "Mozilla 5.0")
                        .header(JWT_HEADER, user_manager_admin_token));

        //then
        perform.andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /test/user ROLE_USER 이상의 권한을 가지고 있다면 인가에 성공한다(2)")
    public void test4() throws Exception {
        //given //when
        ResultActions perform = mockMvc
                .perform(get("/test/user")
                        .header("User-Agent", "Mozilla 5.0")
                        .header(JWT_HEADER, user_manager_admin_token));

        //then
        perform.andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /test/user ROLE_USER 이상의 권한을 가지고 있다면 인가에 성공한다(3)")
    public void test5() throws Exception {
        //given //when
        ResultActions perform = mockMvc
                .perform(get("/test/user")
                        .header("User-Agent", "Mozilla 5.0")
                        .header(JWT_HEADER, adminToken));

        //then
        perform.andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /test/manager 요청 헤더에 토큰이 없다면 인증에 실패한다.")
    public void test6() throws Exception {
        //given //when
        ResultActions perform = mockMvc
                .perform(get("/test/manager")
                        .header("User-Agent", "Mozilla 5.0"));

        //then
        perform.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /test/manager ROLE_USER 권한만 가지고 있다면 인가에 실패한다.")
    public void test7() throws Exception {
        //given //when
        ResultActions perform = mockMvc
                .perform(get("/test/manager")
                        .header("User-Agent", "Mozilla 5.0")
                        .header(JWT_HEADER, userToken));

        //then
        perform.andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /test/manager ROLE_MANAGER 이상의 권한을 가지고 있다면 인가에 성공한다(1)")
    public void test8() throws Exception {
        //given //when
        ResultActions perform = mockMvc
                .perform(get("/test/manager")
                        .header("User-Agent", "Mozilla 5.0")
                        .header(JWT_HEADER, managerToken));

        //then
        perform.andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /test/manager ROLE_MANAGER 이상의 권한을 가지고 있다면 인가에 성공한다(2)")
    public void test9() throws Exception {
        //given //when
        ResultActions perform = mockMvc
                .perform(get("/test/manager")
                        .header("User-Agent", "Mozilla 5.0")
                        .header(JWT_HEADER, adminToken));

        //then
        perform.andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /test/manager ROLE_MANAGER 이상의 권한을 가지고 있다면 인가에 성공한다(3)")
    public void test10() throws Exception {
        //given //when
        ResultActions perform = mockMvc
                .perform(get("/test/manager")
                        .header("User-Agent", "Mozilla 5.0")
                        .header(JWT_HEADER, user_manager_admin_token));

        //then
        perform.andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /test/admin 요청 헤더에 토큰이 없다면 인증에 실패한다")
    public void test11() throws Exception {
        //given //when
        ResultActions perform = mockMvc
                .perform(get("/test/admin")
                        .header("User-Agent", "Mozilla 5.0"));

        //then
        perform.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /test/admin ROLE_USER 권한만 있다면 인가에 실패한다")
    public void test12() throws Exception {
        //given //when
        ResultActions perform = mockMvc
                .perform(get("/test/admin")
                        .header(JWT_HEADER, userToken)
                        .header("User-Agent", "Mozilla 5.0"));

        //then
        perform.andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /test/admin ROLE_MANAGER 권한만 있다면 인가에 실패한다")
    public void test13() throws Exception {
        //given //when
        ResultActions perform = mockMvc
                .perform(get("/test/admin")
                        .header(JWT_HEADER, managerToken)
                        .header("User-Agent", "Mozilla 5.0"));

        //then
        perform.andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /test/admin ROLE_ADMIN 권한을 가지고 있다면 있다면 인가에 성공한다")
    public void test14() throws Exception {
        //given //when
        ResultActions perform = mockMvc
                .perform(get("/test/admin")
                        .header(JWT_HEADER, adminToken)
                        .header("User-Agent", "Mozilla 5.0"));

        //then
        perform.andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /test/admin ROLE_ADMIN 권한을 가지고 있다면 있다면 인가에 성공한다")
    public void test15() throws Exception {
        //given //when
        ResultActions perform = mockMvc
                .perform(get("/test/admin")
                        .header(JWT_HEADER, user_manager_admin_token)
                        .header("User-Agent", "Mozilla 5.0"));

        //then
        perform.andExpect(status().isOk());
    }
}
