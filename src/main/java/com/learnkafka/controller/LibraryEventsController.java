package com.learnkafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.domain.LibraryEventType;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import com.learnkafka.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LibraryEventsController {

    @Autowired
    Producer producer;
    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException {
        producer.sendLibraryEvent(libraryEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }
    @PutMapping("/v1/libraryevent")
    public ResponseEntity<?> updateLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException {
        ResponseEntity<String> BAD_REQUEST = validateLibraryEvent(libraryEvent);
        if (BAD_REQUEST != null) return BAD_REQUEST;
        producer.sendLibraryEvent(libraryEvent);
        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }

    private static ResponseEntity<String> validateLibraryEvent(LibraryEvent libraryEvent) {
        String errorLog=null;
        if(libraryEvent.libraryEventId()==null){
            errorLog="Please pass a correct LibraryEventId";
            log.error(errorLog);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorLog);
        }
        if(!libraryEvent.libraryEventType().equals(LibraryEventType.UPDATE)){
            errorLog="Please pass a correct LibraryEventType";
            log.error(errorLog);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorLog);
        }
        return null;
    }
}
