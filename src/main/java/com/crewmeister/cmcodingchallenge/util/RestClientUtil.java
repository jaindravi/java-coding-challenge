package com.crewmeister.cmcodingchallenge.util;

import com.crewmeister.cmcodingchallenge.constants.FXRateConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class RestClientUtil {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${bundesbank.api.url}")
    private String baseUrl;

    @Value("${bundesbank.api.endpoints.rates}")
    private String ratesEndpoint;

    @Value("${bundesbank.api.endpoints.currencies}")
    private String currenciesEndpoint;

    @Value("${bundesbank.api.default-detail}")
    private String detail;

    public ResponseEntity<String> fetchCurrencyList(){
        URI currencyUri = UriComponentsBuilder
                .fromHttpUrl(baseUrl+currenciesEndpoint)
                .build()
                .encode()
                .toUri();
        return makeRestCall(currencyUri);
    }

    public ResponseEntity<String> fetchExchangeRates(String currencyCode, String date){
        String series = FXRateConstants.TIME_SERIES.replace(FXRateConstants.ISOCODE_PLACEHOLDER, currencyCode);
        URI ratesURI = UriComponentsBuilder
                .fromHttpUrl(baseUrl+ratesEndpoint+series)
                .queryParamIfPresent(FXRateConstants.QUERY_PARAM_START_PERIOD, Optional.ofNullable(date))
                .queryParamIfPresent(FXRateConstants.QUERY_PARAM_END_PERIOD, Optional.ofNullable(date))
                .queryParam(FXRateConstants.QUERY_PARAM_DETAIL, detail)
                .build()
                .encode()
                .toUri();
        return makeRestCall(ratesURI);
    }

    public ResponseEntity<String> makeRestCall(URI uri){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_XML));

        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }
}