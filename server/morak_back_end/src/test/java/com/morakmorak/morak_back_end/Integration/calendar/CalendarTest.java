package com.morakmorak.morak_back_end.Integration.calendar;

import com.morakmorak.morak_back_end.dto.JobInfoDto;
import com.morakmorak.morak_back_end.entity.Job;
import com.morakmorak.morak_back_end.repository.JobQueryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static com.morakmorak.morak_back_end.util.TestConstants.NAME1;
import static com.morakmorak.morak_back_end.util.TestConstants.TISTORY_URL;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
public class CalendarTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    EntityManager entityManager;

    @Autowired
    JobQueryRepository jobQueryRepository;

    @Test
    @DisplayName("startDate가 지난 달, 이번 달, 다음 달, 다다음달의 데이터를 오름차순으로 조회한다.")
    void findJobData1() throws Exception {
        //given
        Date now = Date.valueOf(LocalDate.now());
        Date nextMonth = Date.valueOf(LocalDate.now().plusMonths(1));
        Date nextMonth2 = Date.valueOf(LocalDate.now().plusMonths(2));
        Date prevMonth = Date.valueOf(LocalDate.now().minusMonths(1));

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
                .startDate(now)
                .endDate(Date.valueOf("9999-12-31"))
                .build();

        Job job4 = Job.builder().name(NAME1 + 3)
                .state("시작")
                .endDate(Date.valueOf("9999-12-31"))
                .careerRequirement("경력")
                .url(TISTORY_URL)
                .startDate(nextMonth)
                .build();

        Job job3 = Job.builder().name(NAME1 + 4)
                .state("시작")
                .endDate(Date.valueOf("9999-12-31"))
                .careerRequirement("경력")
                .url(TISTORY_URL)
                .startDate(nextMonth2)
                .build();

        entityManager.persist(job1);
        entityManager.persist(job2);
        entityManager.persist(job3);
        entityManager.persist(job4);

        //when
        ResultActions perform = mockMvc.perform(get("/calendars"));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(NAME1+1)))
                .andExpect(jsonPath("$[1].name", is(NAME1+2)))
                .andExpect(jsonPath("$[2].name", is(NAME1+3)))
                .andExpect(jsonPath("$[3].name", is(NAME1+4)));
    }

    @Test
    @DisplayName("start date가 2달 전이거나 3개월 뒤라면 조회되지 않는다.")
    void findJobData2() throws Exception {
        //given
        Date nextMonth = Date.valueOf(LocalDate.now().plusMonths(3));
        Date prevMonth = Date.valueOf(LocalDate.now().minusMonths(2));

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
        ResultActions perform = mockMvc.perform(get("/calendars"));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").doesNotExist());
    }
}
