package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.config.JpaQueryFactoryConfig;
import com.morakmorak.morak_back_end.dto.JobInfoDto;
import com.morakmorak.morak_back_end.entity.Job;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManager;
import java.sql.Date;
import java.time.*;
import java.util.List;

import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(JpaQueryFactoryConfig.class)

public class JobQueryRepositoryTest {
    @Autowired
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    EntityManager entityManager;

    JobQueryRepository jobQueryRepository;

    @BeforeEach
    void init() {
        jobQueryRepository = new JobQueryRepository(jpaQueryFactory);
    }

    @Test
    @DisplayName("startDate가 이번달의 데이터를 오름차순으로 조회한다.")
    void findJobData1() {
        //given
        Date nextMonth = Date.valueOf(LocalDate.now().plusMonths(1));
        Date nextMonth2 = Date.valueOf(LocalDate.now().plusMonths(2));
        Date prevMonth = Date.valueOf(LocalDate.now().minusMonths(1));
        Date firstDaysOfThisMonth = Date.valueOf(LocalDate.now().withDayOfMonth(1));
        Date secondDaysOfThisMonth = Date.valueOf(LocalDate.now().withDayOfMonth(2));
        Date thirdDaysOfThisMonth = Date.valueOf(LocalDate.now().withDayOfMonth(3));

        Job job1 = Job.builder().name(NAME1 + 1)
                .state("시작")
                .endDate(Date.valueOf("9999-12-31"))
                .careerRequirement("경력")
                .url(TISTORY_URL)
                .startDate(prevMonth)
                .endDate(Date.valueOf("9999-12-31"))
                .build();

        Job job2 = Job.builder().name(NAME1 + 2)
                .state("시작")
                .endDate(Date.valueOf("9999-12-31"))
                .careerRequirement("경력")
                .url(TISTORY_URL)
                .startDate(firstDaysOfThisMonth)
                .endDate(Date.valueOf("9999-12-31"))
                .build();

        Job job3 = Job.builder().name(NAME1 + 3)
                .state("시작")
                .endDate(Date.valueOf("9999-12-31"))
                .careerRequirement("경력")
                .url(TISTORY_URL)
                .startDate(nextMonth)
                .build();

        Job job4 = Job.builder().name(NAME1 + 4)
                .state("시작")
                .endDate(Date.valueOf("9999-12-31"))
                .careerRequirement("경력")
                .url(TISTORY_URL)
                .startDate(nextMonth2)
                .build();

        Job job5 = Job.builder().name(NAME1 + 5)
                .state("시작")
                .endDate(Date.valueOf("9999-12-31"))
                .careerRequirement("경력")
                .url(TISTORY_URL)
                .startDate(secondDaysOfThisMonth)
                .build();

        Job job6 = Job.builder().name(NAME1 + 6)
                .state("시작")
                .endDate(Date.valueOf("9999-12-31"))
                .careerRequirement("경력")
                .url(TISTORY_URL)
                .startDate(thirdDaysOfThisMonth)
                .build();

        entityManager.persist(job1);
        entityManager.persist(job2);
        entityManager.persist(job3);
        entityManager.persist(job4);
        entityManager.persist(job5);
        entityManager.persist(job6);

        //when
        Date date = Date.valueOf(LocalDate.now());
        List<JobInfoDto> jobInfoDtos = jobQueryRepository.getJobDateOn(date);

        //then
        Assertions.assertThat(jobInfoDtos.get(0).getStartDate()).isEqualTo(firstDaysOfThisMonth);
        Assertions.assertThat(jobInfoDtos.get(1).getStartDate()).isEqualTo(secondDaysOfThisMonth);
        Assertions.assertThat(jobInfoDtos.get(2).getStartDate()).isEqualTo(thirdDaysOfThisMonth);
    }

    @Test
    @DisplayName("start date가 2달 전이거나 3개월 뒤라면 조회되지 않는다.")
    void findJobData2() {
        //given
        Date nextMonth = Date.valueOf(LocalDate.now().plusMonths(3));
        Date prevMonth = Date.valueOf(LocalDate.now().minusMonths(2));

        //when
        Job job1 = Job.builder().name(NAME1 + 1)
                .state("시작")
                .endDate(Date.valueOf("9999-12-31"))
                .careerRequirement("경력")
                .url(TISTORY_URL)
                .startDate(prevMonth)
                .endDate(Date.valueOf("9999-12-31"))
                .build();

        Job job2 = Job.builder().name(NAME1 + 2)
                .state("시작")
                .endDate(Date.valueOf("9999-12-31"))
                .careerRequirement("경력")
                .url(TISTORY_URL)
                .startDate(nextMonth)
                .endDate(Date.valueOf("9999-12-31"))
                .build();

        entityManager.persist(job1);
        entityManager.persist(job2);

        //when
        List<JobInfoDto> jobInfoDtos = jobQueryRepository.getJobDateOn(Date.valueOf("9999-12-31"));

        //then
        Assertions.assertThat(jobInfoDtos.isEmpty()).isTrue();
    }
}
