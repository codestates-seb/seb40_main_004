package com.morakmorak.morak_back_end.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobInfoDto {
    private Long jobId;
    private String name;
    private String state;
    private String careerRequirement;
    private String url;
    private Date startDate;
    private Date endDate;

    @QueryProjection
    public JobInfoDto(Long jobId, String name, String state, String careerRequirement, String url, Date startDate, Date endDate) {
        this.jobId = jobId;
        this.name = name;
        this.state = state;
        this.careerRequirement = careerRequirement;
        this.url = url;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
