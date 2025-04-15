package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.entity.CurrencyEntity;
import com.crewmeister.cmcodingchallenge.model.CurrencyDto;
import com.crewmeister.cmcodingchallenge.repository.CurrencyRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CurrencyService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    @Autowired
    CurrencyRepository currencyRepository;

    public List<CurrencyDto> getAllCurrencies() {
        logger.info("fetching currencies from DB");
        List<CurrencyEntity> currencyEntityList = currencyRepository.findAll();
        logger.debug("retrieved list from DB of size : {}", currencyEntityList.size());
        return currencyEntityList.stream().map(currencyEntity -> {
            CurrencyDto currencyDto = new CurrencyDto();
            currencyDto.setCurrencyCode(currencyEntity.getCode());
            currencyDto.setCurrencyName(currencyEntity.getName());
            return currencyDto;
        }).collect(Collectors.toList());
    }

}
