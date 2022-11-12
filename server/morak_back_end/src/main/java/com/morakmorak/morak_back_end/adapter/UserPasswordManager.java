package com.morakmorak.morak_back_end.adapter;

import com.morakmorak.morak_back_end.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPasswordManager {
    private final PasswordEncoder passwordEncoder;

    public String encryptUserPassword(User user) {
        return user.encryptPassword(passwordEncoder);
    }

    public boolean compareUserPassword(User entityUser, User requestUser) {
        return entityUser.comparePassword(passwordEncoder, requestUser);
    }
}
