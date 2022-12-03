package com.morakmorak.morak_back_end.schedule;

import com.morakmorak.morak_back_end.crawler.Crawler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrawlingScheduler {
    private final Crawler crawler;

    @Scheduled(cron = "0 0 4 * * MON-SAT")
    public void runCrawler() {
        crawler.run();
    }
}
