package com.morakmorak.morak_back_end.repository.user;

import com.morakmorak.morak_back_end.entity.Role;
import com.morakmorak.morak_back_end.entity.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleByRoleName(RoleName roleName);
}
