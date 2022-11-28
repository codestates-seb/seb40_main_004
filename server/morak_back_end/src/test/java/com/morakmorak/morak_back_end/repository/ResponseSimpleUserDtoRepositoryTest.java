package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
public class ResponseSimpleUserDtoRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("어떤 이메일을 가진 유저를 저장하고, " +
            "해당 이메일로 유저를 조회하면 저장했을 때와 같은 속성값을 가진다.")
    public void test1() {
        //given
        User user = User
                .builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        userRepository.save(user);

        //when
        User findUser = userRepository
                .findUserByEmail(EMAIL1)
                        .orElseThrow(() -> new IllegalArgumentException());

        //then
        assertThat(findUser.getEmail()).isEqualTo(EMAIL1);
        assertThat(findUser.getPassword()).isEqualTo(PASSWORD1);
    }

    @Test
    @DisplayName("존재하지 않는 이메일을 조회하면 예외가 발생한다")
    public void test2() {
        //given when then
        assertThatThrownBy(()-> {
            userRepository
                    .findUserByEmail(EMAIL1)
                    .orElseThrow(() -> new IllegalArgumentException());
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("존재하지 않는 닉네임으로 유저를 조회할 경우 Optional.empty를 반환한다.")
    public void findByUserNickname1() {
        //given
        //when
        Optional<User> optional = userRepository.findUserByNickname(NICKNAME1);

        //then
        assertThat(optional).isEmpty();
    }

    @Test
    @DisplayName("존재하는 닉네임으로 유저를 조회할 경우 같은 닉네임을 가진 유저를 반환한다.")
    public void findByUserNickname2() {
        //given
        User user = User.builder()
                .nickname(NICKNAME1)
                .build();

        userRepository.save(user);

        //when
        Optional<User> optional = userRepository.findUserByNickname(NICKNAME1);

        //then
        assertThat(optional.get().getNickname()).isEqualTo(NICKNAME1);
    }
}
