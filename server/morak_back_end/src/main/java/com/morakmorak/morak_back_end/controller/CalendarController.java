package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.dto.JobInfoDto;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.List;

import static com.morakmorak.morak_back_end.exception.ErrorCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendars")
public class CalendarController {
    private final CalendarService calendarService;

    @GetMapping("/{date}")
    public List<JobInfoDto> getCalendarData(@PathVariable(name = "date") String requestDate) {
        Date date = getDate(requestDate);
        return calendarService.findCalendarData(date);
    }

    private Date getDate(String requestDate) {
        Date date;
        try {
            date = Date.valueOf(requestDate);
        } catch (IllegalArgumentException e) {
            throw new BusinessLogicException(INVALID_DATE_FORMAT);
        }
        return date;
    }
}
