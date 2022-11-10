package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findRefreshTokenByValue(String value);
}
