package com.morakmorak.morak_back_end.entity;

import com.morakmorak.morak_back_end.domain.PointCalculator;
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

    @Mock
    PointCalculator pointCalculator;

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
    @DisplayName("comparePassword/ 평문의 패스워드와 비교했을 때, 패스워드가 다르다면 false를 반환한다.")
    public void test3() {
        //given
        User user = User
                .builder()
                .password(PASSWORD1)
                .build();

        given(passwordEncoder.matches(PASSWORD2, PASSWORD1)).willReturn(Boolean.FALSE);

        //when
        boolean result = user.comparePassword(passwordEncoder, PASSWORD2);

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("comparePassword/ 평문의 패스워드와 비교했을 때, 패스워드가 같다면 true를 반환한다.")
    public void test4() {
        //given
        User user = User
                .builder()
                .password(PASSWORD1)
                .build();

        given(passwordEncoder.matches(PASSWORD1, PASSWORD1)).willReturn(Boolean.TRUE);

        //when
        boolean result = user.comparePassword(passwordEncoder, PASSWORD1);

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("user의 avatar가 변경된다.")
    void injectAvatar() {
        //given
        User user = User.builder().build();
        Avatar avatar = Avatar.builder().remotePath(CONTENT1).build();

        //when
        user.injectAvatar(avatar);

        //then
        assertThat(user.getAvatar().getRemotePath()).isEqualTo(CONTENT1);
    }

    @Test
    @DisplayName("user의 articles에 article이 추가된다")
    void addArticle() {
        //given
        User user = User.builder().build();
        Article article = Article.builder().title(CONTENT1).build();

        //when
        user.addArticle(article);

        //then
        assertThat(user.getArticles().get(0).getTitle().equals(CONTENT1)).isTrue();
    }

    @Test
    @DisplayName("pointCalculator가 반환하는 값만큼 point가 더해진다.")
    void addPoint() {
        //given
        User user = User.builder().build();
        Article article = Article.builder().title(CONTENT1).build();
        given(pointCalculator.calculatePaymentPoint(article)).willReturn(10);

        //when
        user.addPoint(article, pointCalculator);

        //then
        assertThat(user.getPoint()).isEqualTo(10);
    }
}