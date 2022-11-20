package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.security.resolver.RequestUser;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    
    @GetMapping("/{user-id}/dashboard")
    @ResponseStatus(HttpStatus.OK)
    public UserDto.ResponseDashBoard getDashboard(@PathVariable(name = "user-id") Long id) {
        return userService.findUserDashboard(id);
    }

    @PatchMapping("/profiles")
    @ResponseStatus(HttpStatus.OK)
    public UserDto.RequestEditProfile patchProfile(@Valid @RequestBody UserDto.RequestEditProfile request, @RequestUser UserDto.UserInfo userInfo) {
        return userService.editUserProfile(userMapper.EditProfileToEntity(request), userInfo.getId());
    }
}
