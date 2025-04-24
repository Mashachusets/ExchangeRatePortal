package com.example.demo.job;

import com.example.demo.business.service.ExchangeRateJobService;
import com.example.demo.scheduling.HolidayCalendarProvider;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import java.time.LocalDate;
import java.time.ZoneId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ExchangeRateJobTest {

    @Mock
    private ExchangeRateJobService exchangeRateJobService;

    @Mock
    private HolidayCalendarProvider holidayCalendarProvider;

    @Mock
    private JobExecutionContext context;

    private ExchangeRateJob exchangeRateJob;

    @BeforeEach
    void setUp() {
        exchangeRateJob = new ExchangeRateJob(exchangeRateJobService, holidayCalendarProvider);
    }

    @Test
    void shouldSkipExecutionIfTodayIsHoliday() {
        // given
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Paris"));
        when(holidayCalendarProvider.isHoliday(today)).thenReturn(true);

        // when
        exchangeRateJob.execute(context);

        // then
        verify(exchangeRateJobService, never()).fetchAndStoreLatestRates();
    }

    @Test
    void shouldExecuteServiceIfNotHoliday() {
        // given
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Paris"));
        when(holidayCalendarProvider.isHoliday(today)).thenReturn(false);

        // when
        exchangeRateJob.execute(context);

        // then
        verify(exchangeRateJobService).fetchAndStoreLatestRates();
    }

    @Test
    void shouldLogErrorIfJobFails() {
        // given
        LogCaptor logCaptor = LogCaptor.forClass(ExchangeRateJob.class);
        when(holidayCalendarProvider.isHoliday(any())).thenReturn(true);

        // when
        exchangeRateJob.execute(context);

        // then
        assertThat(logCaptor.getInfoLogs())
                .anyMatch(log -> log.contains("Skipping job"));
    }
}
