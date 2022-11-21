package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.controller.utility.PageRequestGenerator;
import com.morakmorak.morak_back_end.dto.NotificationDto;
import com.morakmorak.morak_back_end.dto.ResponseMultiplePaging;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.security.resolver.RequestUser;
import com.morakmorak.morak_back_end.service.NotificationService;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final String BASE_URL = "localhost:8080.com";

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseMultiplePaging<NotificationDto.SimpleResponse> getNotifications(
                                                                                      @Param("page") Integer page,
                                                                                      @Param("size") Integer size,
                                                                                      @RequestUser UserDto.UserInfo userInfo) {
        PageRequest pageRequest = PageRequestGenerator.of(page, size);
        return notificationService.findNotificationsBy(userInfo.getId(), pageRequest);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.PERMANENT_REDIRECT)
    public void getNotificationUri(@PathVariable Long id, HttpServletResponse response) {
        String redirect_uri = notificationService.findNotificationUriBy(id);
        response.setHeader("Location",BASE_URL + redirect_uri);
    }
}
