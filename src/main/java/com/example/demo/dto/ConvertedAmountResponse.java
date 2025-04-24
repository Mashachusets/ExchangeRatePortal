package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Response containing the result of a currency conversion.")
public record ConvertedAmountResponse(

        @Schema(description = "The converted amount in the target currency.")
        BigDecimal convertedAmount
) {}
