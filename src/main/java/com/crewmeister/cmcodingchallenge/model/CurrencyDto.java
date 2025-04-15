package com.crewmeister.cmcodingchallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class CurrencyDto {
    private String currencyCode;
    private String currencyName;
}
