package com.example.demo.scheduling;

import com.example.demo.business.exceptions.Exceptions.HolidayCalendarLoadException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.quartz.impl.calendar.HolidayCalendar;
import java.time.LocalDate;
import java.time.ZoneId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class HolidayCalendarProviderTest {

    private final HolidayCalendarProvider provider = new HolidayCalendarProvider();

    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Paris");

    @ParameterizedTest
    @CsvSource({
            "2025-05-01",
            "2025-05-09",
            "2025-05-29",
            "2025-06-09",
            "2025-06-19",
            "2025-10-03",
            "2025-11-01",
            "2025-12-24",
            "2025-12-25",
            "2025-12-26",
            "2025-12-31"
    })
    void shouldRecognizeHolidayDates(String dateStr) {
        // given & when
        LocalDate date = LocalDate.parse(dateStr);

        // then
        assertThat(provider.isHoliday(date)).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
            "2025-05-15",
            "2025-06-01",
            "2025-11-10"
    })
    void shouldNotRecognizeNonHolidayDates(String dateStr) {
        // given & when
        LocalDate date = LocalDate.parse(dateStr);

        //then
        assertThat(provider.isHoliday(date)).isFalse();
    }

    @Test
    void shouldLoadHolidayCalendarFromYamlFile() {
        // given
        HolidayCalendar calendar = provider.loadCalendarForCurrentYear("demo/scheduling/test-holidays.yml");

        // when
        LocalDate expected = LocalDate.of(2025, 5, 1);
        long millis = expected.atStartOfDay(ZONE_ID).toInstant().toEpochMilli();

        // then
        assertThat(calendar.isTimeIncluded(millis)).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenFileIsMissing() {
        assertThatThrownBy(() -> provider.loadCalendarForCurrentYear("demo/scheduling/nonexistent.yml"))
                .isInstanceOf(HolidayCalendarLoadException.class)
                .hasMessageContaining("Failed to load holiday calendar for current year");
    }

    @Test
    void shouldThrowExceptionWhenFileIsEmpty() {
        assertThatThrownBy(() -> provider.loadCalendarForCurrentYear("demo/scheduling/test-empty.yml"))
                .isInstanceOf(HolidayCalendarLoadException.class)
                .hasMessageContaining("Failed to load holiday calendar for current year");
    }

    @ParameterizedTest
    @CsvSource({
            "2025-05-01",
            "2025-12-25"
    })
    void shouldRecognizeMultipleConfiguredHolidays(String dateStr) {
        // given
        HolidayCalendar calendar = provider.loadCalendarForCurrentYear("demo/scheduling/test-multi-holidays.yml");

        // when
        LocalDate date = LocalDate.parse(dateStr);
        long millis = date.atStartOfDay(ZONE_ID).toInstant().toEpochMilli();

        // then
        assertThat(calendar.isTimeIncluded(millis)).isFalse();
    }
}
