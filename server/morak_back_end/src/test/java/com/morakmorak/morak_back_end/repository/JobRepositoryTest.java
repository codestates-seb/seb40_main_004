package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Job;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureJdbc;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Transactional
@SpringBootTest
class JobRepositoryTest {
    @Autowired
    JobRepository jobRepository;

    @Test
    void saveAllJob1() {
        //given
        List<Job> jobs = new ArrayList<>();

        for (int i=0; i<10; i++) {
            jobs.add(
                    Job.builder()
                            .name("name"+i)
                            .careerRequirement("TEMP")
                            .url("TEMP")
                            .endDate(Date.valueOf("2022-01-01"))
                            .startDate(Date.valueOf("2022-01-01"))
                            .build()
            );
        }

        //when
        jobRepository.saveAllJob(jobs);
        List<Job> findJobs = jobRepository.findAllJob();

        //then
        Assertions.assertThat(findJobs.size()).isEqualTo(10);
    }

    @Test
    void saveAllJob2() {
        //given
        List<Job> jobs = new ArrayList<>();

        for (int i=0; i<2000; i++) {
            jobs.add(
            Job.builder()
                    .name("name"+i)
                    .careerRequirement("TEMP")
                    .url("TEMP")
                    .endDate(Date.valueOf("2022-01-01"))
                    .startDate(Date.valueOf("2022-01-01"))
                    .build()
            );
        }

        //when
        long start = System.currentTimeMillis();
        log.info("insert_start : " + start);
        jobRepository.saveAllJob(jobs);
        long end = System.currentTimeMillis();
        log.info("insert_end : " + end);
        log.info("result : " + (end - start));

        List<Job> findJobs = jobRepository.findAllJob();

        //then
        Assertions.assertThat(findJobs.size()).isEqualTo(2000);
    }

    @Test
    @DisplayName("고유값 (name)이 중복되어도 예외가 발생하지 않는다.")
    void unique_key_duplicate_save_test() {
        //given
        List<Job> jobs = new ArrayList<>();

        for (int i=0; i<10; i++) {
            jobs.add(
                    Job.builder()
                            .name("name")
                            .careerRequirement("TEMP")
                            .url("TEMP")
                            .endDate(Date.valueOf("2022-01-01"))
                            .startDate(Date.valueOf("2022-01-01"))
                            .build()
            );
        }

        //when
        jobRepository.saveAllJob(jobs);
        List<Job> allJob = jobRepository.findAllJob();

        //then
        Assertions.assertThat(allJob.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("고유값이 중복되는 경우 마지막에 저장된 데이터가 적용된다")
    void unique_key_duplicate_save_test2() {
        //given
        List<Job> jobs = new ArrayList<>();

        for (int i=0; i<10; i++) {
            jobs.add(
                    Job.builder()
                            .name("name")
                            .careerRequirement("TEMP"+i)
                            .url("TEMP")
                            .endDate(Date.valueOf("2022-01-01"))
                            .startDate(Date.valueOf("2022-01-01"))
                            .build()
            );
        }

        //when
        jobRepository.saveAllJob(jobs);
        List<Job> allJob = jobRepository.findAllJob();

        //then
        Assertions.assertThat(allJob.get(0).getCareerRequirement()).isEqualTo("TEMP9");
    }
}