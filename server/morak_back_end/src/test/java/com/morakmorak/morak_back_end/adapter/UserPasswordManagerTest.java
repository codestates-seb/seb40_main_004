package com.morakmorak.morak_back_end.adapter;


import com.morakmorak.morak_back_end.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ONE;
import static com.morakmorak.morak_back_end.util.TestConstants.PASSWORD1;
import static com.morakmorak.morak_back_end.util.TestConstants.PASSWORD2;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserPasswordManagerTest {
    UserPasswordManager userPasswordManager;

    @Mock
    PasswordEncoder passwordEncoder;

    User user1;

    User user2;

    @BeforeEach
    public void init() {
        userPasswordManager = new UserPasswordManager(passwordEncoder);
        user1 = User.builder().password(PASSWORD1).build();
        user2 = User.builder().password(PASSWORD2).build();
    }

    @Test
    @DisplayName("compareUserPassword/ passwordEncoder가 주어진 인자로 matches를 1회 수행한다")
    public void test1() {
        //given
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        //when
        userPasswordManager.compareUserPassword(user1, user2);

        //then
        verify(passwordEncoder, times(ONE)).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("compareUserPassword/ passwordEncoder가 주어진 인자로 encode를 1회 수행한다")
    public void test2() {
        //given
        given(passwordEncoder.encode(anyString())).willReturn(anyString());

        //when
        userPasswordManager.encryptUserPassword(user1);

        //then
        verify(passwordEncoder, times(ONE)).encode(anyString());
    }
}