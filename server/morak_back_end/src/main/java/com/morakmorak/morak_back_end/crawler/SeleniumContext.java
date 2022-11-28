package com.morakmorak.morak_back_end.crawler;

import com.morakmorak.morak_back_end.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SeleniumContext {
    private final JobRepository jobRepository;

    @Bean
    public WebDriver setupWebDriver() {
        System.setProperty("webdriver.chrome.driver", "/Users/eunchanyang/Desktop/java/chromeDriver");

        ChromeOptions options = new ChromeOptions();

        options.addArguments("headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("lang=ko_KR");
        options.addArguments("User-Agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36");
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        return new ChromeDriver(options);
    }

    @Bean
    public WebDriverWait setupWebDriverWait()  {
        return new WebDriverWait(setupWebDriver(), Duration.ofSeconds(20));
    }

    @Bean
    public Crawler crawling() {
        return new JobKoreaCrawler(setupWebDriver(), setupWebDriverWait(), jobRepository);
    }
}
