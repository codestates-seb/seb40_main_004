package com.morakmorak.morak_back_end.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserTest {
    @Mock
    PasswordEncoder passwordEncoder;

    private String ENCODED_PASSWORD;

    @BeforeEach
    public void init() {
        ENCODED_PASSWORD = "ENCODED_PASSWORD";
    }

    @Test
    @DisplayName("encryptionPassword/ 패스워드 암호화 실행한 후 패스워드는 이전과 같지 않다.")
    public void test1() {
        //given
        User user = User
                .builder()
                .password(PASSWORD1)
                .build();

        given(passwordEncoder.encode(user.getPassword())).willReturn(ENCODED_PASSWORD);

        //when
        user.encryptPassword(passwordEncoder);

        //then
        assertThat(user.getPassword()).isNotEqualTo(PASSWORD1);
    }

    @Test
    @DisplayName("encryptionPassword/ 암호화 실행 후 패스워드는 passwordEncoder로 암호화된 값과 같다.")
    public void test2() {
        //given
        User user = User
                .builder()
                .password(PASSWORD1)
                .build();

        given(passwordEncoder.encode(user.getPassword())).willReturn(ENCODED_PASSWORD);

        //when
        user.encryptPassword(passwordEncoder);

        //then
        assertThat(user.getPassword()).isEqualTo(ENCODED_PASSWORD);
    }

    @Test
    @DisplayName("comparePassword/ 다른 유저의 패스워드와 비교했을 때, 패스워드가 다르다면 false를 반환한다.")
    public void test3() {
        //given
        User user = User
                .builder()
                .password(PASSWORD1)
                .build();

        User user2 = User
                .builder()
                .password(PASSWORD2)
                .build();

        given(passwordEncoder.matches(PASSWORD2, PASSWORD1)).willReturn(Boolean.FALSE);

        //when
        boolean result = user.comparePassword(passwordEncoder, user2);

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("comparePassword/ 다른 유저의 패스워드와 비교했을 때, 패스워드가 같다면 true를 반환한다.")
    public void test4() {
        //given
        User user = User
                .builder()
                .password(PASSWORD1)
                .build();

        User user2 = User
                .builder()
                .password(PASSWORD1)
                .build();

        given(passwordEncoder.matches(PASSWORD1, PASSWORD1)).willReturn(Boolean.TRUE);

        //when
        boolean result = user.comparePassword(passwordEncoder, user2);

        //then
        assertThat(result).isTrue();
    }
}