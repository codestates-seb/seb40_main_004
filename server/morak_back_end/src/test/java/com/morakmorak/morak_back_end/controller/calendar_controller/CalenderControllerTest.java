package com.morakmorak.morak_back_end.controller.calendar_controller;

import com.morakmorak.morak_back_end.config.ApiDocumentUtils;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.controller.BookmarkController;
import com.morakmorak.morak_back_end.controller.CalendarController;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.dto.JobInfoDto;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.CalendarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.sql.Date;
import java.util.List;

import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.*;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ACCESS_TOKEN;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.REFRESH_TOKEN;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@Import(SecurityTestConfig.class)
@WebMvcTest({ExceptionController.class, CalendarController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class CalenderControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    CalendarService calendarService;

    @MockBean
    JwtTokenUtil jwtTokenUtil;

    @BeforeEach
    public void init() {
        jwtTokenUtil = new JwtTokenUtil(ACCESS_TOKEN, REFRESH_TOKEN);
    }

    @Test
    void getCalenderTest() throws Exception {
        //given
        JobInfoDto dto = JobInfoDto.builder()
                .jobId(1L)
                .name("MorakMorak.LAB")
                .careerRequirement("경력")
                .endDate(Date.valueOf("2022-02-02"))
                .startDate(Date.valueOf("2022-01-25"))
                .state("시작")
                .url("https://7357.tistory.com/")
                .build();

        List<JobInfoDto> response = List.of(dto, dto);
        BDDMockito.given(calendarService.findCalendarData()).willReturn(response);
        //when
        ResultActions perform = mockMvc.perform(get("/calendars"));

        //then
        perform.andExpect(status().isOk())
                .andDo(document("채용정보_조회성공_200"
                , getDocumentRequest()
                , getDocumentResponse()
                , responseFields(
                        fieldWithPath("[].jobId").type(NUMBER).description("DB 시퀀스 키"),
                        fieldWithPath("[].name").type(STRING).description("회사명"),
                        fieldWithPath("[].careerRequirement").type(STRING).description("경력 요구사항,"),
                        fieldWithPath("[].state").type(STRING).description("채용 공고 상태"),
                        fieldWithPath("[].url").type(STRING).description("채용 홈페이지 경로"),
                        fieldWithPath("[].startDate").type(STRING).description("체용 신청 시작일"),
                        fieldWithPath("[].endDate").type(STRING).description("채용 접수 마감일")
                        ))
                );
    }
}
