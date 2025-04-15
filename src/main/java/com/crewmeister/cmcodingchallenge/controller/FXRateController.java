package com.crewmeister.cmcodingchallenge.controller;

import com.crewmeister.cmcodingchallenge.model.ConvertedAmountDto;
import com.crewmeister.cmcodingchallenge.model.CurrencyDto;
import com.crewmeister.cmcodingchallenge.model.ExchangeRatesListDto;
import com.crewmeister.cmcodingchallenge.service.CurrencyService;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateBootstrapLoader;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController()
@RequestMapping("/api")
public class FXRateController {

    @Autowired
    private ExchangeRateService exRateService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private ExchangeRateBootstrapLoader exchangeRateBootstrapLoader;

    @GetMapping("/currencies")
    @Operation(summary = "list of all available currencies")
    public ResponseEntity<List<CurrencyDto>> getCurrencies() {
        List<CurrencyDto> currencyDtoList = currencyService.getAllCurrencies();
        return ResponseEntity.ok(currencyDtoList);
    }

    @GetMapping("/exchangeRates")
    @Operation(summary = "EUR-FX exchange rates at all available dates or a particular date")
    public ResponseEntity<ExchangeRatesListDto> getExchangesRates(
            @RequestParam String currencyCode,
            @RequestParam (required = false) String date) {
        ExchangeRatesListDto rates = exRateService.getExchangeRates(currencyCode,date);
        return ResponseEntity.ok(rates);
    }

    @GetMapping("/convertToEUR")
    @Operation(summary = "FX amount for a given currency converted to EUR on a particular day")
    public ResponseEntity<ConvertedAmountDto> getAmountInEUR(
            @RequestParam String currencyCode,
            @RequestParam BigDecimal amount,
            @RequestParam String date) {
        ConvertedAmountDto convertedAmount = exRateService.getAmountInEUR(currencyCode, amount, date);
        return ResponseEntity.ok(convertedAmount);
    }


}
