package com.morakmorak.morak_back_end.Integration.point;

import com.morakmorak.morak_back_end.controller.UserController;
import com.morakmorak.morak_back_end.entity.Avatar;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.NotificationService;
import com.morakmorak.morak_back_end.service.auth_user_service.PointService;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ROLE_USER_LIST;
import static com.morakmorak.morak_back_end.util.TestConstants.EMAIL1;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
public class GetPointTest {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    NotificationService notificationService;

    @Autowired
    EntityManager em;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserController userController;

    @Autowired
    PointService pointService;
    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("유효한 조회 요청의 경우 200 반환")
    void getPoints_success() throws Exception {
        //given 기존에 유저가 존재, 포인트도 존재
        User user = User.builder().avatar(Avatar.builder().originalFilename("sdaflkjasdflkajsfl").remotePath("asfdladfjlafljf").build()).nickname("백엔드엔드").grade(Grade.CANDLE).point(100).email(EMAIL1).build();
        em.persist(user);
        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, user.getId(), ROLE_USER_LIST, "백엔드엔드");
        //when 유효한 아이디로 조회 요청 시
        ResultActions result = mockMvc.perform(
                get("/users/points")
                        .header(JWT_HEADER, accessToken)
                        .header("User-Agent", "Mozilla 5.0")
        );

        //then 정확한 dto 반환
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.point").exists())
                .andExpect(jsonPath("$.userInfo.userId").exists())
                .andExpect(jsonPath("$.userInfo.nickname").exists())
                .andExpect(jsonPath("$.userInfo.grade").exists())
                .andExpect(jsonPath("$.avatar.avatarId").exists())
                .andExpect(jsonPath("$.avatar.filename").exists())
                .andExpect(jsonPath("$.avatar.remotePath").exists());
    }

    @Test
    @DisplayName("존재하지 않는 유저의 조회 요청의 경우 404 반환")
    void getPoints_failed() throws Exception {
        //given 유저가 삭제됨
        User user = User.builder().nickname("백엔드엔드").point(100).email(EMAIL1).build();
        em.persist(user);
        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, user.getId(), ROLE_USER_LIST, "백엔드엔드");
        userRepository.delete(user);
        //when 존재하지 않는 유저 정보가 담긴 토큰으로 조회 요청
        ResultActions result = mockMvc.perform(
                get("/users/points")
                        .header(JWT_HEADER, accessToken)
                        .header("User-Agent", "Mozilla 5.0")
        );

        //then 404 반환
        result.andExpect(status().isNotFound());
    }
}
