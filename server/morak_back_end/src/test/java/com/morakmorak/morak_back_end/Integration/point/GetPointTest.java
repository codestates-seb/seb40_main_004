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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ROLE_USER_LIST;
import static com.morakmorak.morak_back_end.util.TestConstants.EMAIL1;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
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
    @DisplayName("????????? ?????? ????????? ?????? 200 ??????")
    void getPoints_success() throws Exception {
        //given ????????? ????????? ??????, ???????????? ??????
        User user = User.builder().avatar(Avatar.builder().originalFilename("sdaflkjasdflkajsfl").remotePath("asfdladfjlafljf").build()).nickname("???????????????").grade(Grade.CANDLE).point(100).email(EMAIL1).build();
        em.persist(user);
        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, user.getId(), ROLE_USER_LIST, "???????????????");
        //when ????????? ???????????? ?????? ?????? ???
        ResultActions result = mockMvc.perform(
                get("/users/points")
                        .header(JWT_HEADER, accessToken)
                        .header("User-Agent", "Mozilla 5.0")
        );

        //then ????????? dto ??????
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
    @DisplayName("???????????? ?????? ????????? ?????? ????????? ?????? 404 ??????")
    void getPoints_failed() throws Exception {
        //given ????????? ?????????
        User user = User.builder().nickname("???????????????").point(100).email(EMAIL1).build();
        em.persist(user);
        String accessToken = jwtTokenUtil.createAccessToken(EMAIL1, user.getId(), ROLE_USER_LIST, "???????????????");
        userRepository.delete(user);
        //when ???????????? ?????? ?????? ????????? ?????? ???????????? ?????? ??????
        ResultActions result = mockMvc.perform(
                get("/users/points")
                        .header(JWT_HEADER, accessToken)
                        .header("User-Agent", "Mozilla 5.0")
        );

        //then 404 ??????
        result.andExpect(status().isNotFound());
    }
}
