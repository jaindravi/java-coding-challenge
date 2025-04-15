package com.crewmeister.cmcodingchallenge.exception;

import com.crewmeister.cmcodingchallenge.model.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.net.SocketTimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler exceptionHandler;
    private WebRequest mockWebRequest;

    @BeforeEach
    void setup() {
        exceptionHandler = new GlobalExceptionHandler();
        mockWebRequest = mock(WebRequest.class);
    }

    @Test
    void testHandleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Currency not found");

        ResponseEntity<?> response = exceptionHandler.handleResourceNotFoundException(ex, mockWebRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse error = (ErrorResponse) response.getBody();
        assertThat(error.getError()).isEqualTo("Resource not found");
        assertThat(error.getMessage()).isEqualTo("Currency not found");
    }

    @Test
    void testHandleSocketTimeoutException() {
        SocketTimeoutException ex = new SocketTimeoutException("External API timeout");

        ResponseEntity<?> response = exceptionHandler.handleTimeoutException(ex, mockWebRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.REQUEST_TIMEOUT);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse error = (ErrorResponse) response.getBody();
        assertThat(error.getError()).isEqualTo("Request Timeout");
        assertThat(error.getMessage()).isEqualTo("External API timeout");
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new RuntimeException("Unexpected failure");

        ResponseEntity<?> response = exceptionHandler.handleGlobalException(ex, mockWebRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse error = (ErrorResponse) response.getBody();
        assertThat(error.getError()).isEqualTo("Internal Server Error");
        assertThat(error.getMessage()).isEqualTo("Unexpected failure");
    }
}
