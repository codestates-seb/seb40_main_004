package com.morakmorak.morak_back_end.service.notification_service;

import com.morakmorak.morak_back_end.dto.NotificationDto;
import com.morakmorak.morak_back_end.dto.PageInfo;
import com.morakmorak.morak_back_end.dto.ResponseMultiplePaging;
import com.morakmorak.morak_back_end.entity.Notification;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.repository.notification.NotificationQueryRepository;
import com.morakmorak.morak_back_end.repository.notification.NotificationRepository;
import com.morakmorak.morak_back_end.service.NotificationService;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import com.morakmorak.morak_back_end.util.TestConstants;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static com.morakmorak.morak_back_end.exception.ErrorCode.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    @InjectMocks
    NotificationService notificationService;

    @Mock
    NotificationQueryRepository notificationQueryRepository;

    @Mock
    NotificationRepository notificationRepository;

    @Mock
    UserService userService;

    @Test
    @DisplayName("알림 조회 시 아이디로 유저를 찾을 수 없다면 BusinessLogicException 발생")
    void getNotifications_failed() {
        //given
        given(userService.findVerifiedUserById(ID1)).willThrow(new BusinessLogicException(USER_NOT_FOUND));

        //when //then
        assertThatThrownBy(() -> userService.findVerifiedUserById(ID1)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("알림 조회 시 성공적으로 유저를 찾는다면 해당하는 값 반환")
    void getNotifications_success() {
        //given
        User user = User.builder().id(ID1).build();
        given(userService.findVerifiedUserById(ID1)).willReturn(user);
        PageRequest pageable = PageRequest.of(0, 10);

        NotificationDto.SimpleResponse dto1 = NotificationDto.SimpleResponse.builder().build();
        NotificationDto.SimpleResponse dto2 = NotificationDto.SimpleResponse.builder().build();

        PageImpl<NotificationDto.SimpleResponse> result = new PageImpl<>(List.of(dto1, dto2), pageable, 2L);

        given(notificationQueryRepository.getNotifications(ID1, pageable)).willReturn(result);

        //when
        ResponseMultiplePaging<NotificationDto.SimpleResponse> when = notificationService.findNotificationsBy(ID1, pageable);

        //then
        assertThat(when.getData().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("해당 Notification Id를 찾지 못할 경우 BusinessLogicException 발생")
    void getNotificationUri_failed() {
        //given
        given(notificationRepository.findById(ID1)).willReturn(Optional.empty());

        //when //then
        assertThatThrownBy(() -> notificationService.findNotificationUriBy(ID1)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("로직이 정상적으로 수행될 경우 해당하는 id를 가진 Notification의 Uri를 반환하고 isCheked를 true로 변환한다.")
    void getNotificationUri_success() {
        //given
        Notification notification = Notification.builder().uri("/ddd/ddd").build();
        given(notificationRepository.findById(ID1)).willReturn(Optional.of(notification));
        //when
        String result = notificationService.findNotificationUriBy(ID1);
        //then
        assertThat(result).isEqualTo(notification.getUri());
        assertThat(notification.getIsChecked()).isTrue();
    }
}
