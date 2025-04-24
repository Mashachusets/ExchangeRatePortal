package com.example.demo.config;

import com.example.demo.business.repository.ExchangeRateRepository;
import com.example.demo.business.service.ExchangeRateJobService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeRateInitializer {

    private final ExchangeRateJobService exchangeRateJobService;

    private final ExchangeRateRepository exchangeRateRepository;

    @PostConstruct
    public void init() {
        boolean hasData = exchangeRateRepository.existsByValidFromAfter(LocalDate.now().minusDays(90));
        if (hasData) {
            log.info("90-day historical rates already present, skipping fetch.");
            return;
        }
            exchangeRateJobService.fetchAndStoreLast90DaysRates();
    }
}
