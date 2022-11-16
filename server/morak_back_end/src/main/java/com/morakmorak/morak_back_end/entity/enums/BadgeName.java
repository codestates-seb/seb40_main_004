package com.morakmorak.morak_back_end.entity.enums;

import lombok.Getter;

@Getter
public enum BadgeName {
    KINDLY("친절한"),
    WISE("박식한"),
    SMART("똑똑한"),
    POO("응가");

    private String name;

    BadgeName(String name) {
        this.name = name;
    }
}
