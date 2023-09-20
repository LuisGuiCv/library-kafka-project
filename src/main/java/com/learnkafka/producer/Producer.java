package com.learnkafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    public CompletableFuture<SendResult<Integer, String>> sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key=libraryEvent.libraryEventId();
        String value=objectMapper.writeValueAsString(libraryEvent);
        List<Header> recordHeaders=List.of(new RecordHeader("event-source","scanner".getBytes()));
        ProducerRecord producerRecord=new ProducerRecord<>(topic,null,key,value,recordHeaders);
        CompletableFuture<SendResult<Integer, String>> completableFuture = kafkaTemplate.send(producerRecord);
        return completableFuture.whenComplete((sendResult,throwable)->{
            if(throwable!=null){
                handleFailure(key,value,throwable);
            }else{
                handleSuccess(key,value,sendResult);
            }
        });
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> sendResult) {
    log.info("Message sent succesfully for the key: {} and the value: {},partition is {} ",key,value,sendResult.getRecordMetadata().partition());
    }

    private void handleFailure(Integer key, String value, Throwable throwable) {
    log.error("Error sending the message and the exception is {} : ",throwable.getMessage(),throwable);
    }
}
