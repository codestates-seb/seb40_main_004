package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.security.resolver.RequestUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/all")
    public String test1() {
        return "all";
    }

    @GetMapping("/user")
    public String test2() {
        return "all";
    }

    @GetMapping("/manager")
    public String test3() {
        return "all";
    }

    @GetMapping("/admin")
    public String admin() {
        return "all";
    }

    @PostMapping("/resolver")
    public String resolver(@RequestUser UserDto.UserInfo userInfo) {
        return userInfo.getEmail();
    }
}
