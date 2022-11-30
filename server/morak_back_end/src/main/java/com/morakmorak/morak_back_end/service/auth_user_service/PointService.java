package com.morakmorak.morak_back_end.service.auth_user_service;

import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PointService {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserDto.ResponsePoint getRemainingPoint(Long userId) {
        User user = userService.findVerifiedUserById(userId);
        return userMapper.toResponsePoint(user);
    }
}
