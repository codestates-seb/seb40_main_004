package com.morakmorak.morak_back_end.crawler;

public class JobKoreaConstant {
    final static String BASE_URL = "https://www.jobkorea.co.kr/starter/calendar";
    final static String MONTHLY_DATE = ".lyDate";
    final static String JOB_FILTER_DEVELOPER_CHECKBOX = "label[for=lb_Jobtype_10016]";
    final static String JOB_FILTER_ACCEPT_BTN = ".btnFilterSc";
    final static String JOB_CALENDAR_DAY_DIV_MORE_BTN = ".moreNum";
    final static String JOB_STATE = ".coLink.devBoothItem strong";
    final static String JOB_NAME = ".coLink.devBoothItem span";
    final static String JOB_CAREER_REQUIREMENT = ".sDesc strong";
    final static String JOB_RECRUITMENT_PERIOD = ".side .day";
    final static String JOB_HREF = ".coLink.devBoothItem";
    final static String NEXT_DAY_BTN = ".dateNext";
    final static String PREV_MONTH_BTN = ".calMonthBtn .prev";
}
