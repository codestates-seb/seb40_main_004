package com.morakmorak.morak_back_end.dto;

public interface ActivityQueryDto {
    Integer getArticleCount();
    Integer getAnswerCount();
    Integer getCommentCount();
    Integer getTotal();
    String getDate();
}
