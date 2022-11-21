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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseMultiplePaging<NotificationDto.SimpleResponse> getNotifications(
                                                                                      @Param("page") Integer page,
                                                                                      @Param("size") Integer size,
                                                                                      @RequestUser UserDto.UserInfo userInfo) {
        PageRequest pageRequest = PageRequestGenerator.of(page, size);
        return notificationService.findNotificationsBy(userInfo.getId(), pageRequest);
    }
}
