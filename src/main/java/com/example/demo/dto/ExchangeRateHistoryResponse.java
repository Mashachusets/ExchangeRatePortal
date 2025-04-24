package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Response containing a list of exchange rate history data for a specific currency.")
public record ExchangeRateHistoryResponse(

        @Schema(description = "List of historical exchange rate entries.")
        List<SingleExchangeRateHistoryResponse> exchangeRateData
) {}
