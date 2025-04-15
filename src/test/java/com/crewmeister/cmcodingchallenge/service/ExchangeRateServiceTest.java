package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.entity.CurrencyEntity;
import com.crewmeister.cmcodingchallenge.entity.ExchangeRateEntity;
import com.crewmeister.cmcodingchallenge.exception.ResourceNotFoundException;
import com.crewmeister.cmcodingchallenge.model.ConvertedAmountDto;
import com.crewmeister.cmcodingchallenge.model.ExchangeRatesListDto;
import com.crewmeister.cmcodingchallenge.repository.ExchangeRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class ExchangeRateServiceTest {
    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testGetExchangeRates_byDate_success() {
        String currencyCode = "USD";
        String date = "2023-04-01";
        String uniqueKey = currencyCode + "_" + date;

        ExchangeRateEntity mockEntity = new ExchangeRateEntity();
        mockEntity.setDate(LocalDate.parse(date));
        mockEntity.setRate(new BigDecimal("1.1"));
        mockEntity.setCurrency(new CurrencyEntity(currencyCode, "US Dollar", null));

        when(exchangeRateRepository.findByUniqueKey(uniqueKey)).thenReturn(mockEntity);

        ExchangeRatesListDto result = exchangeRateService.getExchangeRates(currencyCode, date);

        assertThat(result.getCurrencyCode()).isEqualTo("USD");
        assertThat(result.getExchangeRates().get(0).getExchangeRate()).isEqualTo("1.1");
    }

    @Test
    void testGetAmountInEUR_success() {
        String currencyCode = "USD";
        String date = "2023-04-01";
        String uniqueKey = currencyCode + "_" + date;
        BigDecimal amount = new BigDecimal("110");

        ExchangeRateEntity mockEntity = new ExchangeRateEntity();
        mockEntity.setDate(LocalDate.parse(date));
        mockEntity.setRate(new BigDecimal("1.1"));
        mockEntity.setCurrency(new CurrencyEntity(currencyCode, "US Dollar", null));

        when(exchangeRateRepository.findByUniqueKey(uniqueKey)).thenReturn(mockEntity);

        ConvertedAmountDto result = exchangeRateService.getAmountInEUR(currencyCode, amount, date);

        assertThat(result.getAmountInEUR()).isEqualTo(new BigDecimal("100.0000"));
    }

    @Test
    void testGetExchangeRates_noDate_throwsExceptionIfNoData() {
        String currencyCode = "USD";

        when(exchangeRateRepository.findAllByCurrencyCode(currencyCode)).thenReturn(List.of());

        assertThatThrownBy(() -> exchangeRateService.getExchangeRates(currencyCode, null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No exchange rates found");
    }

    @Test
    void testGetAmountInEUR_dataNotFound_throwsException() {
        String currencyCode = "USD";
        String date = "2023-04-01";
        BigDecimal amount = new BigDecimal("100");

        when(exchangeRateRepository.findByUniqueKey("USD_2023-04-01")).thenReturn(null);

        assertThatThrownBy(() -> exchangeRateService.getAmountInEUR(currencyCode, amount, date))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Exchange Rate not found");
    }
}
