package com.example.demo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "exchange_rates")
@Getter
@Setter
@NoArgsConstructor
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupportedCurrency  currencyCode;

    @Column(nullable = false)
    private LocalDate validFrom;

    @Column(precision = 16, scale = 6, nullable = false)
    private BigDecimal rate;

    public ExchangeRate(SupportedCurrency  currencyCode, LocalDate validFrom, BigDecimal rate) {
        this.currencyCode = currencyCode;
        this.validFrom = validFrom;
        this.rate = rate;
    }
}
