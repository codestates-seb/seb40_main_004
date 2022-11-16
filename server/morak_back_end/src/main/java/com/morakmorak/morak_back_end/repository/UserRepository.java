package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.dto.ActivityDto;
import com.morakmorak.morak_back_end.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByNickname(String nickname);
}
