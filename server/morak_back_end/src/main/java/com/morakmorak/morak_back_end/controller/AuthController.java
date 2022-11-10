package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.dto.AuthDto;
import com.morakmorak.morak_back_end.dto.EmailDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.security.resolver.RequestUser;
import com.morakmorak.morak_back_end.service.AuthService;
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
    public AuthDto.ResponseToken requestLogin(@RequestBody AuthDto.RequestLogin requestLogin) {
        User user = userMapper.toLoginEntity(requestLogin);
        return authService.loginUser(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthDto.ResponseToken requestJoin(@RequestBody @Valid AuthDto.RequestJoin requestJoin) {
        User user = userMapper.toJoinEntity(requestJoin);
        return authService.joinUser(user, requestJoin.getAuthKey());
    }

    @DeleteMapping("/token")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void requestLogout(@RequestHeader(REFRESH_HEADER) String refreshToken) {
        authService.logoutUser(refreshToken);
    }

    @PutMapping("/token")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthDto.ResponseToken requestReissueToken(@RequestHeader(REFRESH_HEADER) String refreshToken) {
        return authService.reissueToken(refreshToken);
    }

    @PostMapping("/mail")
    @ResponseStatus(HttpStatus.CREATED)
    public boolean requestSendEmailAuth(@RequestBody EmailDto.RequestSendMail request) {
        return authService.sendAuthenticationMail(request);
    }

    @PutMapping("/mail")
    @ResponseStatus(HttpStatus.OK)
    public AuthDto.ResponseAuthKey requestVerifyEmailAuth(@RequestBody EmailDto.RequestVerifyAuthKey request) {
        return authService.authenticateEmail(request);
    }

    @PostMapping("/nickname")
    @ResponseStatus(HttpStatus.OK)
    public Boolean requestCheckNickname(@Valid @RequestBody AuthDto.RequestCheckNickname request) {
        return authService.checkDuplicateNickname(request.getNickname());
    }

    @PostMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    public Boolean requestChangePassword(@Valid @RequestBody AuthDto.RequestChangePassword request, @RequestUser UserDto.UserInfo token) {
        return authService.changePassword(request.getOriginalPassword(), request.getNewPassword(), token.getId());
    }
}
