package com.example.demo.controller;

import com.example.demo.business.service.ExchangeRateService;
import com.example.demo.domain.ExchangeRate;
import com.example.demo.domain.SupportedCurrency;
import com.example.demo.dto.ConvertedAmountResponse;
import com.example.demo.dto.ExchangeRateHistoryResponse;
import com.example.demo.mapper.ExchangeRateMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.util.List;

import static com.example.demo.swagger.SwaggerResponseExamples.*;

@Tag(name = "Exchange Rate Controller")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    private final ExchangeRateMapper exchangeRateMapper;

    @GetMapping("/convert")
    @Operation(
            summary = "Convert currency amount",
            description = "Converts a given amount from one currency to another using the latest available exchange rate."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Conversion successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConvertedAmountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "Missing or invalid parameters",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = CONVERT_400))
            ),
            @ApiResponse(
                    responseCode = "404", description = "Exchange rate not found for provided currency code",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = CONVERT_404))
            ),
            @ApiResponse(
                    responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = CONVERT_500))
                    )
    })
    public ResponseEntity<ConvertedAmountResponse> convert(
            @Parameter(description = "Currency to convert to") @RequestParam SupportedCurrency currencyCode,
            @Parameter(description = "Amount to convert") @RequestParam BigDecimal amount
    ) {
        BigDecimal convertedAmount = exchangeRateService.convert(currencyCode, amount);
        return ResponseEntity.ok(exchangeRateMapper.toConvertedAmountResponse(convertedAmount));
    }

    @GetMapping("/history")
    @Operation(
            summary = "Get exchange rate history",
            description = "Returns the exchange rate history for a given currency."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "History successfully retrieved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExchangeRateHistoryResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "Missing or invalid currency code parameter",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = HISTORY_400))
            ),
            @ApiResponse(
                    responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = HISTORY_500))
            )
    })
    public ResponseEntity<ExchangeRateHistoryResponse> getHistory(
            @Parameter(description = "Currency code") @RequestParam SupportedCurrency currencyCode
    ) {
        List<ExchangeRate> exchangeRates = exchangeRateService.getHistory(currencyCode);
        return ResponseEntity.ok(exchangeRateMapper.toHistoryResponse(exchangeRates));
    }
}
