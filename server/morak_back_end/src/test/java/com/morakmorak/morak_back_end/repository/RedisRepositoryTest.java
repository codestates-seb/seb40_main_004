package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.config.RedisContainerTest;
import com.morakmorak.morak_back_end.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static com.morakmorak.morak_back_end.security.util.SecurityConstants.REFRESH_TOKEN_EXPIRE_COUNT;
import static com.morakmorak.morak_back_end.util.TestConstants.NICKNAME1;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RedisRepositoryTest extends RedisContainerTest {
    @Autowired
    RedisRepository<User> redisRepository;

    private final String KEY = "TEST_DATA";

    @BeforeEach
    public void deleteData() {
        redisRepository.deleteData(KEY);
    }

    @Test
    @DisplayName("저장 성공시 true를 반환한다.")
    public void test1() {
        //given
        User user = User
                .builder()
                .email("email")
                .build();

        //when
        boolean result = redisRepository.saveData(KEY, user, REFRESH_TOKEN_EXPIRE_COUNT);

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 data를 요청하면 Optional.empty를 반환한다.")
    public void test2() {
        //given
        //when
        Optional<User> data = redisRepository.getData(KEY, User.class);

        //then
        assertThat(data).isEmpty();
    }

    @Test
    @DisplayName("키와 값을 저장하고 같은 키로 데이터를 요청하면 저장한 객체를 반환한다.")
    public void test3() {
        //given
        User user = User
                .builder()
                .nickname(NICKNAME1)
                .build();

        boolean bool = redisRepository.saveData(KEY, user, REFRESH_TOKEN_EXPIRE_COUNT);

        //when
        Optional<User> data = (Optional<User>) redisRepository.getData(KEY, User.class);

        //then
        assertThat(bool).isTrue();
        assertThat(data.get().getNickname()).isEqualTo(NICKNAME1);
    }

    @Test
    @DisplayName("존재하는 데이터를 삭제하면 true를 반환한다.")
    public void test4() {
        //given
        User user = User
                .builder()
                .nickname(NICKNAME1)
                .build();

        boolean bool = redisRepository.saveData(KEY, user, REFRESH_TOKEN_EXPIRE_COUNT);
        //when
        boolean result = redisRepository.deleteData(KEY);

        //then
        assertThat(bool).isTrue();
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 데이터를 삭제하면 false를 반환한다.")
    public void test5() {
        //given
        //when
        boolean result = redisRepository.deleteData(KEY);

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("존재하는 데이터를 getDataAndDelete로 조회하면 다시 조회했을 때 Optional.empty를 반환한다.")
    public void test6() {
        //given
        User user = User
                .builder()
                .nickname(NICKNAME1)
                .build();

        boolean saveBool = redisRepository.saveData(KEY, user, REFRESH_TOKEN_EXPIRE_COUNT);

        //when
        Optional<User> findData = redisRepository.getDataAndDelete(KEY, User.class);
        Optional<User> result = redisRepository.getData(KEY, User.class);

        //then
        assertThat(saveBool).isTrue();
        assertThat(findData.get().getNickname()).isEqualTo(user.getNickname());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("유효기간이 지난 데이터를 조회하면 Optional.empty를 반환한다.")
    public void test7() throws InterruptedException {
        //given
        User user = User
                .builder()
                .nickname(NICKNAME1)
                .build();

        Long expire = 1L;

        boolean saveBool = redisRepository.saveData(KEY, user, expire);
        Thread.sleep(500);

        //when
        Optional data = redisRepository.getData(KEY, User.class);

        //then
        assertThat(data).isEmpty();
    }
}