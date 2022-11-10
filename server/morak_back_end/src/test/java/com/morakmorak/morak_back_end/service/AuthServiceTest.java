package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.adapter.TokenGenerator;
import com.morakmorak.morak_back_end.adapter.UserPasswordManager;
import com.morakmorak.morak_back_end.entity.RefreshToken;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.repository.RedisRepository;
import com.morakmorak.morak_back_end.repository.RoleRepository;
import com.morakmorak.morak_back_end.repository.UserRepository;
import com.morakmorak.morak_back_end.repository.UserRoleRepository;
import com.morakmorak.morak_back_end.security.exception.InvalidJwtTokenException;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.BEARER_REFRESH_TOKEN;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.INVALID_BEARER_REFRESH_TOKEN;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    AuthService authService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserPasswordManager userPasswordManager;

    @Mock
    TokenGenerator tokenGenerator;

    @Mock
    UserRoleRepository userRoleRepository;

    @Mock
    JwtTokenUtil jwtTokenUtil;

    @Mock
    RoleRepository roleRepository;

    @Mock
    RedisRepository<User> refreshTokenRedisRepository;

    @Mock
    RedisRepository<String> authKeyRedisRepository;

    @Mock
    AuthMailSenderImpl authMailSenderImpl;

    @Test
    @DisplayName("유저 로그인 / 조회한 유저의 패스워드를 복호화하여 인자와 비교했을 때 " +
            "값이 다르다면 BusinessLogicEception이 발생한다.")
    public void test2() {
        //given
        User dbUser = User
                .builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        User requestUser = User
                .builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        given(userRepository.findUserByEmail(requestUser.getEmail())).willReturn(Optional.of(dbUser));
        given(userPasswordManager.compareUserPassword(dbUser, requestUser)).willReturn(Boolean.FALSE);

        //when then
        assertThatThrownBy(() -> authService.loginUser(requestUser)).isInstanceOf(BusinessLogicException.class);
    }

//    @Test
//    @DisplayName("유저 로그인 / 조회한 유저의 패스워드를 복호화하여 인자와 비교했을 때 " +
//            "값이 같다면 AuthDto.Token을 반환한다.")
//    public void test3() {
//        //given
//        User dbUser = User
//                .builder()
//                .id(ID1)
//                .email(EMAIL1)
//                .password(PASSWORD1)
//                .build();
//
//        User requestUser = User
//                .builder()
//                .email(EMAIL1)
//                .password(PASSWORD1)
//                .build();
//
//        given(userRepository.findUserByEmail(requestUser.getEmail())).willReturn(Optional.of(dbUser));
//        given(userPasswordManager.compareUserPassword(dbUser, requestUser)).willReturn(Boolean.TRUE);
//        given(refreshTokenRedisRepository.saveData(anyString(), any(User.class), anyLong())).willReturn(Boolean.TRUE);
//        given(tokenGenerator.generateAccessToken(dbUser)).willReturn(BEARER_ACCESS_TOKEN);
//        given(tokenGenerator.generateRefreshToken(dbUser)).willReturn(BEARER_REFRESH_TOKEN);
//
//        //when
//        AuthDto.ResponseToken responseToken = authService.loginUser(requestUser);
//
//        //then
//        assertThat(responseToken.getAccessToken()).isEqualTo(BEARER_ACCESS_TOKEN);
//        assertThat(responseToken.getRefreshToken()).isEqualTo(BEARER_REFRESH_TOKEN);
//    }

//    @Test
//    @DisplayName("유저 회원가입 / 요청값과 동일한 메일을 가진 유저가 이미 존재하다면 BusinessLogicException이 발생한다")
//    public void test4() {
//        //given
//        User requestUser = User.builder()
//                .email(EMAIL1)
//                .password(PASSWORD1)
//                .build();
//
//        given(userRepository.findUserByEmail(EMAIL1)).willReturn(Optional.of(requestUser));
//
//        //when then
//        assertThatThrownBy(() -> authService.joinUser(requestUser))
//                .isInstanceOf(BusinessLogicException.class);
//    }

    // TODO : REDIS 적용으로 인한 임시 비활성화, 살릴 방법 찾지 못하면 삭제 예정
