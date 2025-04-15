package com.crewmeister.cmcodingchallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class ConvertedAmountDto {
    private BigDecimal amountInEUR;
    private BigDecimal exchangeRate;
    private String currencyCode;
    private String date;
}
