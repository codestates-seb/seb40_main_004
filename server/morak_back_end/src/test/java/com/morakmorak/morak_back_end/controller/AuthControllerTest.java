package com.morakmorak.morak_back_end.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.dto.AuthDto;
import com.morakmorak.morak_back_end.dto.EmailDto;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.security.exception.InvalidJwtTokenException;
import com.morakmorak.morak_back_end.security.resolver.JwtArgumentResolver;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.security.util.SecurityConstants;
import com.morakmorak.morak_back_end.service.AuthService;
import com.morakmorak.morak_back_end.util.SecurityTestConfig;
import com.morakmorak.morak_back_end.util.SecurityTestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.morakmorak.morak_back_end.exception.ErrorCode.*;
import static com.morakmorak.morak_back_end.security.util.SecurityConstants.*;
import static com.morakmorak.morak_back_end.security.util.SecurityConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.security.util.SecurityConstants.JWT_PREFIX;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ACCESS_TOKEN;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@Import(SecurityTestConfig.class)
@WebMvcTest({AuthController.class, UserMapper.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AuthService authService;

    @MockBean
    JwtArgumentResolver jwtArgumentResolver;

    JwtTokenUtil jwtTokenUtil;

    @BeforeEach
    public void init() {
        jwtTokenUtil = new JwtTokenUtil(ACCESS_TOKEN, REFRESH_TOKEN);
    }

    @Test
    @DisplayName("로그인 / 입력된 아이디 혹은 패스워드가 유효하지 않을 경우 401 UNAUTHORIZED 반환")
    public void requestLogin1() throws Exception {
        // given
        AuthDto.RequestLogin requestLogin = AuthDto.RequestLogin
                .builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        String json = objectMapper.writeValueAsString(requestLogin);

        given(authService.loginUser(any(User.class))).willThrow(new BusinessLogicException(INVALID_USER));

        // when
        ResultActions perform = mockMvc
                .perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                );

        // then
        perform.andExpect(status().isUnauthorized())
                .andDo(document("Login Failed (unmatched password or not found Email)",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("Invalid email or not found email"),
                                fieldWithPath("password").description("Invalid password or not found email")
                        )
                        )
                );
    }

    @Test
    @DisplayName("로그인 / 입력된 아이디 및 패스워드가 유효한 경우 AccessToken과 RefreshToken 반환 반환")
    public void requestLogin2() throws Exception {
        // given
        AuthDto.RequestLogin requestLogin = AuthDto.RequestLogin
                .builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        String json = objectMapper.writeValueAsString(requestLogin);

        AuthDto.ResponseToken responseToken = AuthDto.ResponseToken.builder()
                .accessToken(BEARER_ACCESS_TOKEN)
                .refreshToken(BEARER_REFRESH_TOKEN)
                .build();

        given(authService.loginUser(any(User.class))).willReturn(responseToken);

        // when
        ResultActions perform = mockMvc
                .perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                );

        // then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value(BEARER_ACCESS_TOKEN))
                .andExpect(jsonPath("$.refreshToken").value(BEARER_REFRESH_TOKEN))
                .andDo(document("login success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("email").description("valid user email"),
                                        fieldWithPath("password").description("valid user password")
                                ),
                                responseFields(
                                        fieldWithPath("accessToken").description("accessToken, set client's LocalStorage"),
                                        fieldWithPath("refreshToken").description("refreshToken, set client's Cookie")
                                )
                        )
                );
    }

    @Test
    @DisplayName("회원가입하는 유저의 입력값이 검증에 실패할 경우 400 BadRequest 반환")
    public void test3() throws Exception {
        //given
        AuthDto.RequestJoin request = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .email(INVALID_EMAIL)
                .authKey(AUTH_KEY)
                .build();

        String json = objectMapper.writeValueAsString(request);

        given(authService.joinUser(any(User.class), anyString())).willThrow(new BusinessLogicException(ONLY_TEST_CODE));

        //when
        ResultActions perform = mockMvc
                .perform(post("/auth")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON));

        //then
        perform
                .andExpect(status().isBadRequest())
                .andDo(document(
                        "join failed (invalid input value)",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("invalid email"),
                                fieldWithPath("password").description("password"),
                                fieldWithPath("nickname").description("nickname"),
                                fieldWithPath("authKey").description("auth Key")
                        )
                        )
                );
    }

    @Test
    @DisplayName("회원가입하는 유저의 이메일이 이미 데이터베이스에 존재할 경우 409 Conflict 반환")
    public void test4() throws Exception {
        //given
        AuthDto.RequestJoin request = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .email(EMAIL1)
                .authKey(AUTH_KEY)
                .build();

        String json = objectMapper.writeValueAsString(request);
        given(authService.joinUser(any(User.class), anyString())).willThrow(new BusinessLogicException(EMAIL_EXISTS));

        //when
        ResultActions perform = mockMvc
                .perform(post("/auth")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON));

        //then
        perform
                .andExpect(status().isConflict())
                .andDo(document(
                                "join failed (duplicate email)",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("email").description("exist email"),
                                        fieldWithPath("password").description("valid password"),
                                        fieldWithPath("nickname").description("valid nickname"),
                                        fieldWithPath("authKey").description("auth Key")
                                )
                        )
                );
    }


    @Test
    @DisplayName("회원가입하는 유저의 입력값이 유효하고 중복되지 않는다면 201 스테이터스 코드와 시퀀스값 반환")
    public void test6() throws Exception {
        //given
        AuthDto.RequestJoin request = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .email(EMAIL1)
                .authKey(AUTH_KEY)
                .build();

        AuthDto.ResponseToken token = AuthDto.ResponseToken.builder()
                .accessToken(BEARER_ACCESS_TOKEN)
                .refreshToken(BEARER_REFRESH_TOKEN)
                .build();

        String json = objectMapper.writeValueAsString(request);
        given(authService.joinUser(any(User.class), anyString())).willReturn(token);

        //when
        ResultActions perform = mockMvc
                .perform(post("/auth")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON));

        //then
        perform
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value(BEARER_ACCESS_TOKEN))
                .andExpect(jsonPath("$.refreshToken").value(BEARER_REFRESH_TOKEN))
                .andDo(document(
                                "join success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("email").description("valid email"),
                                        fieldWithPath("password").description("valid password"),
                                        fieldWithPath("nickname").description("valid nickname"),
                                        fieldWithPath("authKey").description("auth key")
                                ),
                                responseFields(
                                        fieldWithPath("accessToken").description("set client's local storage"),
                                        fieldWithPath("refreshToken").description("set client's cookie")
                                )
                        )
                );
    }

    @Test
    @DisplayName("리프레시 토큰이 존재하지 않을 시 404 NotFound를 반환")
    public void test7() throws Exception {
        //given
        given(authService.logoutUser(BEARER_REFRESH_TOKEN)).willThrow(new BusinessLogicException(TOKEN_NOT_FOUND));

        //when
        ResultActions perform = mockMvc
                .perform(delete("/auth/token")
                        .header(REFRESH_HEADER, BEARER_REFRESH_TOKEN));

        //then
        perform
                .andExpect(status().isNotFound())
                .andDo(document(
                        "logout failed (not found token)",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(REFRESH_HEADER).description("Not Found RefreshToken")
                        )
                )
                );
    }

    @Test
    @DisplayName("리프레시 토큰이 존재하고 정상적으로 로직이 수행됐을 경우 204 NoContent를 반환한다.")
    public void test8() throws Exception {
        //given
        given(authService.logoutUser(BEARER_REFRESH_TOKEN)).willReturn(Boolean.TRUE);

        //when
        ResultActions perform = mockMvc
                .perform(delete("/auth/token")
                        .header(REFRESH_HEADER, BEARER_REFRESH_TOKEN));

        //then
        perform
                .andExpect(status().isNoContent())
                .andDo(document(
                                "logout success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(REFRESH_HEADER).description("valid refreshToken")
                                )
                        )
                );
    }

    @Test
    @DisplayName("요청받은 리프레시 토큰이 존재하지 않을 때 404 NotFound를 반환한다.")
    public void test9() throws Exception {
        //given
        given(authService.reissueToken(BEARER_REFRESH_TOKEN)).willThrow(new BusinessLogicException(TOKEN_NOT_FOUND));

        //when
        ResultActions perform = mockMvc
                .perform(put("/auth/token")
                        .header(REFRESH_HEADER, BEARER_REFRESH_TOKEN));

        //then
        perform.andExpect(status().isNotFound())
                .andDo(
                        document("token reissue failed(NotFoundToken)",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(REFRESH_HEADER).description("that token dose not exist in the database")
                                ))
                );
    }

    @Test
    @DisplayName("요청받은 리프레시 토큰이 유효하지 않을 때 401 UnAuthorized를 반환한다.")
    public void test10() throws Exception {
        //given
        given(authService.reissueToken(BEARER_REFRESH_TOKEN)).willThrow(new InvalidJwtTokenException(ErrorCode.EXPIRED_EXCEPTION));

        //when
        ResultActions perform = mockMvc
                .perform(put("/auth/token")
                        .header(REFRESH_HEADER, BEARER_REFRESH_TOKEN));

        //then
        perform.andExpect(status().isUnauthorized())
                .andDo(document(
                        "token reissue failed(InvalidToken)",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(REFRESH_HEADER).description("invalid token(expired, malformed, unsupported, signature, illegalArgument..)")
                        ))
                );
    }

    @Test
    @DisplayName("요청받은 리프레시 토큰이 유효하고 데이터베이스에 존재하는 경우 201 Created와 함께 RefreshToken/AccessToken을 반환한다.")
    public void test11() throws Exception {
        //given
        AuthDto.ResponseToken response = AuthDto.ResponseToken
                .builder()
                .accessToken(BEARER_ACCESS_TOKEN)
                .refreshToken(BEARER_REFRESH_TOKEN)
                .build();

        given(authService.reissueToken(BEARER_REFRESH_TOKEN)).willReturn(response);

        //when
        ResultActions perform = mockMvc
                .perform(put("/auth/token")
                        .header(REFRESH_HEADER, BEARER_REFRESH_TOKEN));

        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value(BEARER_ACCESS_TOKEN))
                .andExpect(jsonPath("$.refreshToken").value(BEARER_REFRESH_TOKEN))
                .andDo(document(
                        "token reissuance success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(REFRESH_HEADER).description("valid token")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("set client's localStorage"),
                                fieldWithPath("refreshToken").description("set client's cookie")
                        )
                )
                );
    }

    @Test
    @DisplayName("이메일 인증을 요청하고 5분 내에 재요청할경우 409 Conflict 반환")
    public void test12() throws Exception {
        //given
        EmailDto.RequestSendMail request = EmailDto.RequestSendMail.builder()
                .email(EMAIL1)
                .build();

        String json = objectMapper.writeValueAsString(request);
        given(authService.sendAuthenticationMail(any(EmailDto.RequestSendMail.class))).willThrow(new BusinessLogicException(AUTH_KEY_ALREADY_EXISTS));

        //when
        ResultActions perform = mockMvc
                .perform(post("/auth/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));

        //then
        perform
                .andExpect(status().isConflict())
                .andDo(document(
                        "auth key that already exists",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("the same email can only be verified once every 5 minutes.")
                        )
                )
                );
    }

    @Test
    @DisplayName("이메일 인증이 정상적으로 요청될 경우 201 created와 true를 반환한다.")
    public void test13() throws Exception {
        //given
        EmailDto.RequestSendMail request = EmailDto.RequestSendMail.builder()
                .email(EMAIL1)
                .build();

        String json = objectMapper.writeValueAsString(request);
        given(authService.sendAuthenticationMail(any(EmailDto.RequestSendMail.class))).willReturn(Boolean.TRUE);

        //when
        ResultActions perform = mockMvc
                .perform(post("/auth/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));

        //then
        perform
                .andExpect(status().isCreated())
                .andExpect(content().string(Boolean.TRUE.toString()))
                .andDo(document(
                                "auth key that already exists",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("email").description("the same email can only be verified once every 5 minutes.")
                                ),
                                responseBody()
                        )
                );
    }

    @Test
    @DisplayName("전송한 인증 키가 올바르지 않을 경우 404 NOT FOUND를 반환한다")
    public void test14() throws Exception {
        //given
        EmailDto.RequestVerifyAuthKey request = EmailDto.RequestVerifyAuthKey
                .builder()
                .authKey(AUTH_KEY)
                .email(EMAIL1)
                .build();

        given(authService.authenticateEmail(any(EmailDto.RequestVerifyAuthKey.class))).willThrow(new BusinessLogicException(ErrorCode.INVALID_AUTH_KEY));
        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc
                .perform(put("/auth/mail")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json));

        //then
        perform
                .andExpect(status().isNotFound())
                .andDo(document(
                        "auth key that already exists",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("authKey").description("invalid authKey"),
                                fieldWithPath("email").description("user email")
                        )
                )
                );
    }

    @Test
    @DisplayName("전송한 인증 키가 올바른 경우 회원가입 시 필요한 인증키와 스테이터스 코드 200을 반환한다")
    public void test15() throws Exception {
        //given
        String newAuthKey = "222222222";

        EmailDto.RequestVerifyAuthKey request = EmailDto.RequestVerifyAuthKey
                .builder()
                .authKey(AUTH_KEY)
                .email(EMAIL1)
                .build();

        AuthDto.ResponseAuthKey response = AuthDto.ResponseAuthKey
                .builder()
                .authKey(newAuthKey)
                .build();

        given(authService.authenticateEmail(any(EmailDto.RequestVerifyAuthKey.class))).willReturn(response);
        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc
                .perform(put("/auth/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));

        //then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authKey").value(newAuthKey))
                .andDo(document(
                                "request email verification request",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("authKey").description("user authKey"),
                                        fieldWithPath("email").description("user email")
                                ),
                        responseFields(
                                fieldWithPath("authKey").description("set in user's useState And send when requestJoin")
                        )
                        )
                );
    }

    @Test
    @DisplayName("회원가입하려는 유저의 인증키가 유효하지 않은 경우 404 NOT FOUND 반환")
    public void test16() throws Exception {
        //given
        AuthDto.RequestJoin request = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .email(EMAIL1)
                .authKey(AUTH_KEY)
                .build();

        String json = objectMapper.writeValueAsString(request);
        given(authService.joinUser(any(User.class), anyString())).willThrow(new BusinessLogicException(ErrorCode.INVALID_AUTH_KEY));

        //when
        ResultActions perform = mockMvc
                .perform(post("/auth")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON));

        //then
        perform
                .andExpect(status().isNotFound())
                .andDo(document(
                                "join success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("email").description("valid email"),
                                        fieldWithPath("password").description("valid password"),
                                        fieldWithPath("nickname").description("valid nickname"),
                                        fieldWithPath("authKey").description("auth key is only valid for one hour")
                                )
                        )
                );
    }

    @Test
    @DisplayName("중복 확인하는 닉네임이 이미 존재할 경우 409 Conflict를 반환한다.")
    public void checkDuplicateNickname_failed1() throws Exception {
        //given
        AuthDto.RequestCheckNickname dto = AuthDto.RequestCheckNickname
                .builder()
                .nickname(NICKNAME1)
                .build();

        given(authService.checkDuplicateNickname(NICKNAME1)).willThrow(new BusinessLogicException(NICKNAME_EXISTS));

        String json = objectMapper.writeValueAsString(dto);

        //when
        ResultActions perform = mockMvc.perform(post("/auth/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isConflict())
                .andDo(document(
                        "닉네임_중복_체크_status_409",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickname").description("이미 존재하는 닉네임 요청")
                )
                )
                );
    }

    @Test
    @DisplayName("닉네임 중복 확인 요청 시 dto 검증에 실패할 경우 400 BadRequest를 반환한다.")
    public void checkDuplicateNickname_failed2() throws Exception {
        //given
        AuthDto.RequestCheckNickname dto = AuthDto.RequestCheckNickname
                .builder()
                .nickname(INVALID_NICKNAME)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        //when
        ResultActions perform = mockMvc.perform(post("/auth/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document(
                                "닉네임_중복확인_유효하지_않은_요청값_400",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("nickname").description("유효성 검사 실패, 닉네임은 null이거나 공백일 수 없으며 닉네임 규정을 충족해야합니다.")
                                )
                        )
                );
    }

    @Test
    @DisplayName("닉네임 중복 확인 요청 시 중복 닉네임이 존재하지 않고 dto 유효성 검사에 성공할 경우 true와 200 OK를 반환한다.")
    public void checkDuplicateNickname_success() throws Exception {
        //given
        AuthDto.RequestCheckNickname dto = AuthDto.RequestCheckNickname
                .builder()
                .nickname(NICKNAME1)
                .build();

        given(authService.checkDuplicateNickname(NICKNAME1)).willReturn(Boolean.TRUE);

        String json = objectMapper.writeValueAsString(dto);

        //when
        ResultActions perform = mockMvc.perform(post("/auth/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isOk())
                .andExpect(content().string(Boolean.TRUE.toString()))
                .andDo(document(
                                "닉네임_중복확인_유효하지_않은_요청값_400",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("nickname").description("중복되지 않는 닉네임")
                                ),
                        responseBody()
                        )
                );
    }

    @Test
    @DisplayName("회원가입 요청 시 동일한 닉네임이 데이터베이스에 존재한다면 409 Conflict를 반환한다.")
    public void requestJoin_failed() throws Exception {
        //given
        AuthDto.RequestJoin request = AuthDto.RequestJoin
                .builder()
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .email(EMAIL1)
                .authKey(AUTH_KEY)
                .build();

        String json = objectMapper.writeValueAsString(request);
        given(authService.joinUser(any(User.class), anyString())).willThrow(new BusinessLogicException(NICKNAME_EXISTS));

        //when
        ResultActions perform = mockMvc
                .perform(post("/auth")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON));

        //then
        perform
                .andExpect(status().isConflict())
                .andDo(document(
                                "join failed (duplicate email)",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("email").description("valid email"),
                                        fieldWithPath("password").description("valid password"),
                                        fieldWithPath("nickname").description("이미 존재하는 닉네임."),
                                        fieldWithPath("authKey").description("auth Key")
                                )
                        )
                );
    }

    @Test
    @DisplayName("비밀번호 변경 요청 시 DTO 유효성 검증에 실패한다면 400 BadRequest 반환")
    public void requestChangePassword_failed() throws Exception {
        //given
        AuthDto.RequestChangePassword request = AuthDto.RequestChangePassword.builder()
                .originalPassword(PASSWORD1)
                .newPassword(INVALID_PASSWORD)
                .build();

        String json = objectMapper.writeValueAsString(request);

        given(authService.changePassword(anyString(), anyString(), anyLong())).willThrow(new BusinessLogicException(ONLY_TEST_CODE));

        //when
        ResultActions perform = mockMvc.perform(post("/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, ACCESS_TOKEN));

        //then
        perform
                .andExpect(status().isBadRequest())
                .andDo(
                        document("changePassword_failed_400",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("access token")
                        ),
                        requestFields(
                                fieldWithPath("originalPassword").description("변경 전 패스워드"),
                                fieldWithPath("newPassword").description("유효성 검사 실패 패스워드")
                        )
                )
                );
    }

    @Test
    @DisplayName("비밀번호 변경 요청 시 originalPassword가 기존 패스워드와 다르다면 409 Conflict를 반환한다.")
    public void requestChangePassword_failed2() throws Exception {
        //given
        AuthDto.RequestChangePassword request = AuthDto.RequestChangePassword.builder()
                .originalPassword(PASSWORD1)
                .newPassword(PASSWORD2)
                .build();

        String json = objectMapper.writeValueAsString(request);

        given(authService.changePassword(anyString(), anyString(), any())).willThrow(new BusinessLogicException(UNABLE_TO_CHANGE_PASSWORD));

        //when
        ResultActions perform = mockMvc.perform(post("/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, ACCESS_TOKEN));

        //then
        perform
                .andExpect(status().isConflict())
                .andDo(
                        document("changePassword_failed_409",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(JWT_HEADER).description("access token")
                                ),
                                requestFields(
                                        fieldWithPath("originalPassword").description("기존 패스워드(잘못된 값 입력)"),
                                        fieldWithPath("newPassword").description("변경 희망 패스워드")
                                )
                        )
                );
    }

    @Test
    @DisplayName("비밀번호 변경 요청 시 DTO 유효성 검증에 성공하고 DB 패스워드와 originalPassword가 일치한다면 200 OK와 true 반환")
    public void requestChangePassword_success() throws Exception {
        //given
        AuthDto.RequestChangePassword request = AuthDto.RequestChangePassword.builder()
                .originalPassword(PASSWORD1)
                .newPassword(PASSWORD2)
                .build();

        String json = objectMapper.writeValueAsString(request);

        given(authService.changePassword(anyString(), anyString(), any())).willReturn(Boolean.TRUE);

        //when
        ResultActions perform = mockMvc.perform(post("/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, ACCESS_TOKEN));

        //then
        perform
                .andExpect(status().isOk())
                .andExpect(content().string(Boolean.TRUE.toString()))
                .andDo(
                        document("changePassword_failed_400",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(JWT_HEADER).description("access token")
                                ),
                                requestFields(
                                        fieldWithPath("originalPassword").description("변경 전 패스워드"),
                                        fieldWithPath("newPassword").description("유효성 검사 실패 패스워드")
                                ),
                                responseBody()
                        )
                );
    }
}
