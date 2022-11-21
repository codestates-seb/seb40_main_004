package com.morakmorak.morak_back_end.Integration.notification;

import com.morakmorak.morak_back_end.controller.NotificationController;
import com.morakmorak.morak_back_end.dto.NotificationDto;
import com.morakmorak.morak_back_end.dto.ResponseMultiplePaging;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.Notification;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.DomainType;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.NotificationService;
import com.morakmorak.morak_back_end.util.SecurityTestConstants;
import com.morakmorak.morak_back_end.util.TestConstants;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
public class NotificationTest {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    NotificationService notificationService;

    @Autowired
    EntityManager em;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    NotificationController notificationController;

    User user;

    String accessToken;

    @BeforeEach
    void init() {
        user = User.builder().build();

        for (int i=0; i<30; i++) {
            Notification notification = Notification.builder()
                    .domainId((long) i)
                    .message(String.valueOf(i))
                    .isChecked(i % 2 == 0 ? Boolean.TRUE : Boolean.FALSE)
                    .domainType(DomainType.ARTICLE)
                    .user(user)
                    .build();

            notification.mapUserAndNotification();
            em.persist(notification);
            em.persist(user);
        }

        accessToken = jwtTokenUtil.createAccessToken(EMAIL1, user.getId(), ROLE_USER_LIST);
    }

    @Test
    @DisplayName("페이지 및 스테이터스 정상 반환 테스 ")
    void getNotifications() throws Exception {
        // given
        // when
        ResultActions perform = mockMvc.perform(get("/notifications?page=1&size=10")
                .header(JWT_HEADER, accessToken));

        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.pageInfo.size", is(10)))
                .andExpect(jsonPath("$.pageInfo.page", is(1)))
                .andExpect(jsonPath("$.pageInfo.totalElements", is(30)))
                .andExpect(jsonPath("$.pageInfo.totalPages", is(3)));
    }

    @Test
    @DisplayName("내림차순 반환 테스트")
    void getNotifications2() throws Exception {
        //given
        UserDto.UserInfo token = UserDto.UserInfo.builder()
                .id(user.getId())
                .email(EMAIL1)
                .roles(ROLE_USER_LIST)
                .build();

        //when
        ResponseMultiplePaging<NotificationDto.SimpleResponse> notifications = notificationController.getNotifications(1, 10,token);

        //then
        assertThat(notifications.getData().size()).isEqualTo(10);
        assertThat(notifications.getData().get(0).getMessage()).isEqualTo("29");
        assertThat(notifications.getData().get(9).getMessage()).isEqualTo("20");
    }
}