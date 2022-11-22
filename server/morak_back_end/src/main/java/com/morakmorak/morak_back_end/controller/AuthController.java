package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.dto.AuthDto;
import com.morakmorak.morak_back_end.dto.EmailDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.security.resolver.RequestUser;
import com.morakmorak.morak_back_end.service.auth_user_service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.morakmorak.morak_back_end.security.util.SecurityConstants.REFRESH_HEADER;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthDto.ResponseToken postLogin(@Valid @RequestBody AuthDto.RequestLogin requestLogin) {
        User user = userMapper.toLoginEntity(requestLogin);
        return authService.loginUser(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthDto.ResponseToken postJoin(@Valid @RequestBody AuthDto.RequestJoin requestJoin) {
        User user = userMapper.toJoinEntity(requestJoin);
        return authService.joinUser(user, requestJoin.getAuthKey());
    }

    @DeleteMapping("/token")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteToken(@RequestHeader(REFRESH_HEADER) String refreshToken) {
        authService.logoutUser(refreshToken);
    }

    @PutMapping("/token")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthDto.ResponseToken putToken(@RequestHeader(REFRESH_HEADER) String refreshToken) {
        return authService.reissueToken(refreshToken);
    }

    @PostMapping("/mail")
    @ResponseStatus(HttpStatus.CREATED)
    public Boolean postAuthMail(@RequestBody EmailDto.RequestSendMail request) {
        return authService.sendAuthenticationMailForJoin(request.getEmail());
    }

    @PutMapping("/mail")
    @ResponseStatus(HttpStatus.OK)
    public AuthDto.ResponseAuthKey putAuthMailKey(@Valid @RequestBody EmailDto.RequestVerifyAuthKey request) {
        return authService.authenticateEmail(request.getEmail(), request.getAuthKey());
    }

    @PostMapping("/nickname")
    @ResponseStatus(HttpStatus.OK)
    public Boolean postCheckNickname(@Valid @RequestBody AuthDto.RequestCheckNickname request) {
        return authService.checkDuplicateNickname(request.getNickname());
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    public Boolean patchPassword(@Valid @RequestBody AuthDto.RequestChangePassword request, @RequestUser UserDto.UserInfo token) {
        return authService.changePassword(request.getOriginalPassword(), request.getNewPassword(), token.getId());
    }

    @PostMapping("/password/support")
    @ResponseStatus(HttpStatus.CREATED)
    public Boolean postPasswordMail(@Valid @RequestBody EmailDto.RequestSendMail request) {
        return authService.sendAuthenticationMailForFindPwd(request.getEmail());
    }

    @PostMapping("/password/recovery")
    @ResponseStatus(HttpStatus.OK)
    public Boolean postPasswordMailKey(@Valid @RequestBody EmailDto.RequestVerifyAuthKey request) {
        return authService.sendUserPasswordEmail(request.getEmail(), request.getAuthKey());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@Valid @RequestBody AuthDto.RequestWithdrawal request, @RequestUser UserDto.UserInfo token) {
        authService.deleteAccount(request.getPassword(), token.getId());
    }
}
