package com.murat.featurephone.common;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(NoSuchElementException.class)
    public void handleNotFound(NoSuchElementException exception){
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                exception
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleBadRequest(IllegalArgumentException exception){
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                exception
        );
    }



}
