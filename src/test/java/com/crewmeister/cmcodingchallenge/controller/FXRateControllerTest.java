package com.crewmeister.cmcodingchallenge.controller;

import com.crewmeister.cmcodingchallenge.model.ConvertedAmountDto;
import com.crewmeister.cmcodingchallenge.model.CurrencyDto;
import com.crewmeister.cmcodingchallenge.model.ExchangeRatesListDto;
import com.crewmeister.cmcodingchallenge.service.CurrencyService;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateBootstrapLoader;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(FXRateController.class)
public class FXRateControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exRateService;

    @MockBean
    private CurrencyService currencyService;

    @MockBean
    private ExchangeRateBootstrapLoader exchangeRateBootstrapLoader;

    @InjectMocks
    private FXRateController fxRateController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(fxRateController).build();
        openMocks(this);
    }

    @Test
    public void testGetCurrencies() throws Exception {

        CurrencyDto currencyDto1 = new CurrencyDto("USD", "United States Dollar");
        CurrencyDto currencyDto2 = new CurrencyDto("EUR", "Euro");
        List<CurrencyDto> currencies = Arrays.asList(currencyDto1, currencyDto2);

        when(currencyService.getAllCurrencies()).thenReturn(currencies);

        mockMvc.perform(get("/api/currencies"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$[0].currencyCode").value("USD"))
               .andExpect(jsonPath("$[1].currencyCode").value("EUR"));

        verify(currencyService, times(1)).getAllCurrencies();
    }

    @Test
    public void testGetExchangesRates() throws Exception {
        String currencyCode = "USD";
        String date = "2025-04-15";
        ExchangeRatesListDto exchangeRatesListDto = new ExchangeRatesListDto();

        when(exRateService.getExchangeRates(currencyCode, date)).thenReturn(exchangeRatesListDto);

        mockMvc.perform(get("/api/exchangeRates")
                                .param("currencyCode", currencyCode)
                                .param("date", date))
               .andExpect(status().isOk())
               .andExpect(content().contentType("application/json"));

        verify(exRateService, times(1)).getExchangeRates(currencyCode, date);
    }

    @Test
    public void testGetAmountInEUR() throws Exception {
        String currencyCode = "USD";
        BigDecimal amount = new BigDecimal("1000");
        String date = "2025-04-15";
        ConvertedAmountDto convertedAmountDto = new ConvertedAmountDto(new BigDecimal("850"),new BigDecimal("2.43"),"EUR", date);

        when(exRateService.getAmountInEUR(currencyCode, amount, date)).thenReturn(convertedAmountDto);

        mockMvc.perform(get("/api/convertToEUR")
                                .param("currencyCode", currencyCode)
                                .param("amount", amount.toString())
                                .param("date", date))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.currencyCode").value("EUR"))
               .andExpect(jsonPath("$.amountInEUR").value("850"));

        verify(exRateService, times(1)).getAmountInEUR(currencyCode, amount, date);
    }
}
