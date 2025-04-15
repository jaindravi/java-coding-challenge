package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.entity.CurrencyEntity;
import com.crewmeister.cmcodingchallenge.externalapi.metadata.*;
import com.crewmeister.cmcodingchallenge.model.CurrencyDto;
import com.crewmeister.cmcodingchallenge.repository.CurrencyRepository;
import com.crewmeister.cmcodingchallenge.util.RestClientUtil;
import com.crewmeister.cmcodingchallenge.util.XMLMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.crewmeister.cmcodingchallenge.constants.FXRateConstants.CURRENCY_CODE_SIZE;
import static com.crewmeister.cmcodingchallenge.constants.FXRateConstants.LANG_ENGLISH;

@Slf4j
@Component
public class CurrencyBootstrapLoader {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateBootstrapLoader.class);

    @Autowired
    private RestClientUtil restClientUtil;

    @Autowired
    CurrencyRepository currencyRepository;


    @Retryable(value = SocketTimeoutException.class, maxAttemptsExpression = "${bundesbank.api.retry.attempts}",
            backoff = @Backoff(delayExpression = "${bundesbank.api.retry.delay}"))
    public void loadAllCurrencies(){
        logger.info("loading currencies to DB");
        List<CurrencyDto> currencies = fetchCurrenciesFromApi();
        logger.debug("retrieved list from API of size : {}", currencies.size());

        List<CurrencyEntity> entityList = new ArrayList<>();
        for(CurrencyDto currencyDto : currencies){
            CurrencyEntity entity = new CurrencyEntity();
            entity.setCode(currencyDto.getCurrencyCode());
            entity.setName(currencyDto.getCurrencyName());
            entityList.add(entity);
        }
        logger.info("saving list of currencies to DB");
        currencyRepository.saveAll(entityList);
    }

    private List<CurrencyDto> fetchCurrenciesFromApi() {
        try{
            logger.debug("Calling API to fetch currencies");
            ResponseEntity<String> response = restClientUtil.fetchCurrencyList();
            logger.debug("Received response from API");
            StructureWrapper structureWrapper = XMLMapperUtil.unmarshalXML(response.getBody(), StructureWrapper.class);
            return mapCurrenciesList(structureWrapper);
        } catch (Exception ex){
            throw new RuntimeException("Internal server error "+ ex.getMessage());
        }
    }

    private static List<CurrencyDto> mapCurrenciesList(StructureWrapper structureWrapper) {
        logger.debug("Mapping list of currencies");
        List<Codelist> codelists = getCodelistsFromStructure(structureWrapper);

        return codelists.stream()
                .flatMap(CurrencyBootstrapLoader::mapToCurrencyStream)
                .collect(Collectors.toList());
    }

    private static List<Codelist> getCodelistsFromStructure(StructureWrapper structureWrapper) {
        return Optional.ofNullable(structureWrapper.getStructures())
                       .map(Structures::getCodelists)
                       .map(Codelists::getCodelist)
                       .orElse(Collections.emptyList());
    }

    private static Stream<CurrencyDto> mapToCurrencyStream(Codelist codelist){
        return Optional.ofNullable(codelist.getCodes())
                .orElse(Collections.emptyList())
                .stream()
                .filter(code -> code.getId().length()==CURRENCY_CODE_SIZE)
                .map(CurrencyBootstrapLoader::mapToCurrencyFromCode);
    }

    private static CurrencyDto mapToCurrencyFromCode(Code code) {
        CurrencyDto currencyDto = new CurrencyDto();
        currencyDto.setCurrencyCode(code.getId());

        Optional.ofNullable(code.getNames())
                .orElse(Collections.emptyList())
                .stream()
                .filter(name -> LANG_ENGLISH.equalsIgnoreCase(name.getLang()))
                .findFirst()
                .ifPresent(name -> currencyDto.setCurrencyName(name.getValue()));
        return currencyDto;
    }

}
