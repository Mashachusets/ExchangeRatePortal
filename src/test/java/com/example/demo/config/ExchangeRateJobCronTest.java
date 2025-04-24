package com.example.demo.config;

import org.junit.jupiter.api.Test;
import org.quartz.CronExpression;
import org.quartz.impl.calendar.HolidayCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import static org.assertj.core.api.Assertions.assertThat;

class ExchangeRateJobCronTest {

    private static final TimeZone PARIS = TimeZone.getTimeZone("Europe/Paris");

    @Test
    void shouldReturnNextValidExecutionTime() throws Exception {
        // given
        CronExpression cron = createParisCron();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        Date saturday = dateFormat.parse("2025-04-12 15:00");

        // when
        Date nextExecution = cron.getNextValidTimeAfter(saturday);

        // then
        assertThat(dateFormat.format(nextExecution)).isEqualTo("2025-04-14 16:05");
    }

    @Test
    void shouldSkipHolidayAndReturnNextWorkingDay_withCronExpression() throws ParseException {
        // given
        CronExpression cron = createParisCron();
        HolidayCalendar holidayCalendar = new HolidayCalendar();
        holidayCalendar.addExcludedDate(parseDate("2025-05-01"));

        Date testTime = parseDateTime("2025-05-01 15:00");

        // when
        long safeStartTime = holidayCalendar.getNextIncludedTime(testTime.getTime());
        Date nextExecution = cron.getNextValidTimeAfter(new Date(safeStartTime));

        // then
        assertThat(formatDateTime(nextExecution)).isEqualTo("2025-05-02 16:05");
    }

    private CronExpression createParisCron() throws ParseException {
        CronExpression cron = new CronExpression("0 5 16 ? * MON-FRI");
        cron.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        return cron;
    }

    private Date parseDate(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(PARIS);
        return dateFormat.parse(date);
    }

    private Date parseDateTime(String dateTime) throws ParseException {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateTimeFormat.setTimeZone(PARIS);
        return dateTimeFormat.parse(dateTime);
    }

    private String formatDateTime(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        formatter.setTimeZone(PARIS);
        return formatter.format(date);
    }
}
