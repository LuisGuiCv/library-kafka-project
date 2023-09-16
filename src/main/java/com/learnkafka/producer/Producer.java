package com.learnkafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class Producer {

    @Value("${spring.kafka.topic}")
    public String topic;
    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public Producer(KafkaTemplate<Integer, String> kafkaTemplate,ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper=objectMapper;
    }

    public SendResult<Integer, String> sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        Integer key=libraryEvent.libraryEventId();
        String value=objectMapper.writeValueAsString(libraryEvent);
        SendResult<Integer, String> sendResult = kafkaTemplate.send(topic, key, value).get(3, TimeUnit.SECONDS);
        handleSuccess(key,value,sendResult);
        return sendResult;
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> sendResult) {
    log.info("Message sent succesfully for the key: {} and the value: {},partition is {} ",key,value,sendResult.getRecordMetadata().partition());
    }

    private void handleFailure(Integer key, String value, Throwable throwable) {
    log.error("Error sending the message and the exception is {} : ",throwable.getMessage(),throwable);
    }
}
