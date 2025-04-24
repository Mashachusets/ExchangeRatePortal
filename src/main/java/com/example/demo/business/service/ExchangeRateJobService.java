package com.example.demo.business.service;

import com.example.demo.business.exceptions.Exceptions.DailyExchangeRatesNotFoundException;
import com.example.demo.business.exceptions.Exceptions.ExchangeRateJobException;
import com.example.demo.business.repository.ExchangeRateRepository;
import com.example.demo.domain.ExchangeRate;
import com.example.demo.domain.SupportedCurrency;
import com.example.demo.external.ecb.EcbXmlFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExchangeRateJobService {

    private final ExchangeRateRepository exchangeRateRepository;

    private final EcbXmlFetcher xmlFetcher;

    @Value("${ecb.daily.rates.url}")
    private String ecbDailyRatesUrl;

    @Value("${ecb.last.90.days.rates.url}")
    private String ecbLast90DaysRatesUrl;

    @Transactional
    public void fetchAndStoreLatestRates() {
        try {
            EcbXmlData result = extractLatestCurrencyData(ecbDailyRatesUrl)
                    .orElseThrow(() -> new DailyExchangeRatesNotFoundException("No daily exchange rate data found in ECB response."));

            storeRates(List.of(result));

            log.info("Successfully fetched latest exchange rates");

        } catch (Exception e) {
            log.error("Failed to fetch and store latest exchange rates", e);
            throw new ExchangeRateJobException("Failed to fetch and store latest exchange rates", e);
        }
    }

    @Transactional
    public void fetchAndStoreLast90DaysRates() {
        try {
            List<EcbXmlData> dataList = extractAllCurrencyData(ecbLast90DaysRatesUrl);

            storeRates(dataList);

            log.info("Successfully fetched and stored 90-day historical exchange rates");

        } catch (Exception e) {
            log.error("Failed to fetch historical 90 days of exchange rates", e);
            throw new ExchangeRateJobException("Failed to fetch historical 90 days of exchange rates", e);
        }
    }

    private void storeRates(List<EcbXmlData> dataList) {
        List<ExchangeRate> newRates = new ArrayList<>();

        for (EcbXmlData data : dataList) {
            NodeList nodes = data.currencyCubes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node instanceof Element currencyElement) {
                    parseAndValidateRate(currencyElement, data.rateDate())
                            .ifPresent(newRates::add);
                }
            }
        }

        if (newRates.isEmpty()) {
            log.info("No new exchange rates found in provided ECB data.");
            return;
        }

        exchangeRateRepository.saveAll(newRates);
        log.info("Stored {} new exchange rates", newRates.size());
    }

    private Optional<EcbXmlData> extractLatestCurrencyData(String url) {
        return extractAllCurrencyData(url).stream().findFirst();
    }

    private List<EcbXmlData> extractAllCurrencyData(String ecbUrl) {
        try {
            Document doc = xmlFetcher.fetchXml(ecbUrl);
            NodeList topLevelCubes = doc.getElementsByTagName("Cube");

            List<EcbXmlData> dataList = new ArrayList<>();

            for (int i = 0; i < topLevelCubes.getLength(); i++) {
                Element element = (Element) topLevelCubes.item(i);

                if (element.hasAttribute("time")) {
                    LocalDate rateDate = LocalDate.parse(element.getAttribute("time"));
                    NodeList currencyCubes = element.getChildNodes();

                    dataList.add(new EcbXmlData(rateDate, currencyCubes));
                }
            }

            return dataList;
        } catch (Exception e) {
            throw new ExchangeRateJobException("Failed to parse ECB XML", e);
        }
    }

    private Optional<ExchangeRate> parseAndValidateRate(Element element, LocalDate rateDate) {
        String currencyCode = element.getAttribute("currency").trim();
        String rateString = element.getAttribute("rate").trim();

        try {
            SupportedCurrency currency = SupportedCurrency.valueOf(currencyCode);

            BigDecimal rate = new BigDecimal(rateString);

            if (rate.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("Skipping currency {} due to non-positive rate: {}", currency, rate);
                return Optional.empty();
            }

            boolean exists = exchangeRateRepository.existsByCurrencyCodeAndValidFrom(currency, rateDate);
            if (exists) {
                log.debug("Skipping duplicate rate for {} on {}", currency, rateDate);
                return Optional.empty();
            }

            return Optional.of(new ExchangeRate(currency, rateDate, rate));

        } catch (Exception e) {
            log.warn("Skipping malformed rate entry: currency='{}', rate='{}'. Error: {}", currencyCode, rateString, e.getMessage());
            return Optional.empty();
        }
    }

    private record EcbXmlData(LocalDate rateDate, NodeList currencyCubes) {}
}
