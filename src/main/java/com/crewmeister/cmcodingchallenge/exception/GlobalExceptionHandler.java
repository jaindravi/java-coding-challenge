package com.crewmeister.cmcodingchallenge.exception;

import com.crewmeister.cmcodingchallenge.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.net.SocketTimeoutException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Resource not found", ex.getMessage());
        logger.error("Resource Not Found: {}", ex.getMessage());
        logger.debug("Resource Not Found Exception : ", ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseEntity<?> handleTimeoutException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Request Timeout", ex.getMessage());
        logger.error("Request Timeout: {}", ex.getMessage());
        logger.debug("Request Timeout Exception : ", ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.REQUEST_TIMEOUT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Internal Server Error", ex.getMessage());
        logger.error("Internal Server Error : {}", ex.getMessage());
        logger.debug("Global Exception : "+ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
