package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Job;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class JobRepository {
    private final JdbcTemplate jdbcTemplate;

    public void saveAllJob(List<Job> jobs) {
        String sql =
                " REPLACE INTO job (" +
                        "name, state, career_requirement, start_date, end_date, url, created_at, last_modified_at" +
                        ") values (" +
                        "?, ?, ?, ?, ?, ?, ?, ?" +
                        ")";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Job job = jobs.get(i);
                        ps.setString(1, job.getName());
                        ps.setString(2, job.getState());
                        ps.setString(3, job.getCareerRequirement());
                        ps.setDate(4, job.getStartDate());
                        ps.setDate(5, job.getEndDate());
                        ps.setString(6, job.getUrl());
                        ps.setDate(7, new Date(Instant.now().toEpochMilli()));
                        ps.setDate(8, new Date(Instant.now().toEpochMilli()));
                    }

                    @Override
                    public int getBatchSize() {
                        return jobs.size();
                    }
                }
        );
    }

    public List<Job> findAllJob() {
        String sql =
                "SELECT * FROM job";

        return jdbcTemplate.query(
                sql,
                new RowMapper<Job>() {
                    @Override
                    public Job mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return Job.builder().url(rs.getString("url"))
                                .startDate(rs.getDate("start_date"))
                                .endDate(rs.getDate("end_date"))
                                .name(rs.getString("name"))
                                .careerRequirement(rs.getString("career_requirement"))
                                .state(rs.getString("state"))
                                .id(rs.getLong("job_id"))
                                .build();
                    }
                }
        );
    }
}