//    @Test
//    @DisplayName("유저 회원가입 / 요청값으로 들어온 메일이 기존에 존재하지 않는다면 회원가입에 성공하고 가입된 아이디를 반환한다")
//    public void test5() {
//        //given
//        User requestUser = User.builder()
//                .id(ID1)
//                .email(EMAIL1)
//                .password(PASSWORD1)
//                .build();
//
//        given(authService.saveRefreshToken(BEARER_REFRESH_TOKEN, requestUser)).willReturn(true);
//        given(tokenGenerator.generateAccessToken(requestUser)).willReturn(BEARER_ACCESS_TOKEN);
//        given(tokenGenerator.generateRefreshToken(requestUser)).willReturn(BEARER_REFRESH_TOKEN);
//        given(refreshTokenRedisRepository.saveData(REFRESH_TOKEN, requestUser, REFRESH_TOKEN_EXPIRE_COUNT)).willReturn(Boolean.TRUE);
//
//        //when
//        AuthDto.ResponseToken responseToken = authService.joinUser(requestUser);
//
//        //then
//        assertThat(responseToken.getAccessToken()).isEqualTo(BEARER_ACCESS_TOKEN);
//        assertThat(responseToken.getRefreshToken()).isEqualTo(BEARER_REFRESH_TOKEN);
//    }

    @Test
    @DisplayName("유저 로그아웃 / 요청값으로 들어온 리프레시 토큰이 기존에 존재하지 않는다면 BusinessLogicEcxeption이 발생한다.")
    public void test6() {
        //given when then
        assertThatThrownBy(() -> authService.logoutUser(BEARER_REFRESH_TOKEN))
                .isInstanceOf(BusinessLogicException.class);
    }

//    TODO : Redis 적용으로 인한 임시 비활성화, 해결 불가능할 경우 삭제 예정
//    @Test
//    @DisplayName("유저 로그아웃 / 요청값으로 들어온 리프레시 토큰 데이터가 존재한다면 해당 데이터를 삭제하고 true를 반환한다.")
//    public void test7() {
//        //given
//        User user = User
//                .builder()
//                .email(EMAIL1)
//                .build();
//
//        given(refreshTokenRedisRepository.getDataAndDelete(REFRESH_TOKEN, User.class)).willReturn(Optional.of(user));
//
//        //when
//        boolean result = authService.logoutUser(BEARER_REFRESH_TOKEN);
//
//        //then
//        assertThat(result).isTrue();
//    }

