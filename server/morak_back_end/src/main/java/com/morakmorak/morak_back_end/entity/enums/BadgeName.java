package com.morakmorak.morak_back_end.entity.enums;

import lombok.Getter;

@Getter
public enum BadgeName {
    KINDLY("친절한"),
    WISE("현명한"),
    SMART("똑똑한"),
    POO("응가"),
    DETAILED("꼼꼼한"),
    INTELLEGENT("박식한"),
    GENEROUS("너그러운"),
    COHERENT("조리있는"),
    RELIABLE("믿음직한"),
    WITTY("재치있는"),
    PASSIONATE("열정적인"),
    HELPFUL("도움이 되는"),
    WARM("따듯한"),
    CONSIDERATE("사려 깊은"),
    PROBLEMSOLVER("문제를 해결하는"),
    CARING("배려심 있는"),
    CREATIVE("창의적인"),
    LOGICAL("논리적인"),
    UNDERSTANDABLE("이해하기 쉬운");


    private String name;

    BadgeName(String name) {
        this.name = name;
    }
}
