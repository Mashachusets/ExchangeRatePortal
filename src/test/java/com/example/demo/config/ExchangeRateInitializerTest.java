package com.example.demo.config;

import com.example.demo.business.repository.ExchangeRateRepository;
import com.example.demo.business.service.ExchangeRateJobService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExchangeRateInitializerTest {

    @Mock
    private ExchangeRateJobService exchangeRateJobService;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private ExchangeRateInitializer initializer;

    @Test
    void shouldSkipFetchWhenDataExists() {
        // given
        when(exchangeRateRepository.existsByValidFromAfter(any())).thenReturn(true);

        // when
        initializer.init();

        // then
        verify(exchangeRateJobService, never()).fetchAndStoreLast90DaysRates();
    }

    @Test
    void shouldFetchRatesWhenNoDataExists() {
        // given
        when(exchangeRateRepository.existsByValidFromAfter(any())).thenReturn(false);

        // when
        initializer.init();

        // then
        verify(exchangeRateJobService).fetchAndStoreLast90DaysRates();
    }
}
