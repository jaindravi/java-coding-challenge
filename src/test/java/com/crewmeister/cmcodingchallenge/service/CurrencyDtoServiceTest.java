package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.entity.CurrencyEntity;
import com.crewmeister.cmcodingchallenge.model.CurrencyDto;
import com.crewmeister.cmcodingchallenge.repository.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class CurrencyDtoServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private CurrencyService currencyService;

    private CurrencyEntity currencyEntity1;
    private CurrencyEntity currencyEntity2;

    @BeforeEach
    public void setUp() {
        currencyEntity1 = new CurrencyEntity("USD", "United States Dollar",null);
        currencyEntity2 = new CurrencyEntity("EUR", "Euro",null);
        openMocks(this);
    }

    @Test
    public void testGetAllCurrencies() {

        when(currencyRepository.findAll()).thenReturn(Arrays.asList(currencyEntity1, currencyEntity2));

        List<CurrencyDto> result = currencyService.getAllCurrencies();

        assertEquals(2, result.size(), "The size of the list should be 2.");
        assertEquals("USD", result.get(0).getCurrencyCode(), "First currency code should be USD.");
        assertEquals("United States Dollar", result.get(0).getCurrencyName(), "First currency name should be 'United States Dollar'.");
        assertEquals("EUR", result.get(1).getCurrencyCode(), "Second currency code should be EUR.");
        assertEquals("Euro", result.get(1).getCurrencyName(), "Second currency name should be 'Euro'.");

        verify(currencyRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllCurrencies_NoData() {
        when(currencyRepository.findAll()).thenReturn(Arrays.asList());

        List<CurrencyDto> result = currencyService.getAllCurrencies();

        assertEquals(0, result.size(), "The size of the list should be 0 when there is no data.");
        verify(currencyRepository, times(1)).findAll();
    }
}
