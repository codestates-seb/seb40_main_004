package com.morakmorak.morak_back_end.util;

import com.morakmorak.morak_back_end.dto.BadgeQueryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class BadgeQueryDtoTestImpl implements BadgeQueryDto {
    private Long ranking;
    private Long badgeId;
    private String name;

    @Override
    public Long getBadge_Id() {
        return badgeId;
    }

    @Override
    public String getName() {
        return name;
    }
}
