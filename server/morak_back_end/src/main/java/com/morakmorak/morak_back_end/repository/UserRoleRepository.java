package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}
