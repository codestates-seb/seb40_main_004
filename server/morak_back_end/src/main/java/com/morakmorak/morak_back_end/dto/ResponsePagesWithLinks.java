package com.morakmorak.morak_back_end.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponsePagesWithLinks<T> extends RepresentationModel<ResponsePagesWithLinks<T>> {
    private List<T> data = new ArrayList<>();
    private PageInfo pageInfo;

    public static ResponsePagesWithLinks of(ResponseMultiplePaging responseMultiplePaging) {
        return ResponsePagesWithLinks.builder()
                .data(responseMultiplePaging.getData())
                .pageInfo(responseMultiplePaging.getPageInfo())
                .build();
    }
}
