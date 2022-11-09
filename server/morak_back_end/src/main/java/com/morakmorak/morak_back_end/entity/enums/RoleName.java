package com.morakmorak.morak_back_end.entity.enums;

public enum RoleName {
    ROLE_USER("ROLE_USER"),
    ROLE_MANAGER("ROLE_MANAGER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private final String name;

    RoleName(String name) {
        this.name = name;
    }
}
