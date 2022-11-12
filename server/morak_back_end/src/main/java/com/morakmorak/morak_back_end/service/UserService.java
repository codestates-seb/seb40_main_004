package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findVerifiedUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
    }
}
