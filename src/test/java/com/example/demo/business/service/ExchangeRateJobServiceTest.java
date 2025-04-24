package com.example.demo.business.service;

import com.example.demo.business.exceptions.Exceptions.ExchangeRateJobException;
import com.example.demo.business.repository.ExchangeRateRepository;
import com.example.demo.dom.DOM;
import com.example.demo.domain.ExchangeRate;
import com.example.demo.external.ecb.EcbXmlFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Document;
import java.math.BigDecimal;
import java.util.List;
import static com.example.demo.CommonTestDataBuilder.TODAY;
import static com.example.demo.domain.ExchangeRateDataBuilder.DEFAULT_CURRENCY;
import static com.example.demo.domain.ExchangeRateDataBuilder.exchangeRate;
import static com.example.demo.domain.SupportedCurrency.JPY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ExchangeRateJobServiceTest {

    @InjectMocks
    private ExchangeRateJobService jobService;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private EcbXmlFetcher ecbXmlFetcher;

    private DOM dom;

    private static final String DUMMY_DAILY_URL = "http://localhost:8080/daily";
    private static final String DUMMY_90DAY_URL = "http://localhost:8080/90days";

    @BeforeEach
    void setUp() {
        dom = new DOM();
        ReflectionTestUtils.setField(jobService, "ecbDailyRatesUrl", DUMMY_DAILY_URL);
        ReflectionTestUtils.setField(jobService, "ecbLast90DaysRatesUrl", DUMMY_90DAY_URL);
    }

    @Test
    void shouldStoreValidExchangeRate() throws Exception {
        // given
        Document document = dom.loadDocumentFromXml("demo/service/test-last-90-days.xml");
        when(ecbXmlFetcher.fetchXml(anyString())).thenReturn(document);
        when(exchangeRateRepository.existsByCurrencyCodeAndValidFrom(any(), any()))
                .thenReturn(false);

        List<ExchangeRate> expectedRates = List.of(
                exchangeRate(DEFAULT_CURRENCY, TODAY, new BigDecimal("1.1000")),
                exchangeRate(JPY, TODAY,  new BigDecimal("130.12")),
                exchangeRate(DEFAULT_CURRENCY, TODAY.minusDays(1), new BigDecimal("1.1001")),
                exchangeRate(JPY, TODAY.minusDays(1), new BigDecimal("130.13"))
        );


        // when
        jobService.fetchAndStoreLast90DaysRates();

        //then
        verify(exchangeRateRepository).saveAll(argThat(savedRates -> {
            assertThat(savedRates).hasSize(4);
            assertThat(savedRates).usingRecursiveFieldByFieldElementComparator()
                    .containsExactlyInAnyOrderElementsOf(expectedRates);
            return true;
        }));

    }

    @Test
    void shouldSkipDuplicateExchangeRate() throws Exception {
        // given
        Document document = dom.loadDocumentFromXml("demo/service/test-last-90-days.xml");
        when(ecbXmlFetcher.fetchXml(anyString())).thenReturn(document);

        when(exchangeRateRepository.existsByCurrencyCodeAndValidFrom(any(), any())).thenReturn(true);

        // when
        jobService.fetchAndStoreLast90DaysRates();

        // then
        verify(exchangeRateRepository, never()).saveAll(any());
    }


    @Test
    void shouldSkipMalformedRate() throws Exception {
        // given
        Document document = dom.loadDocumentFromXml("demo/service/test-last-90-days-malformed.xml");
        when(ecbXmlFetcher.fetchXml(anyString())).thenReturn(document);

        // when
        jobService.fetchAndStoreLast90DaysRates();

        // then
        verify(exchangeRateRepository, never()).saveAll(any());
    }

    @Test
    void shouldSkipRateWithNonPositiveValue() throws Exception {
        // given
        Document document = dom.loadDocumentFromXml("demo/service/test-last-90-days-incorrect-rate.xml");
        when(ecbXmlFetcher.fetchXml(anyString())).thenReturn(document);

        // when
        jobService.fetchAndStoreLast90DaysRates();

        // then
        verify(exchangeRateRepository, never()).saveAll(any());
    }

    @Test
    void shouldThrowExceptionIfNoDataReturned() {
        assertThrows(ExchangeRateJobException.class, () ->
                jobService.fetchAndStoreLatestRates());
    }
}
