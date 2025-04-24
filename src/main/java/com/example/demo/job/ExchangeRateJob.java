package com.example.demo.job;

import com.example.demo.business.service.ExchangeRateJobService;
import com.example.demo.scheduling.HolidayCalendarProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.ZoneId;

@Slf4j
@DisallowConcurrentExecution
@Component
@RequiredArgsConstructor
public class ExchangeRateJob implements Job {

    private final ExchangeRateJobService exchangeRateJobService;

    private final HolidayCalendarProvider holidayCalendarProvider;

    @Override
    public void execute(JobExecutionContext context) {
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Paris"));

        if (holidayCalendarProvider.isHoliday(today)) {
            log.info("Skipping job: {} is a holiday.", today);
            return;
        }

        try {
            log.info("Starting exchange rate job");
            exchangeRateJobService.fetchAndStoreLatestRates();
            log.info("ExchangeRateJob executed successfully.");
        } catch (Exception e) {
            log.error("ExchangeRateJob failed", e);
        }
    }
}
