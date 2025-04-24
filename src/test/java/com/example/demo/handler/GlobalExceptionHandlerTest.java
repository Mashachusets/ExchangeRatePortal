package com.example.demo.handler;

import com.example.demo.business.handler.GlobalExceptionHandler;
import com.example.demo.business.service.ExchangeRateService;
import com.example.demo.controller.ExchangeRateController;
import com.example.demo.mapper.ExchangeRateMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import static com.example.demo.domain.ExchangeRateDataBuilder.DEFAULT_CURRENCY;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ExchangeRateController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExchangeRateService exchangeRateService;

    @MockitoBean
    private ExchangeRateMapper exchangeRateMapper;

    @Test
    void shouldReturnBadRequestForIllegalArgumentException() throws Exception {
        when(exchangeRateService.convert(eq(DEFAULT_CURRENCY), eq(BigDecimal.ZERO)))
                .thenThrow(new IllegalArgumentException("Input amount must be greater than 0."));

        mockMvc.perform(get("/api/convert?currencyCode=USD&amount=0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Input amount must be greater than 0."));
    }

    @Test
    void shouldReturnBadRequestForMissingParam() throws Exception {
        mockMvc.perform(get("/api/convert?amount=10.0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Missing required parameter: currencyCode"));
    }

    @Test
    void shouldReturnBadRequestForTypeMismatch() throws Exception {
        mockMvc.perform(get("/api/convert?currencyCode=USD&amount=a"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid value for parameter: amount"));
    }

    @Test
    void shouldReturnBadRequestForEnumMismatch() throws Exception {
        mockMvc.perform(get("/api/convert?currencyCode=USB&amount=1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unsupported currency: USB"));
    }
}
