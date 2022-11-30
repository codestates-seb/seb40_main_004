package com.morakmorak.morak_back_end.entity.enums;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum CategoryName {
    INFO("정보"),
    QNA("질문글"),
    ;

    private String name;

    CategoryName(String name) {
        this.name = name;
    }
    public static Optional<CategoryName> check(String name) {
        try { return Optional.of(CategoryName.valueOf(name)); }
        catch (Exception e) {/* do nothing */}
        return Optional.empty();
    }
}
