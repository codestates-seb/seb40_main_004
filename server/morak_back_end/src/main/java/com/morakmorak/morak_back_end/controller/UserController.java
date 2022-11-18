package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    
    @GetMapping("/{user-id}/dashboard")
    @ResponseStatus(HttpStatus.OK)
    public UserDto.ResponseDashBoard getDashboard(@PathVariable(name = "user-id") Long id) {
        return userService.findUserDashboard(id);
    }
}
