package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
}
