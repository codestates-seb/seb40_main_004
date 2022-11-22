package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.dto.NotificationDto;
import com.morakmorak.morak_back_end.dto.ResponseMultiplePaging;
import com.morakmorak.morak_back_end.entity.Notification;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.repository.notification.NotificationQueryRepository;
import com.morakmorak.morak_back_end.repository.notification.NotificationRepository;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.morakmorak.morak_back_end.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationQueryRepository notificationQueryRepository;
    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public ResponseMultiplePaging<NotificationDto.SimpleResponse> findNotificationsBy(Long userId, PageRequest pageRequest) {
        userService.findVerifiedUserById(userId);
        Page<NotificationDto.SimpleResponse> notifications = notificationQueryRepository.getNotifications(userId, pageRequest);

        return new ResponseMultiplePaging<>(notifications.getContent(), notifications);
    }

    public String findNotificationUriBy(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new BusinessLogicException(NOTIFICATION_NOT_FOUND));
        notification.changeCheckStatus();
        return notification.getUri();
    }

    public void deleteNotificationData(Long notificationId, Long userID) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new BusinessLogicException(NOTIFICATION_NOT_FOUND));
        if (!Objects.equals(notification.getUser().getId(), userID)) throw new BusinessLogicException(NO_ACCESS_TO_THAT_OBJECT);
        notificationRepository.delete(notification);
    }
}
