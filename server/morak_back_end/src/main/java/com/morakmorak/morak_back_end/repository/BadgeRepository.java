package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Badge;
import com.morakmorak.morak_back_end.entity.enums.BadgeName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    Optional<Badge> findBadgeByName(BadgeName name);
}
