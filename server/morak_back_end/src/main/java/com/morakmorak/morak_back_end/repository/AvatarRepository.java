package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvatarRepository extends JpaRepository<Avatar,Long> {

    Optional<Avatar> findByUserId(Long userId);
}
