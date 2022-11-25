package com.morakmorak.morak_back_end.entity.enums;

import lombok.Getter;

@Getter
public enum TagName {
    JAVA("Java"),
    C("C"),
    NODE("Node.JS"),
    SPRING("Spring"),
    REACT("react.Js"),
    JAVASCRIPT("javascript"),
    CPLUSCPLUS("C++"),
    CSHOP("C#"),
    NEXT("next.js"),
    NEST("nest.js"),
    PYTHON("python"),
    SWIFT("swift"),
    KOTLIN("kotiln"),
    CSS("css"),
    HTML("html"),
    AWS("aws"),
    REDUX("redux"),
    SCALA("scala"),
    GO("go"),
    TYPESCRIPT("typescript");

    private String name;

    TagName(String name) {
        this.name = name;
    }
}
