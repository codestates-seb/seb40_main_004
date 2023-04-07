package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Role;
import com.morakmorak.morak_back_end.entity.enums.RoleName;
import com.morakmorak.morak_back_end.repository.user.RoleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)

public class RoleRepositoryTest {
    @Autowired
    RoleRepository roleRepository;

    @BeforeEach
    public void init() {
        roleRepository.save(new Role(RoleName.ROLE_USER));
        roleRepository.save(new Role(RoleName.ROLE_MANAGER));
        roleRepository.save(new Role(RoleName.ROLE_ADMIN));
    }

    @Test
    @DisplayName("RoleName 반환 테스트 ROLE_USER (enum으로 탐색하기 때문에 실패 테스트 적용하지 않음)")
    public void test1() {
        //given
        //when
        Role role_user = roleRepository.findRoleByRoleName(RoleName.ROLE_USER);

        //then
        Assertions.assertThat(role_user.getRoleName()).isEqualTo(RoleName.ROLE_USER);
    }

    @Test
    @DisplayName("RoleName 반환 테스트 ROLE_MANAGER (enum으로 탐색하기 때문에 실패 테스트 적용하지 않음)")
    public void test2() {
        //given
        //when
        Role role_manager = roleRepository.findRoleByRoleName(RoleName.ROLE_MANAGER);

        //then
        Assertions.assertThat(role_manager.getRoleName()).isEqualTo(RoleName.ROLE_MANAGER);
    }

    @Test
    @DisplayName("RoleName 반환 테스트 ROLE_ADMIN (enum으로 탐색하기 때문에 실패 테스트 적용하지 않음)")
    public void test3() {
        //given
        //when
        Role role_admin = roleRepository.findRoleByRoleName(RoleName.ROLE_ADMIN);

        //then
        Assertions.assertThat(role_admin.getRoleName()).isEqualTo(RoleName.ROLE_ADMIN);
    }
}
