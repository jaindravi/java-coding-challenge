package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.entity.ExchangeRateEntity;
import com.crewmeister.cmcodingchallenge.exception.ResourceNotFoundException;
import com.crewmeister.cmcodingchallenge.model.ConvertedAmountDto;
import com.crewmeister.cmcodingchallenge.model.ExchangeRateDto;
import com.crewmeister.cmcodingchallenge.model.ExchangeRatesListDto;
import com.crewmeister.cmcodingchallenge.repository.ExchangeRateRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExchangeRateService {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);

    @Autowired
    ExchangeRateRepository exchangeRateRepository;

    public ExchangeRatesListDto getExchangeRates(String currencyCode, @Nullable String date){
        ExchangeRatesListDto exchangeRatesListDto = new ExchangeRatesListDto();
        exchangeRatesListDto.setCurrencyCode(currencyCode);
        if(date!=null && !date.isEmpty()){
            logger.debug("fetching {} EX rate on {} : ", currencyCode, date);
            String uniqueKey = currencyCode+"_"+date;
            ExchangeRateEntity entity = exchangeRateRepository.findByUniqueKey(uniqueKey);
            if (entity == null) {
                throw new ResourceNotFoundException("Exchange rate not found for " + uniqueKey);
            }
            exchangeRatesListDto.setExchangeRates(List.of(mapEntityToDto(entity)));
        } else{
            logger.debug("fetching all EX rates for : {}", currencyCode);
            List<ExchangeRateEntity> entities = exchangeRateRepository.findAllByCurrencyCode(currencyCode);
            if (entities.isEmpty()) {
                throw new ResourceNotFoundException("No exchange rates found for " + currencyCode);
            }
            exchangeRatesListDto.setExchangeRates(mapEntityListToDto(entities));
        }
        return exchangeRatesListDto;
    }

    private static List<ExchangeRateDto> mapEntityListToDto(List<ExchangeRateEntity> rateEntities){
        return rateEntities.stream()
                           .map(ExchangeRateService::mapEntityToDto)
                           .collect(Collectors.toList());
    }

    private static ExchangeRateDto mapEntityToDto(ExchangeRateEntity entity){
        ExchangeRatesListDto listDto = new ExchangeRatesListDto();
        listDto.setCurrencyCode(entity.getCurrency().getCode());
        ExchangeRateDto dto = new ExchangeRateDto();
        dto.setExchangeRate(entity.getRate());
        dto.setDate(entity.getDate().toString());
        return dto;
    }

    public ConvertedAmountDto getAmountInEUR(String currencyCode, BigDecimal amount,
                                             String date) {

        String uniqueKey = currencyCode+"_"+date;
        logger.debug("fetching exchange rate for {} : ", uniqueKey);
        ExchangeRateEntity exRateEntity = exchangeRateRepository.findByUniqueKey(uniqueKey);

        if(null!=exRateEntity){
            return calculateAmountAndMapToDto(exRateEntity,amount);
        }
        throw new ResourceNotFoundException("Exchange Rate not found " +date);
    }

    private static ConvertedAmountDto calculateAmountAndMapToDto(ExchangeRateEntity entity, BigDecimal amount){
        ConvertedAmountDto dto = new ConvertedAmountDto();
        if(entity.getRate()!=null){
            BigDecimal convertedAmount = amount.divide(entity.getRate(), 4, RoundingMode.HALF_UP);
            dto.setExchangeRate(entity.getRate());
            dto.setAmountInEUR(convertedAmount);
            dto.setCurrencyCode(entity.getCurrency().getCode());
            dto.setDate(entity.getRate().toString());
            return dto;
        }
        throw new ResourceNotFoundException("Exchange Rate not found "+ entity.getDate());
    }

}