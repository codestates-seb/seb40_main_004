package com.morakmorak.morak_back_end.util;

import com.morakmorak.morak_back_end.dto.ActivityQueryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class ActivityQueryDtoTestImpl implements ActivityQueryDto {
    private Integer articleCount;
    private Integer answerCount;
    private Integer commentCount;
    private Integer total;
    private String date;

    @Override
    public Integer getArticleCount() {
        return articleCount;
    }

    @Override
    public Integer getAnswerCount() {
        return answerCount;
    }

    @Override
    public Integer getCommentCount() {
        return commentCount;
    }

    @Override
    public Integer getTotal() {
        return total;
    }

    @Override
    public String getDate() {
        return date;
    }
}
