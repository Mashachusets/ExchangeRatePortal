package com.example.demo.mapper;

import com.example.demo.domain.ExchangeRate;
import com.example.demo.dto.ConvertedAmountResponse;
import com.example.demo.dto.ExchangeRateHistoryResponse;
import com.example.demo.dto.SingleExchangeRateHistoryResponse;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static com.example.demo.CommonTestDataBuilder.TODAY;
import static com.example.demo.domain.ExchangeRateDataBuilder.exchangeRate;
import static com.example.demo.domain.ExchangeRateDataBuilder.DEFAULT_RATE;
import static org.assertj.core.api.Assertions.assertThat;

class ExchangeRateMapperTest {

    private final ExchangeRateMapper exchangeRateMapper = new ExchangeRateMapper();

    @Test
    void shouldMapAmountToConvertedAmountResponse() {
        // given
        BigDecimal amount = DEFAULT_RATE;

        // when
        ConvertedAmountResponse result = exchangeRateMapper.toConvertedAmountResponse(amount);

        // then
        assertThat(result.convertedAmount())
                .isEqualByComparingTo("1.0");
    }

    @Test
    void shouldMapToExchangeRateHistoryResponse() {
        // given
        BigDecimal rateAmount = new BigDecimal("1.1");
        ExchangeRate exchangeRate1 = exchangeRate();
        ExchangeRate exchangeRate2 = exchangeRate(TODAY.plusDays(1), rateAmount);
        List<ExchangeRate> exchangeRates = List.of(exchangeRate1, exchangeRate2);

        ExchangeRateHistoryResponse expected = new ExchangeRateHistoryResponse(List.of(
                new SingleExchangeRateHistoryResponse(TODAY, DEFAULT_RATE),
                new SingleExchangeRateHistoryResponse(TODAY.plusDays(1), rateAmount)
        ));

        // when
        ExchangeRateHistoryResponse result = exchangeRateMapper.toHistoryResponse(exchangeRates);

        // then
        assertThat(result).isEqualTo(expected);
    }
}
