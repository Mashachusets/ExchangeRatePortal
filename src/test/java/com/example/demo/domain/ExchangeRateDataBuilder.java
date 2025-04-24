package com.example.demo.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import static com.example.demo.CommonTestDataBuilder.TODAY;
import static com.example.demo.domain.SupportedCurrency.USD;

public class ExchangeRateDataBuilder {

    public static final SupportedCurrency DEFAULT_CURRENCY = USD;
    public static final BigDecimal DEFAULT_RATE = new BigDecimal("1.0");

    public static ExchangeRate exchangeRate() {
        return exchangeRate(DEFAULT_CURRENCY, TODAY, DEFAULT_RATE);
    }

    public static ExchangeRate exchangeRate(LocalDate date, BigDecimal rate) {
        return exchangeRate(DEFAULT_CURRENCY, date, rate);
    }

    public static ExchangeRate exchangeRate(SupportedCurrency currency, LocalDate date, BigDecimal rate) {
        return new ExchangeRate(currency, date, rate);
    }
}
