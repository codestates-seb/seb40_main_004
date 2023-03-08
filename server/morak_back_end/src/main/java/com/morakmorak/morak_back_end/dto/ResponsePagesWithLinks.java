package com.morakmorak.morak_back_end.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponsePagesWithLinks extends RepresentationModel<ResponsePagesWithLinks> {
    private ResponseMultiplePaging responseMultiplePaging;

    public static ResponsePagesWithLinks of(ResponseMultiplePaging responseMultiplePaging) {
        return new ResponsePagesWithLinks(responseMultiplePaging);
    }
}
