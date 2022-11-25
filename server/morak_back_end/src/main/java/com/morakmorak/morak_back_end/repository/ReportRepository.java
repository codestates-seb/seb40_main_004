package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findReportByContent(String content);
}
