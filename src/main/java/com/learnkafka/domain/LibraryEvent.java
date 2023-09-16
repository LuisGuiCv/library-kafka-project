package com.learnkafka.domain;

public record LibraryEvent(Integer libraryEventId, LibraryEvenType libraryEventType, Book book) {


}
