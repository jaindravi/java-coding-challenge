package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.constants.FXRateConstants;
import com.crewmeister.cmcodingchallenge.entity.CurrencyEntity;
import com.crewmeister.cmcodingchallenge.entity.ExchangeRateEntity;
import com.crewmeister.cmcodingchallenge.exception.ResourceNotFoundException;
import com.crewmeister.cmcodingchallenge.externalapi.data.GenericData;
import com.crewmeister.cmcodingchallenge.externalapi.data.GenericValue;
import com.crewmeister.cmcodingchallenge.externalapi.data.Obs;
import com.crewmeister.cmcodingchallenge.model.ExchangeRateDto;
import com.crewmeister.cmcodingchallenge.model.ExchangeRatesListDto;
import com.crewmeister.cmcodingchallenge.repository.CurrencyRepository;
import com.crewmeister.cmcodingchallenge.repository.ExchangeRateRepository;
import com.crewmeister.cmcodingchallenge.util.RestClientUtil;
import com.crewmeister.cmcodingchallenge.util.XMLMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.crewmeister.cmcodingchallenge.constants.FXRateConstants.BATCH_SIZE;

@Slf4j
@Component
public class ExchangeRateBootstrapLoader {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateBootstrapLoader.class);

    @Autowired
    private RestClientUtil restClientUtil;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    private List<String> getCurrencyCodes() {
        return currencyRepository.findAll().stream()
                                 .map(CurrencyEntity::getCode)
                                 .collect(Collectors.toList());
    }

    @Retryable(value = SocketTimeoutException.class, maxAttemptsExpression = "${bundesbank.api.retry.attempts}",
            backoff = @Backoff(delayExpression = "${bundesbank.api.retry.delay}"))
    public void loadExchangeRatesFromApiToDB(@Nullable String date) {
        List<String> currencyCodes = getCurrencyCodes();

        logger.debug("Loading country-wise FX Rate data to DB");
        for (String currencyCode : currencyCodes) {
            try {
                ExchangeRatesListDto exchangeRatesList = getExchangeRatesFromApi(currencyCode, date);
                saveExchangeRates(currencyCode, exchangeRatesList)
                        .exceptionally(ex -> {
                    log.error("Async save failed for {}: {}", currencyCode, ex.getMessage());
                    return null;
                });
                logger.debug("Loaded exchange rates for currency: {}", currencyCode);
            } catch (Exception ex) {
                logger.error("Failed to load exchange rates for currency {} due to: {}", currencyCode, ex.getMessage());
            }
        }

        logger.info("Exchange rates of all countries loaded successfully!");
    }

    public ExchangeRatesListDto getExchangeRatesFromApi(String currencyCode, @Nullable String date) {
        ResponseEntity<String> response = fetchExchangeRatesFromApi(currencyCode, date);
        GenericData genericData = unmarshalApiResponse(response);
        if (genericData == null || genericData.getDataSet() == null) {
            throw new RuntimeException("Invalid API response for currency: " + currencyCode);
        }
        return mapGenericDataToExchangeRates(genericData);
    }

    private ResponseEntity<String> fetchExchangeRatesFromApi(String currencyCode, @Nullable String date) {
        try {
            logger.debug("Calling API to fetch {} EX rate on {}", currencyCode, date);
            return restClientUtil.fetchExchangeRates(currencyCode, date);
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching exchange rates for " + currencyCode, ex);
        }
    }

    private GenericData unmarshalApiResponse(ResponseEntity<String> response) {
        try {
            logger.debug("Received response: {}", response.getStatusCode());
            return XMLMapperUtil.unmarshalXML(response.getBody(), GenericData.class);
        } catch (Exception ex) {
            throw new RuntimeException("Error unmarshalling API response", ex);
        }
    }

    private ExchangeRatesListDto mapGenericDataToExchangeRates(GenericData genericData) {
        ExchangeRatesListDto exchangeRatesListDto = new ExchangeRatesListDto();
        exchangeRatesListDto.setCurrencyCode(getCurrencyCodeFromApi(genericData));
        exchangeRatesListDto.setExchangeRates(mapExchangeRatesToDto(genericData));
        return exchangeRatesListDto;
    }

    private String getCurrencyCodeFromApi(GenericData genericData) {
        return genericData.getDataSet().getSeries().getSeriesKey().getValues().stream()
                          .filter(value -> value.getId().equals(FXRateConstants.CURRENCY_CODE_API_PARAM))
                          .findFirst()
                          .map(GenericValue::getValue)
                          .orElseThrow(() -> new ResourceNotFoundException("Currency code not found in API response"));
    }

    private List<ExchangeRateDto> mapExchangeRatesToDto(GenericData genericData) {
        return genericData.getDataSet().getSeries().getObsList().stream()
                          .map(this::mapObsToExchangeRateDto)
                          .collect(Collectors.toList());
    }

    private ExchangeRateDto mapObsToExchangeRateDto(Obs obs) {
        ExchangeRateDto exRate = new ExchangeRateDto();
        exRate.setDate(obs.getObsDimension().getValue());
        if (obs.getObsValue() != null) {
            exRate.setExchangeRate(new BigDecimal(obs.getObsValue().getValue()));
        }
        return exRate;
    }

    @Async
    @Transactional
    private CompletableFuture<Void> saveExchangeRates(String currencyCode, ExchangeRatesListDto exchangeRatesList) {

        List<List<ExchangeRateDto>> partitions = partitionList(exchangeRatesList.getExchangeRates(), BATCH_SIZE);

        for (List<ExchangeRateDto> batch : partitions) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<ExchangeRateEntity> rateEntities = new ArrayList<>();
                for (ExchangeRateDto rateDto : batch) {
                    ExchangeRateEntity rateEntity = new ExchangeRateEntity();
                    rateEntity.setRate(rateDto.getExchangeRate());
                    rateEntity.setDate(LocalDate.parse(rateDto.getDate()));
                    rateEntity.setUniqueKey(currencyCode + "_" + rateDto.getDate());
                    rateEntity.setCurrency(new CurrencyEntity(currencyCode, null, null));
                    rateEntities.add(rateEntity);
                }
                exchangeRateRepository.saveAll(rateEntities);
            });
        }

        return CompletableFuture.completedFuture(null);
    }

    public static <T> List<List<T>> partitionList(List<T> list, int size) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(new ArrayList<>(list.subList(i, Math.min(i + size, list.size()))));
        }
        return partitions;
    }

}