//    TODO : Redis 적용으로 인한 임시 비활성화, 해결 불가능할 경우 삭제 예정
//    @Test
//    @DisplayName("유저 회원가입 / 회원가입 시 기본적으로 권한을 가진다.")
//    public void test8() {
//        //given
//        User requestUser = User.builder()
//                .id(ID1)
//                .email(EMAIL1)
//                .password(PASSWORD1)
//                .build();
//
//        Role role = Role.builder().roleName(ROLE_USER).build();
//
//        UserRole userRole = UserRole.builder()
//                .user(requestUser)
//                .role(role)
//                .build();
//
//        given(userRepository.findUserByEmail(EMAIL1)).willReturn(Optional.empty());
//        given(userRoleRepository.save(any(UserRole.class))).willReturn(userRole);
//        given(tokenGenerator.generateAccessToken(requestUser)).willReturn(BEARER_ACCESS_TOKEN);
//        given(tokenGenerator.generateRefreshToken(requestUser)).willReturn(BEARER_REFRESH_TOKEN);
//        given(refreshTokenRedisRepository.saveData(REFRESH_TOKEN, requestUser, REFRESH_TOKEN_EXPIRE_COUNT)).willReturn(Boolean.TRUE);
//
//        //when
//        authService.joinUser(requestUser);
//
//        //then
//        assertThat(userRole.getUser()).isEqualTo(requestUser);
//        assertThat(userRole.getRole()).isEqualTo(role);
//    }

    @Test
    @DisplayName("토큰 재발행/ 데이터베이스에 존재하지 않는 RefreshToken을 전달 받은 경우 BusinessLogicException이 발생한다.")
    public void test9() {
        //given
        RefreshToken refreshToken = new RefreshToken(BEARER_REFRESH_TOKEN);
//        given(refreshTokenRepository.findRefreshTokenByValue(REFRESH_TOKEN)).willReturn(Optional.empty());
        //when then
        assertThatThrownBy(() -> authService.reissueToken(BEARER_REFRESH_TOKEN))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("토큰 재발행/ 유효하지 않은 RefreshToken을 전달 받은 경우 InvalidTokenException이 발생한다.")
    public void test10() {
        //given
        given(tokenGenerator.tokenValidation(INVALID_BEARER_REFRESH_TOKEN)).willThrow(InvalidJwtTokenException.class);

        //when then
        assertThatThrownBy(() -> authService.reissueToken(INVALID_BEARER_REFRESH_TOKEN))
                .isInstanceOf(InvalidJwtTokenException.class);
    }

    // TODO : Redis 적용으로 인한 임시 비활성화, 해결 불가능할 경우 삭제 예정
//    @Test
//    @DisplayName("토큰 재발행/ 유효한 RefreshToken을 전달 받은 경우 새로운 AccessToken과 RefreshToken을 발행한다.")
//    public void test11() {
//        //given
//        User user = User.builder()
//                .email(EMAIL1)
//                .build();
//
//        given(tokenGenerator.tokenValidation(BEARER_REFRESH_TOKEN)).willReturn(Boolean.TRUE);
//        given(refreshTokenRedisRepository.getDataAndDelete(REFRESH_TOKEN, User.class)).willReturn(Optional.of(user));
//        given(tokenGenerator.generateRefreshToken(user)).willReturn(BEARER_REFRESH_TOKEN);
//        given(tokenGenerator.generateAccessToken(user)).willReturn(BEARER_ACCESS_TOKEN);
//
//        //when
//        AuthDto.ResponseToken responseToken = authService.reissueToken(BEARER_REFRESH_TOKEN);
//
//        //then
//        assertThat(responseToken.getAccessToken()).isEqualTo(BEARER_ACCESS_TOKEN);
//        assertThat(responseToken.getRefreshToken()).isEqualTo(BEARER_REFRESH_TOKEN);
//    }

    // TODO : Redis 적용으로 인한 임시 비활성화, 해결 불가능할 경우 삭제 예정
//    @Test
//    @DisplayName("회원가입 성공 시 토큰을 반환한다")
//    public void test12() {
//        //given
//        User requestUser = User.builder()
//                .id(ID1)
//                .email(EMAIL1)
//                .password(PASSWORD1)
//                .build();
//
//        Role role = Role.builder().roleName(ROLE_USER).build();
//
//        UserRole userRole = UserRole.builder()
//                .user(requestUser)
//                .role(role)
//                .build();
//
//        given(userRepository.findUserByEmail(EMAIL1)).willReturn(Optional.empty());
//        given(userRoleRepository.save(any(UserRole.class))).willReturn(userRole);
//        given(tokenGenerator.generateAccessToken(requestUser)).willReturn(BEARER_ACCESS_TOKEN);
//        given(tokenGenerator.generateRefreshToken(requestUser)).willReturn(BEARER_REFRESH_TOKEN);
//        given(refreshTokenRedisRepository.saveData(REFRESH_TOKEN, requestUser, REFRESH_TOKEN_EXPIRE_COUNT)).willReturn(Boolean.TRUE);
//
//        //when
//        AuthDto.ResponseToken responseToken = authService.joinUser(requestUser);
//
//        //then
//        assertThat(responseToken.getAccessToken()).isEqualTo(BEARER_ACCESS_TOKEN);
//        assertThat(responseToken.getRefreshToken()).isEqualTo(BEARER_REFRESH_TOKEN);
//    }

//    TODO : Redis 적용으로 인한 임시 비활성화, 해결 불가능할 경우 삭제 예정
//    @Test
//    @DisplayName("레디스에 이메일 인증 전송을 요청받은 내역이 이미 존재한다면 BusinessLogicException이 발생한다.")
//    public void test13() {
//        // given
//        EmailDto.RequestSendMail request = EmailDto.RequestSendMail
//                .builder()
//                .email(EMAIL1)
//                .build();
//
//        String existsAuthKey = "1111";
//        given(authKeyRedisRepository.getData(EMAIL1, String.class)).willReturn(Optional.of(existsAuthKey));
//
//        // when then
//        assertThatThrownBy(() -> authService.sendAuthenticationMail(request))
//                .isInstanceOf(BusinessLogicException.class);
//    }

//    TODO : Redis 적용으로 인한 임시 비활성화, 해결 불가능할 경우 삭제 예정
//    @Test
//    @DisplayName("레디스에 이메일 인증 전송을 요청받은 내역이 존재하지 않는다면 실행을 완료하고 true를 반환한다.")
//    public void test14() {
//        // given
//        EmailDto.RequestSendMail request = EmailDto.RequestSendMail
//                .builder()
//                .email(EMAIL1)
//                .build();
//
//        given(authKeyRedisRepository.getDataAndDelete(EMAIL1, String.class)).willReturn(Optional.empty());
//        given(emailSendService.sendAuthKeyEmail(any(EmailDto.RequestSendMail.class), anyString())).willReturn(Boolean.TRUE);
//
//        // when
//        boolean result = authService.sendAuthenticationMail(request);
//
//        // then
//        assertThat(result).isTrue();
//    }

    @Test
    @DisplayName("닉네임 중복 검사 시 해당 닉네임이 존재할 경우 BusinessLogicException 발생")
    public void checkDuplicateNickname_failed() {
        //given
        given(userRepository.findUserByNickname(NICKNAME1)).willReturn(Optional.of(User.builder().build()));

        //when //then
        assertThatThrownBy(() -> authService.checkDuplicateNickname(NICKNAME1)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("닉네임 중복 검사 시 해당 닉네임이 존재하지 않을 경우 true 반환")
    public void checkDuplicateNickname_success() {
        //given
        given(userRepository.findUserByNickname(NICKNAME1)).willReturn(Optional.empty());

        //when
        Boolean result = authService.checkDuplicateNickname(NICKNAME1);

        //then
        assertThat(result).isTrue();
    }
}
