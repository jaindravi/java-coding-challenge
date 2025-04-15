package com.crewmeister.cmcodingchallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ExchangeRateDto {
    private BigDecimal ExchangeRate;
    private String date;
   // private String currencyCode;
}
