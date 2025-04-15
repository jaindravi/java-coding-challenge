package com.crewmeister.cmcodingchallenge.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ExchangeRatesListDto {
    private String currencyCode;
    private List<ExchangeRateDto> exchangeRates;
}
