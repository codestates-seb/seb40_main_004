package com.morakmorak.morak_back_end.domain;

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

    public Boolean comparePasswordWithUser(User entityUser, User requestUser) {
        return entityUser.comparePassword(passwordEncoder, requestUser.getPassword());
    }

    public Boolean comparePasswordWithPlainPassword(User entityUser, String plainPassword) {
        return entityUser.comparePassword(passwordEncoder, plainPassword);
    }

    public String changeUserPassword(User user, String newPassword) {
        user.changePassword(newPassword);
        return encryptUserPassword(user);
    }
}
