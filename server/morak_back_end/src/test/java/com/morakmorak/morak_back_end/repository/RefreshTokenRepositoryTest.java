package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.RefreshToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)

public class RefreshTokenRepositoryTest {
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("db에 같은 값을 가진 토큰이 존재하지 않는다면 Optional.empty를 반환한다.")
    void test1() {
        //given
        //when
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findRefreshTokenByValue(REFRESH_TOKEN);

        //then
        assertThat(refreshToken).isEmpty();
    }

    @Test
    @DisplayName("db에 같은 값을 가진 토큰이 존재한다면 해당 토큰을 반환한다.")
    void test2() {
        //given
        RefreshToken dbToken = RefreshToken.builder()
                .value(REFRESH_TOKEN)
                .build();

        refreshTokenRepository.save(dbToken);

        //when
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findRefreshTokenByValue(REFRESH_TOKEN);

        //then
        assertThat(refreshToken.isPresent()).isTrue();
        assertThat(refreshToken.get().getValue()).isEqualTo(REFRESH_TOKEN);
    }
}
