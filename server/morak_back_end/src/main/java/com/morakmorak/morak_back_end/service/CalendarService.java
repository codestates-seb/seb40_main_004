package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.dto.JobInfoDto;
import com.morakmorak.morak_back_end.repository.JobQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final JobQueryRepository jobQueryRepository;

    @Transactional(readOnly = true)
    public List<JobInfoDto> findCalendarData(Date date) {
        return jobQueryRepository.getJobDateOn(date);
    }
}
