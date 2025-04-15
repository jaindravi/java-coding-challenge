package com.crewmeister.cmcodingchallenge.scheduler;

import com.crewmeister.cmcodingchallenge.service.CurrencyBootstrapLoader;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateBootstrapLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyAndRateScheduler {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyAndRateScheduler.class);

    @Autowired
    CurrencyBootstrapLoader currencyBootstrapLoader;

    @Autowired
    ExchangeRateBootstrapLoader exRateBootstrapLoader;

    @Async
    @PostConstruct
    public void loadInitialData(){
        logger.info("Loading initial data from api to DB");
        loadAllAvailableCurrencies();
        loadHistoricalDataFromApiToDB();
    }

    public void loadAllAvailableCurrencies(){
        currencyBootstrapLoader.loadAllCurrencies();
    }

    public void loadHistoricalDataFromApiToDB(){
        logger.info("loading historical exchange rates from API");
        exRateBootstrapLoader.loadExchangeRatesFromApiToDB(null);
    }

    /* runs every day at 5 AM
    Assuming data gets updated everyday before 5 am */

    @Scheduled(cron = "0 0 5 * * *")
    public void fetchTodayExchangeRate() {
        logger.info("fetching exchange rates for {} from API ", LocalDate.now());
        exRateBootstrapLoader.loadExchangeRatesFromApiToDB(LocalDate.now().toString());
    }

}
