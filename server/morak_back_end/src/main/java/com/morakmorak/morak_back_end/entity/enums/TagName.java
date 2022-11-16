package com.morakmorak.morak_back_end.entity.enums;

import lombok.Getter;

@Getter
public enum TagName {
    JAVA("Java"),
    C("C"),
    NODE("Node.JS"),
    SPRING("Spring");

    private String name;

    TagName(String name) {
        this.name = name;
    }
}
