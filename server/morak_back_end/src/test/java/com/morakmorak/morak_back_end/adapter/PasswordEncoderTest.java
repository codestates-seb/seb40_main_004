package com.morakmorak.morak_back_end.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.morakmorak.morak_back_end.util.TestConstants.PASSWORD1;
import static com.morakmorak.morak_back_end.util.TestConstants.PASSWORD2;
import static org.assertj.core.api.Assertions.assertThat;

public class PasswordEncoderTest {
    PasswordEncoder passwordEncoder;

    @BeforeEach
    public void init() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    @DisplayName("패스워드를 인코드하면 전/후의 값이 다르다.")
    public void test1() {
        //given
        String encodedPwd = passwordEncoder.encode(PASSWORD1);

        //when
        boolean result = PASSWORD1.matches(encodedPwd);

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("각자 다른 평문을 암호화하고 mathces 비교하면 false를 반환한다")
    public void test2() {
        //given
        String encodedPwd = passwordEncoder.encode(PASSWORD1);
        String encodedPwd2 = passwordEncoder.encode(PASSWORD2);

        //when
        boolean result = encodedPwd.matches(encodedPwd2);

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("서로 같은 평문을 암호화하고 matches 비교하면 false를 반환한다.")
    public void test3() {
        //given
        String encodedPwd = passwordEncoder.encode(PASSWORD1);
        String encodedPwd2 = passwordEncoder.encode(PASSWORD1);

        //when
        boolean result = encodedPwd.matches(encodedPwd2);

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("평문을 암호화하고 다른 평문과 passwordEncoder.matches로 비교하면 false를 반환한다.")
    public void test4() {
        //given
        String encodedPwd = passwordEncoder.encode(PASSWORD1);

        //when
        boolean result = passwordEncoder.matches(PASSWORD2, encodedPwd);

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("평문을 암호화하고 같은 평문과 passwordEncoder.matches로 비교하면 true를 반환한다.")
    public void test5() {
        //given
        String encodedPwd = passwordEncoder.encode(PASSWORD1);

        //when
        boolean result = passwordEncoder.matches(PASSWORD1, encodedPwd);

        //then
        assertThat(result).isTrue();
    }
}
