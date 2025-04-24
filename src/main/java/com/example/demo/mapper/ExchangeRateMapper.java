package com.example.demo.mapper;

import com.example.demo.dto.ConvertedAmountResponse;
import com.example.demo.dto.SingleExchangeRateHistoryResponse;
import com.example.demo.dto.ExchangeRateHistoryResponse;
import com.example.demo.domain.ExchangeRate;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;

@Component
public class ExchangeRateMapper {

    public ConvertedAmountResponse toConvertedAmountResponse(BigDecimal result) {
        return new ConvertedAmountResponse(result);
    }

    public ExchangeRateHistoryResponse toHistoryResponse(List<ExchangeRate> exchangeRates) {
        List<SingleExchangeRateHistoryResponse> data = exchangeRates.stream()
                .map(rate -> new SingleExchangeRateHistoryResponse(rate.getValidFrom(), rate.getRate()))
                .toList();
        return new ExchangeRateHistoryResponse(data);
    }
}
