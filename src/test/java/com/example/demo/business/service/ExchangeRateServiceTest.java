package com.example.demo.business.service;

import com.example.demo.business.repository.ExchangeRateRepository;
import com.example.demo.domain.ExchangeRate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static com.example.demo.CommonTestDataBuilder.TODAY;
import static com.example.demo.domain.ExchangeRateDataBuilder.DEFAULT_RATE;
import static com.example.demo.domain.ExchangeRateDataBuilder.DEFAULT_CURRENCY;
import static com.example.demo.domain.ExchangeRateDataBuilder.exchangeRate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal("100");

    @Test
    void shouldConvertCurrencyAmount() {
        // given
        ExchangeRate rate = exchangeRate();
        when(exchangeRateRepository.findFirstByCurrencyCodeOrderByValidFromDesc(DEFAULT_CURRENCY))
                .thenReturn(Optional.of(rate));

        // when
        BigDecimal result = exchangeRateService.convert(DEFAULT_CURRENCY, DEFAULT_AMOUNT);

        // then
        assertThat(result).isEqualByComparingTo(new BigDecimal("100.0"));
    }

    @Test
    void shouldThrowExceptionIfAmountIsNotGreaterThanZero() {
        // given
        BigDecimal zeroAmount = BigDecimal.ZERO;

        // when / then
        assertThatThrownBy(() -> exchangeRateService.convert(DEFAULT_CURRENCY, zeroAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Input amount must be greater than 0.");
    }

    @Test
    void shouldReturnCurrencyRateHistory() {
        // given
        List<ExchangeRate> rates = List.of(
                exchangeRate(DEFAULT_CURRENCY, TODAY.minusDays(2), new BigDecimal("1.05")),
                exchangeRate(DEFAULT_CURRENCY, TODAY.minusDays(1), DEFAULT_RATE)
        );

        when(exchangeRateRepository.findAllByCurrencyCodeOrderByValidFromDesc(DEFAULT_CURRENCY))
                .thenReturn(rates);

        // when
        List<ExchangeRate> result = exchangeRateService.getHistory(DEFAULT_CURRENCY);

        // then
        assertThat(result).hasSize(2).containsExactlyElementsOf(rates);
    }

    @Test
    void shouldReturnEmptyListIfNoHistoryFound() {
        // given
        when(exchangeRateRepository.findAllByCurrencyCodeOrderByValidFromDesc(DEFAULT_CURRENCY))
                .thenReturn(List.of());

        // when
        List<ExchangeRate> result = exchangeRateService.getHistory(DEFAULT_CURRENCY);

        // then
        assertThat(result).isEmpty();
    }
}
