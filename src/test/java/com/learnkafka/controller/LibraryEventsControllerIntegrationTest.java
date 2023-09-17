package com.learnkafka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.producer.Producer;
import com.learnkafka.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibraryEventsController.class)
class LibraryEventsControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    Producer producer;

    @Test
    void postLibraryEvent() throws Exception {

        String value=objectMapper.writeValueAsString(TestUtil.libraryEventRecord());
        Mockito.when(producer.sendLibraryEvent(Mockito.isA(LibraryEvent.class))).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/libraryevent")
                .content(value)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

    }

    @Test
    void invalidBookProperties() throws Exception {
        String value=objectMapper.writeValueAsString(TestUtil.bookRecordWithInvalidValues());
        Mockito.when(producer.sendLibraryEvent(Mockito.isA(LibraryEvent.class))).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/libraryevent")
                        .content(value)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateLibraryEventCorrectIdAndType() throws Exception {
        String value=objectMapper.writeValueAsString(TestUtil.libraryEventRecordUpdate());
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/libraryevent")
                .content(value)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    @Test
    void testUpdateLibraryInvalidFields() throws Exception {
        String value=objectMapper.writeValueAsString(TestUtil.libraryEventRecordUpdateWithNullLibraryEventId());
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/libraryevent")
                .content(value)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }
}