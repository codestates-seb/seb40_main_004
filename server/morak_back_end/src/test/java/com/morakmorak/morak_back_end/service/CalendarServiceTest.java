package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.dto.JobInfoDto;
import com.morakmorak.morak_back_end.repository.JobQueryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CalendarServiceTest {
    @InjectMocks
    CalendarService calendarService;

    @Mock
    JobQueryRepository jobQueryRepository;

    @Test
    void findCalendarData() {
        //given
        JobInfoDto dto = JobInfoDto.builder().url("url").build();

        BDDMockito.given(jobQueryRepository.getJobDateOn(Date.valueOf("9999-12-31"))).willReturn(List.of(dto));

        //when
        List<JobInfoDto> response = calendarService.findCalendarData(Date.valueOf("9999-12-31"));

        //then
        Assertions.assertThat(response.size()).isEqualTo(1);
        Assertions.assertThat(response.get(0).getUrl()).isEqualTo("url");
    }
}
