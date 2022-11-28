package com.morakmorak.morak_back_end.crawler;

import com.morakmorak.morak_back_end.entity.Job;
import com.morakmorak.morak_back_end.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class JobKoreaCrawler implements Crawler {
    private final String BASE_URL = "https://www.jobkorea.co.kr/starter/calendar";
    private final String MONTHLY_DATE = ".lyDate";
    private final String JOB_FILTER_DEVELOPER_CHECKBOX = "label[for=lb_Jobtype_10016]";
    private final String JOB_FILTER_ACCEPT_BTN = ".btnFilterSc";
    private final String JOB_CALENDAR_DAY_DIV_MORE_BTN = ".moreNum";
    private final String JOB_STATE = ".coLink.devBoothItem strong";
    private final String JOB_NAME = ".coLink.devBoothItem span";
    private final String JOB_CAREER_REQUIREMENT = ".sDesc strong";
    private final String JOB_RECRUITMENT_PERIOD = ".side .day";
    private final String JOB_HREF = ".coLink.devBoothItem";
    private final String NEXT_DAY_BTN = ".dateNext";
    private final String PREV_MONTH_BTN = ".calMonthBtn .prev";

    private final int RANGE = 140;

    private final WebDriver webDriver;
    private final WebDriverWait webDriverWait;
    private final JobRepository jobRepository;

    @Override
    public void process() {
        Set<String> companyNameSet = new HashSet<>();
        ArrayList<Job> jobs = new ArrayList<>();

        webDriver.get(BASE_URL);
        WebElement filterCheckBox = selectElementAndWaitFindByCssSelector(JOB_FILTER_DEVELOPER_CHECKBOX, webDriver, webDriverWait);
        WebElement filterAcceptBtn = selectElementAndWaitFindByCssSelector(JOB_FILTER_ACCEPT_BTN, webDriver, webDriverWait);
        setDeveloperFilter(filterCheckBox, filterAcceptBtn);

        WebElement prevMonthBtn = selectElementAndWaitFindByCssSelector(PREV_MONTH_BTN, webDriver, webDriverWait);
        prevMonthBtn.click();
        List<WebElement> moreButtons = selectElementsAndWaitFindByCssSelector(JOB_CALENDAR_DAY_DIV_MORE_BTN, webDriver, webDriverWait);
        moreButtons.get(0).click();

        for (int i = 0; i < RANGE; i++) {
            log.info("{}번 테이블 크롤링 시작", i+1);

            try {
                selectElementAndWaitFindByCssSelector(JOB_STATE, webDriver, webDriverWait);
            } catch (Exception exception) {
                log.info("{}번 테이블 예외 발생",i,exception);
                moveNextPage();
                continue;
            }
            WebElement monthlyDate = selectElementAndWaitFindByCssSelector(MONTHLY_DATE, webDriver, webDriverWait);
            List<WebElement> states = selectElementsAndWaitFindByCssSelector(JOB_STATE, webDriver, webDriverWait);
            List<WebElement> urls = selectElementsAndWaitFindByCssSelector(JOB_HREF, webDriver, webDriverWait);
            List<WebElement> names = selectElementsAndWaitFindByCssSelector(JOB_NAME, webDriver, webDriverWait);
            List<WebElement> careerRequirements = selectElementsAndWaitFindByCssSelector(JOB_CAREER_REQUIREMENT, webDriver, webDriverWait);
            List<WebElement> periods = selectElementsAndWaitFindByCssSelector(JOB_RECRUITMENT_PERIOD, webDriver, webDriverWait);


            for (int j = 0; j < states.size(); j++) {
                String year = monthlyDate.getText().split("\\.")[0] + "-";
                String state = states.get(j).getText();
                String url = urls.get(j).getAttribute("href");
                String name = names.get(j).getText().split("\n")[1];
                String careerRequirement = careerRequirements.get(j).getText();
                String period = periods.get(j).getText().replace("/", "-");

                if (state.equals("마감") || state.equals("발표") || state.equals("인적성")) continue;
                if (companyNameSet.contains(name)) continue;
                if (careerRequirement.equals("신입·경력") || careerRequirement.equals("인턴·신입·경력") ) careerRequirement = "경력무관";

                companyNameSet.add(name);
                String[] splitPeriod = period.split("~");
                Date startDay = Date.valueOf(year + splitPeriod[0]);
                Date endDay = Date.valueOf(year + splitPeriod[1]);

                if (startDay.after(endDay)) {
                    endDay = Date.valueOf((Integer.parseInt(monthlyDate.getText().split("\\.")[0]) + 1) + "-" + splitPeriod[1]);
                }

                jobs.add(new Job(name, state, careerRequirement, startDay, endDay, url));
            }

            moveNextPage();
        }

        log.info("크롤링 종료 및 벌크 삽입 시작");
        jobRepository.saveAllJob(jobs);
    }

    private void moveNextPage() {
        WebElement nextDayBtn = selectElementAndWaitFindByCssSelector(NEXT_DAY_BTN, webDriver, webDriverWait);
        nextDayBtn.click();
    }

    private void setDeveloperFilter(WebElement filterCheckBox, WebElement filterAcceptBtn) {
        filterCheckBox.click();
        filterAcceptBtn.click();
    }

    @Override
    public boolean switchTapTo(int tab, WebDriver webDriver) {
        return Crawler.super.switchTapTo(tab, webDriver);
    }

    @Override
    public void clickBtnOnce(String location, WebDriver webDriver, WebDriverWait webDriverWait) {
        Crawler.super.clickBtnOnce(location, webDriver, webDriverWait);
    }

    @Override
    public List<WebElement> selectElementsAndWaitFindByCssSelector(String location, WebDriver webDriver, WebDriverWait webDriverWait) {
        return Crawler.super.selectElementsAndWaitFindByCssSelector(location, webDriver, webDriverWait);
    }

    @Override
    public WebElement selectElementAndWaitFindByCssSelector(String location, WebDriver webDriver, WebDriverWait webDriverWait) {
        return Crawler.super.selectElementAndWaitFindByCssSelector(location, webDriver, webDriverWait);
    }

    @Override
    public void waitFindByCssSelector(String location, WebDriverWait webDriverWait) {
        Crawler.super.waitFindByCssSelector(location, webDriverWait);
    }

    @Override
    public List<WebElement> selectElementsByCssSelector(String location, WebDriver webDriver) {
        return Crawler.super.selectElementsByCssSelector(location, webDriver);
    }

    @Override
    public WebElement selectElementByCssSelector(String location, WebDriver webDriver) {
        return Crawler.super.selectElementByCssSelector(location, webDriver);
    }
}
