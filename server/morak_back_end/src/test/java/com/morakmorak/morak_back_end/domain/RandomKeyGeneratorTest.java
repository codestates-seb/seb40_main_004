package com.morakmorak.morak_back_end.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static com.morakmorak.morak_back_end.security.util.SecurityConstants.*;

class RandomKeyGeneratorTest {
    RandomKeyGenerator randomKeyGenerator;

    @BeforeEach
    void init() {
        randomKeyGenerator = new RandomKeyGenerator();
    }

    @Test
    @DisplayName("generateMailAuthKey로 매번 다른 임시 인증번호를 생성한다.")
    void generateMailAuthKey() {
        //given
        int size = 20;
        Set<String> mailAuthKeySet = new HashSet<>();

        //when
        for (int i=0; i<size; i++) {
            mailAuthKeySet.add(randomKeyGenerator.generateMailAuthKey());
        }

        //then
        Assertions.assertThat(mailAuthKeySet.size()).isEqualTo(size);
    }

    @Test
    @DisplayName("generateTemporaryPassword 메서드로 매번 다른 임시 비밀번호를 생성한다.")
    void generateTemporaryPassword() {
        //given
        int size = 20;
        Set<String> temporaryPasswordSet = new HashSet<>();

        //when
        for (int i=0; i<size; i++) {
            temporaryPasswordSet.add(randomKeyGenerator.generateTemporaryPassword());
        }

        //then
        Assertions.assertThat(temporaryPasswordSet.size()).isEqualTo(size);
    }

    @Test
    @DisplayName("generateMailAuthKey로 생성되는 임시번호의 길이는 늘 일정하다.")
    void generateMailAuthKey_length() {
        //given
        int size = 20;
        Set<String> mailAuthKeySet = new HashSet<>();

        //when
        for (int i=0; i<size; i++) {
            mailAuthKeySet.add(randomKeyGenerator.generateMailAuthKey());
        }

        //then
        mailAuthKeySet.forEach(
                e -> Assertions.assertThat(e.length()).isEqualTo(EMAIL_AUTH_KEY_LENGTH)
        );
    }

    @Test
    @DisplayName("generateTemporaryPassword 생성되는 임시번호의 길이는 늘 일정하다.")
    void generateTemporaryPassword_length() {
        //given
        int size = 20;
        Set<String> temporaryPasswordSet = new HashSet<>();

        //when
        for (int i=0; i<size; i++) {
            temporaryPasswordSet.add(randomKeyGenerator.generateTemporaryPassword());
        }

        //then
        temporaryPasswordSet.forEach(
                e -> Assertions.assertThat(e.length()).isEqualTo(TEMPORARY_PASSWORD_LENGTH)
        );
    }
}