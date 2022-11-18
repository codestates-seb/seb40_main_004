package com.morakmorak.morak_back_end.util;

import com.morakmorak.morak_back_end.dto.TagQueryDto;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TagQueryDtoTestImpl implements TagQueryDto {
    private Long tagId;
    private String name;

    @Override
    public Long getTagId() {
        return tagId;
    }

    @Override
    public String getName() {
        return name;
    }
}
