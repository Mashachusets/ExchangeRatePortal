package com.example.demo.business.service;

import com.example.demo.business.exceptions.Exceptions.ExchangeRateNotFoundException;
import com.example.demo.business.repository.ExchangeRateRepository;
import com.example.demo.domain.ExchangeRate;
import com.example.demo.domain.SupportedCurrency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    @Transactional(readOnly = true)
    public BigDecimal convert(SupportedCurrency currencyCode, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Input amount must be greater than 0.");
        }

        ExchangeRate toRate = exchangeRateRepository
                .findFirstByCurrencyCodeOrderByValidFromDesc(currencyCode)
                .orElseThrow(() -> new ExchangeRateNotFoundException(currencyCode.toString()));

        return amount.multiply(toRate.getRate());
    }

    @Transactional(readOnly = true)
    public List<ExchangeRate> getHistory(SupportedCurrency currencyCode) {
        return exchangeRateRepository.findAllByCurrencyCodeOrderByValidFromDesc(currencyCode);
    }
}
