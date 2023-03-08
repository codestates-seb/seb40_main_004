package com.morakmorak.morak_back_end.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
@RequiredArgsConstructor
public class ResponsePagesWithLinks extends RepresentationModel<ResponsePagesWithLinks> {
    private final ResponseMultiplePaging responseMultiplePaging;

}
