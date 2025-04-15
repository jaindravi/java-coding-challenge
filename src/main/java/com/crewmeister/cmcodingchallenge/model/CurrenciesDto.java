package com.crewmeister.cmcodingchallenge.model;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class CurrenciesDto {

   private List<CurrencyDto> currencies;
}
