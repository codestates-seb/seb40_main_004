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

import static com.morakmorak.morak_back_end.crawler.JobKoreaConstant.*;

@Slf4j
@RequiredArgsConstructor
public class JobKoreaCrawler implements Crawler {
    private final int CRAWLING_RANGE = 140;

    private final WebDriver webDriver;
    private final WebDriverWait webDriverWait;
    private final JobRepository jobRepository;

    @Override
    public void run() {
        webDriver.get(BASE_URL);

        setJobSearchFilterDeveloperOnly();
        moveLastMonthTable();
        onClickModalBtn();

        Set<String> companyNameSet = new HashSet<>();
        ArrayList<Job> jobList = new ArrayList<>();
        for (int i = 0; i < CRAWLING_RANGE; i++) {
            if (!trySelectElementOnJobTableOf(i)) continue;
            addJobInJobList(companyNameSet, jobList);
            moveNextPage();
        }

        jobRepository.saveAllJob(jobList);
    }

    private void onClickModalBtn() {
        List<WebElement> moreButtons = selectElementsAndWaitFindByCssSelector(JOB_CALENDAR_DAY_DIV_MORE_BTN, webDriver, webDriverWait);
        moreButtons.get(0).click();
    }

    private void moveLastMonthTable() {
        WebElement prevMonthBtn = selectElementAndWaitFindByCssSelector(PREV_MONTH_BTN, webDriver, webDriverWait);
        prevMonthBtn.click();
    }

    private void setJobSearchFilterDeveloperOnly() {
        WebElement filterCheckBox = selectElementAndWaitFindByCssSelector(JOB_FILTER_DEVELOPER_CHECKBOX, webDriver, webDriverWait);
        WebElement filterAcceptBtn = selectElementAndWaitFindByCssSelector(JOB_FILTER_ACCEPT_BTN, webDriver, webDriverWait);
        filterCheckBox.click();
        filterAcceptBtn.click();
    }


    private void addJobInJobList(Set<String> companyNameSet, ArrayList<Job> jobList) {
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

            if (recruitmentIsNotStartingState(state)) continue;
            if (isDuplicateCompanyName(companyNameSet, name)) continue;
            if (careerRequirement.equals("신입·경력") || careerRequirement.equals("인턴·신입·경력") ) careerRequirement = "경력무관";

            companyNameSet.add(name);
            String[] splitPeriod = period.split("~");
            Date startDay = Date.valueOf(year + splitPeriod[0]);
            Date endDay = Date.valueOf(year + splitPeriod[1]);

            if (startDay.after(endDay)) {
                endDay = changeYearToNextYear(monthlyDate, splitPeriod);
            }

            jobList.add(new Job(name, state, careerRequirement, startDay, endDay, url));
        }
    }

    private Date changeYearToNextYear(WebElement monthlyDate, String[] splitPeriod) {
        return Date.valueOf((Integer.parseInt(monthlyDate.getText().split("\\.")[0]) + 1) + "-" + splitPeriod[1]);
    }

    private boolean isDuplicateCompanyName(Set<String> companyNameSet, String name) {
        return companyNameSet.contains(name);
    }

    private boolean recruitmentIsNotStartingState(String state) {
        return state.equals("마감") || state.equals("발표") || state.equals("인적성");
    }

    private boolean trySelectElementOnJobTableOf(int i) {
        try {
            selectElementAndWaitFindByCssSelector(JOB_STATE, webDriver, webDriverWait);
        } catch (Exception exception) {
            log.info("{}번 테이블 예외 발생", i,exception);
            moveNextPage();
            return false;
        }
        return true;
    }

    private void moveNextPage() {
        WebElement nextDayBtn = selectElementAndWaitFindByCssSelector(NEXT_DAY_BTN, webDriver, webDriverWait);
        nextDayBtn.click();
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
