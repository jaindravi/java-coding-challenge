package com.crewmeister.cmcodingchallenge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "currency")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CurrencyEntity {

    @Id
    private String code;

    private String name;

    @OneToMany(mappedBy = "currency", cascade = CascadeType.ALL)
    private List<ExchangeRateEntity> exchangeRates = new ArrayList<>();
}
