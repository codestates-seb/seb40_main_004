package com.morakmorak.morak_back_end.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JobInfoDto {
    private Long jobId;
    private String name;
    private String startDate;
    private String endDate;
}
