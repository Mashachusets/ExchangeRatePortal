package com.example.demo.scheduling;

import com.example.demo.business.exceptions.Exceptions.HolidayCalendarLoadException;
import com.example.demo.business.exceptions.Exceptions.MissingFileException;
import org.quartz.impl.calendar.HolidayCalendar;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class HolidayCalendarProvider {

    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Paris");
    private final HolidayCalendar currentYearCalendar;

    public HolidayCalendarProvider() {
        this.currentYearCalendar = loadCalendarForCurrentYear("holidays.yml");
    }

    HolidayCalendar loadCalendarForCurrentYear(String file) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(file)) {
            if (input == null) {
                throw new MissingFileException("Missing holidays.yml file in resources.");
            }
            Yaml yaml = new Yaml(new Constructor(Map.class, new LoaderOptions()));
            Map<Integer, List<String>> raw = yaml.load(input);

            int currentYear = LocalDate.now(ZONE_ID).getYear();
            List<String> dateStrings = raw.getOrDefault(currentYear, List.of());

            HolidayCalendar calendar = new HolidayCalendar();
            for (String dateStr : dateStrings) {
                LocalDate date = LocalDate.parse(dateStr);
                calendar.addExcludedDate(Date.from(date.atStartOfDay(ZONE_ID).toInstant()));
            }

            return calendar;
        } catch (Exception e) {
            throw new HolidayCalendarLoadException("Failed to load holiday calendar for current year", e);
        }
    }

    public boolean isHoliday(LocalDate date) {
        long millis = date.atStartOfDay(ZONE_ID).toInstant().toEpochMilli();
        return !currentYearCalendar.isTimeIncluded(millis);
    }

    public HolidayCalendar getCurrentYearCalendar() {
        return currentYearCalendar;
    }
}
