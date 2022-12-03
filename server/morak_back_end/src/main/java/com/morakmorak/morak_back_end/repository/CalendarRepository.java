package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
}
