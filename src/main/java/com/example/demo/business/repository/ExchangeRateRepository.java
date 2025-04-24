package com.example.demo.business.repository;

import com.example.demo.domain.ExchangeRate;
import com.example.demo.domain.SupportedCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findFirstByCurrencyCodeOrderByValidFromDesc(SupportedCurrency currencyCode);

    List<ExchangeRate> findAllByCurrencyCodeOrderByValidFromDesc(SupportedCurrency  currencyCode);

    Boolean existsByCurrencyCodeAndValidFrom(SupportedCurrency  currencyCode, LocalDate date);

    Boolean existsByValidFromAfter(LocalDate date);
}
