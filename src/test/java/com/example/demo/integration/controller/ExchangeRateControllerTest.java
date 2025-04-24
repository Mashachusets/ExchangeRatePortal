package com.example.demo.integration.controller;

import com.example.demo.business.repository.ExchangeRateRepository;
import com.example.demo.domain.ExchangeRate;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import static com.example.demo.CommonTestDataBuilder.TODAY;
import static com.example.demo.domain.ExchangeRateDataBuilder.DEFAULT_CURRENCY;
import static com.example.demo.domain.ExchangeRateDataBuilder.exchangeRate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class ExchangeRateControllerTest extends ControllerIntegrationTestBase {

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    private static final String ROOT_URL = "/api";

    @BeforeEach
    void cleanUp() {
        exchangeRateRepository.deleteAll();
    }

    @Test
    void shouldConvertAmountToForeignCurrency() throws Exception {
        // given
        String currency = DEFAULT_CURRENCY.toString();
        String inputAmount = "100";
        BigDecimal rateAmount = new BigDecimal("1.1");
        ExchangeRate rate1 = exchangeRate();
        ExchangeRate rate2 = exchangeRate(TODAY.minusDays(1), rateAmount);

        exchangeRateRepository.saveAll(List.of(rate1, rate2));

        // when & then
        get(ROOT_URL + "/convert", Map.of(
                "currencyCode", currency,
                "amount", inputAmount
        ))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedConversionResponse));
    }

    @Test
    void shouldReturnBadRequestWhenAmountIsNotGreaterThanZero() throws Exception {
        // given
        String inputAmount = "0";
        String currency = DEFAULT_CURRENCY.toString();

        // when & then
        get(ROOT_URL + "/convert", Map.of(
                "currencyCode", currency,
                "amount", inputAmount
        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Input amount must be greater than 0."));
    }

    @Test
    void shouldReturnExchangeRateHistory() throws Exception {
        // given
        ExchangeRate rate1 = exchangeRate();
        ExchangeRate rate2 = exchangeRate(TODAY.minusDays(2), new BigDecimal("1.10"));
        ExchangeRate rate3 = exchangeRate(TODAY.minusDays(1), new BigDecimal("1.11"));

        exchangeRateRepository.saveAll(List.of(rate1, rate2, rate3));

        // when & then
        get(ROOT_URL + "/history", Map.of("currencyCode", "USD"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedHistoryResponse));
    }

    @Language("JSON")
    private static final String expectedConversionResponse = """
    {
      "convertedAmount": 100.0
    }
    """;

    @Language("JSON")
    private static final String expectedHistoryResponse = """
    {
      "exchangeRateData": [
        {
          "date": "2025-04-20",
          "rate": 1.00
        },
        {
          "date": "2025-04-19",
          "rate": 1.11
        },
        {
          "date": "2025-04-18",
          "rate": 1.10
        }
      ]
    }
    """;
}
