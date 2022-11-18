package com.morakmorak.morak_back_end.util;

import com.morakmorak.morak_back_end.dto.BadgeQueryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class BadgeQueryDtoTestImpl implements BadgeQueryDto {
    private Long badgeId;
    private String name;

    @Override
    public Long getBadgeId() {
        return badgeId;
    }

    @Override
    public String getName() {
        return name;
    }
}
