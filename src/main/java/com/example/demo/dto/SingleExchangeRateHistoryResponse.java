package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Data point for exchange rate history")
public record SingleExchangeRateHistoryResponse(

        @Schema(description = "Date from when the rate is valid", example = "2024-04-20")
        LocalDate date,

        @Schema(description = "Exchange rate against the base currency", example = "1.095")
        BigDecimal rate
) {}
