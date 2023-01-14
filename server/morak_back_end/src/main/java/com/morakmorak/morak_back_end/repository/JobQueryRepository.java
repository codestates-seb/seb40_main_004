package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.dto.JobInfoDto;
import com.morakmorak.morak_back_end.dto.QJobInfoDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static com.morakmorak.morak_back_end.entity.QJob.*;

@Repository
@RequiredArgsConstructor
public class JobQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public List<JobInfoDto> getJobDateOn(Date date) {
        LocalDate startDayOfMonth = date.toLocalDate().withDayOfMonth(1);
        LocalDate lastDayOfMonth = date.toLocalDate().plusMonths(1).withDayOfMonth(1).minusDays(1);

        return jpaQueryFactory.select(new QJobInfoDto(
                job.id, job.name, job.state, job.careerRequirement, job.url, job.startDate, job.endDate
        ))
                .from(job)
                .where(job.startDate.between(Date.valueOf(startDayOfMonth), Date.valueOf(lastDayOfMonth)))
                .groupBy(job.id)
                .orderBy(job.startDate.asc())
                .fetch();
    }
}
