package com.crewmeister.cmcodingchallenge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "exchange_rate", uniqueConstraints = @UniqueConstraint(columnNames = {"uniqueKey"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_code")
    private CurrencyEntity currency;

    private LocalDate date;

    @Column(precision = 15, scale = 4)
    private BigDecimal rate;

    private String uniqueKey;

    @PrePersist
    public void generateUniqueKey() {
        if (currency.getCode() != null && date != null) {
            this.uniqueKey = currency.getCode() + "_" + date.toString();
        }
    }
}
