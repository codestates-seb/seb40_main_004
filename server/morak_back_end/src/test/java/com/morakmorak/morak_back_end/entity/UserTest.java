package com.morakmorak.morak_back_end.entity;

import com.morakmorak.morak_back_end.domain.PointCalculator;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.morakmorak.morak_back_end.entity.enums.Grade.*;
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

    @Test
    @DisplayName("pointCalculator가 반환하는 값만큼 point가 감소한다.")
    void minusPoint() {
        //given
        User user = User.builder().build();
        Article article = Article.builder().title(CONTENT1).build();
        given(pointCalculator.calculatePaymentPoint(article)).willReturn(10);

        //when
        user.minusPoint(article, pointCalculator);

        //then
        assertThat(user.getPoint()).isEqualTo(-10);
    }

    @Test
    @DisplayName("유저의 포인트가 20000점 이상이라면 Grade.MORAKMORAK을 반환한다.")
    void checkGradeUpdatable1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //given
        User user = User.builder().point(20000).build();
        Method checkGradeUpdatable = user.getClass().getDeclaredMethod("checkGradeUpdatable");
        checkGradeUpdatable.setAccessible(true);

        //when
        Grade result = (Grade) checkGradeUpdatable.invoke(user);

        //then
        assertThat(result).isEqualTo(MORAKMORAK);
    }

    @Test
    @DisplayName("유저의 포인트가 10000점 이상, 20000점 미만이라면 Grade.BONFIRE 반환한다.")
    void checkGradeUpdatable2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //given
        User user1 = User.builder().point(19999).build();
        User user2 = User.builder().point(10000).build();
        Method checkGradeUpdatable = user1.getClass().getDeclaredMethod("checkGradeUpdatable");
        checkGradeUpdatable.setAccessible(true);

        //when
        Grade result1 = (Grade) checkGradeUpdatable.invoke(user1);
        Grade result2 = (Grade) checkGradeUpdatable.invoke(user2);

        //then
        assertThat(result1).isEqualTo(BONFIRE);
        assertThat(result2).isEqualTo(BONFIRE);
    }

    @Test
    @DisplayName("유저의 포인트가 5000점 이상, 10000점 미만이라면 Grade.BONFIRE 반환한다.")
    void checkGradeUpdatable3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //given
        User user1 = User.builder().point(5000).build();
        User user2 = User.builder().point(9999).build();
        Method checkGradeUpdatable = user1.getClass().getDeclaredMethod("checkGradeUpdatable");
        checkGradeUpdatable.setAccessible(true);

        //when
        Grade result1 = (Grade) checkGradeUpdatable.invoke(user1);
        Grade result2 = (Grade) checkGradeUpdatable.invoke(user2);

        //then
        assertThat(result1).isEqualTo(CANDLE);
        assertThat(result2).isEqualTo(CANDLE);
    }

    @Test
    @DisplayName("유저의 포인트가 5000점 미만이라면 Grade.MATCH를 반환한다.")
    void checkGradeUpdatable4() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //given
        User user1 = User.builder().point(4999).build();
        User user2 = User.builder().point(0).build();
        Method checkGradeUpdatable = user1.getClass().getDeclaredMethod("checkGradeUpdatable");
        checkGradeUpdatable.setAccessible(true);

        //when
        Grade result1 = (Grade) checkGradeUpdatable.invoke(user1);
        Grade result2 = (Grade) checkGradeUpdatable.invoke(user2);

        //then
        assertThat(result1).isEqualTo(MATCH);
        assertThat(result2).isEqualTo(MATCH);
    }

    @Test
    @DisplayName("checkGradeUpdatable의 반환값에 따라 유저의 등급이 변경된다.")
    void updateGrade() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //given
        User user1 = User.builder().point(4999).build();
        User user2 = User.builder().point(0).build();
        Method updateGrade = user1.getClass().getDeclaredMethod("updateGrade");
        updateGrade.setAccessible(true);

        //when
        updateGrade.invoke(user1);
        updateGrade.invoke(user2);

        //then
        assertThat(user1.getGrade()).isEqualTo(MATCH);
        assertThat(user2.getGrade()).isEqualTo(MATCH);
    }

    @Test
    @DisplayName("checkGradeUpdatable의 반환값에 따라 유저의 등급이 변경된다.")
    void updateGrade2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //given
        User user1 = User.builder().point(5000).build();
        User user2 = User.builder().point(9999).build();
        Method updateGrade = user1.getClass().getDeclaredMethod("updateGrade");
        updateGrade.setAccessible(true);

        //when
        updateGrade.invoke(user1);
        updateGrade.invoke(user2);

        //then
        assertThat(user1.getGrade()).isEqualTo(CANDLE);
        assertThat(user2.getGrade()).isEqualTo(CANDLE);
    }

    @Test
    @DisplayName("checkGradeUpdatable의 반환값에 따라 유저의 등급이 변경된다.")
    void updateGrade3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //given
        User user1 = User.builder().point(10000).build();
        User user2 = User.builder().point(19999).build();
        Method updateGrade = user1.getClass().getDeclaredMethod("updateGrade");
        updateGrade.setAccessible(true);

        //when
        updateGrade.invoke(user1);
        updateGrade.invoke(user2);

        //then
        assertThat(user1.getGrade()).isEqualTo(BONFIRE);
        assertThat(user2.getGrade()).isEqualTo(BONFIRE);
    }

    @Test
    @DisplayName("checkGradeUpdatable의 반환값에 따라 유저의 등급이 변경된다.")
    void updateGrade4() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //given
        User user1 = User.builder().point(20000).build();
        User user2 = User.builder().point(30000).build();
        Method updateGrade = user1.getClass().getDeclaredMethod("updateGrade");
        updateGrade.setAccessible(true);

        //when
        updateGrade.invoke(user1);
        updateGrade.invoke(user2);

        //then
        assertThat(user1.getGrade()).isEqualTo(MORAKMORAK);
        assertThat(user2.getGrade()).isEqualTo(MORAKMORAK);
    }

    @Test
    @DisplayName("포인트 감소 시 포인트에 따라 등급이 변경된다")
    void minusPoint2() {
        //given
        User user = User.builder().point(50000).grade(MORAKMORAK).build();
        Article article = Article.builder().title(CONTENT1).build();
        given(pointCalculator.calculatePaymentPoint(article)).willReturn(49000);

        //when
        user.minusPoint(article, pointCalculator);

        //then
        assertThat(user.getGrade()).isEqualTo(MATCH);
    }

    @Test
    @DisplayName("pointCalculator가 반환하는 값만큼 point가 더해진다.")
    void addPoint2() {
        //given
        User user = User.builder().point(0).grade(MATCH).build();
        Article article = Article.builder().title(CONTENT1).build();
        given(pointCalculator.calculatePaymentPoint(article)).willReturn(49000);

        //when
        user.addPoint(article, pointCalculator);

        //then
        assertThat(user.getGrade()).isEqualTo(MORAKMORAK);
    }

    @Test
    @DisplayName("setRandomEmail() 수행 전후로 이메일이 다르다.")
    void setRandomEmail1() {
        // given
        User user = User.builder().email(EMAIL1).build();

        // when
        user.setRandomEmail();

        // then
        assertThat(user.getEmail()).isNotEqualTo(EMAIL1);
    }

    @Test
    @DisplayName("같은 메일을 가진 유저가 각각 setRandomEmail()을 수행해도 각 이메일은 모두 다르다")
    void setRandomEmail2() {
        // given
        List<User> users = new ArrayList<>();

        // when
        for (int i=0; i<1000; i++) {
            User user = User.builder().email(EMAIL1).build();
            user.setRandomEmail();
            users.add(user);
        }

        // then
        assertThat(users).doesNotHaveDuplicates();
    }
}