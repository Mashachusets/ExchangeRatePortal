package com.example.demo.config;

import com.example.demo.job.ExchangeRateJob;
import com.example.demo.scheduling.HolidayCalendarProvider;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.calendar.HolidayCalendar;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import java.util.Map;
import java.util.TimeZone;

@Slf4j
@Configuration
public class ExchangeRateJobConfig {

    private static final JobKey JOB_KEY = new JobKey("exchangeRateJob");
    private static final TriggerKey TRIGGER_KEY = new TriggerKey("exchangeRateTrigger");
    private static final String DAILY_CRON = "0 5 16 ? * MON-FRI";
    private static final String TIME_ZONE = String.valueOf(TimeZone.getTimeZone("Europe/Paris"));


    @Bean
    public SpringBeanJobFactory springBeanJobFactory(AutowireCapableBeanFactory beanFactory) {
        return new SpringBeanJobFactory() {
            @Override
            protected @NotNull Object createJobInstance(@NotNull TriggerFiredBundle bundle) throws Exception {
                Object job = super.createJobInstance(bundle);
                beanFactory.autowireBean(job);
                return job;
            }
        };
    }

    @Bean
    public HolidayCalendar quartzHolidayCalendar(HolidayCalendarProvider provider) {
        return provider.getCurrentYearCalendar();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
            JobDetail jobDetail,
            Trigger trigger,
            HolidayCalendar holidayCalendar,
            SpringBeanJobFactory jobFactory
    ) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory);
        factory.setJobDetails(jobDetail);
        factory.setTriggers(trigger);
        factory.setCalendars(Map.of("holidays", holidayCalendar));
        return factory;
    }

    @Bean
    public JobDetail exchangeRateJobDetail() {
        return JobBuilder.newJob(ExchangeRateJob.class)
                .withIdentity(JOB_KEY)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger exchangeRateTrigger(JobDetail exchangeRateJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(exchangeRateJobDetail)
                .withIdentity(TRIGGER_KEY)
                .withSchedule(
                        CronScheduleBuilder.cronSchedule(DAILY_CRON)
                                .inTimeZone(TimeZone.getTimeZone(TIME_ZONE)))
                //.startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.MINUTE))
                .modifiedByCalendar("holidays")
                .build();
    }
}
