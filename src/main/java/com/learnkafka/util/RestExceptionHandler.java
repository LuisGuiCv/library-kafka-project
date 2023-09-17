package com.learnkafka.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<?> handleException(MethodArgumentNotValidException ex){
                String errorMessage = ex.getBindingResult().getFieldErrors().stream().map(fieldError ->
                                fieldError.getField() + " - " + fieldError.getDefaultMessage())
                        .sorted()
                        .collect(Collectors.joining(","));
                log.info("error message {} ",errorMessage);
                return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
}
