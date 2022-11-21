package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.dto.NotificationDto;
import com.morakmorak.morak_back_end.dto.ResponseMultiplePaging;
import com.morakmorak.morak_back_end.repository.notification.NotificationQueryRepository;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationQueryRepository notificationQueryRepository;
    private final UserService userService;

    public ResponseMultiplePaging<NotificationDto.SimpleResponse> findNotificationsBy(Long userId, PageRequest pageRequest) {
        userService.findVerifiedUserById(userId);
        Page<NotificationDto.SimpleResponse> notifications = notificationQueryRepository.getNotifications(userId, pageRequest);

        return new ResponseMultiplePaging<>(notifications.getContent(), notifications);
    }
}
