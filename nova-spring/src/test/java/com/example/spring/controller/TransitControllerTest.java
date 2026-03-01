package com.example.spring.controller;

import com.example.spring.dto.CardDto;
import com.example.spring.service.TransitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransitController.class)
class TransitControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TransitService transitService;

    @Test
    void getCards_returnsOkResponseDto() throws Exception {
        when(transitService.getCardTripCounts()).thenReturn(List.of(
                new CardDto("card-1", 3),
                new CardDto("card-2", 1)
        ));

        mvc.perform(get("/api/transit/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].cardId").value("card-1"))
                .andExpect(jsonPath("$.data[0].totalTrips").value(3));
    }
}
