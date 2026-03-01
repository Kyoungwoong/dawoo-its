package com.example.spring.service;

import com.example.spring.domain.Transit;
import com.example.spring.dto.CardDto;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TransitServiceTest {

    @Test
    void getCardTripCounts_countsAndSorts() throws Exception {
        TransitService service = new TransitService();

        List<Transit> transits = List.of(
                new Transit("1", "A", "S1", "S2", 0, "t1", "OK"),
                new Transit("2", "A", "S1", "S2", 0, "t2", "OK"),
                new Transit("3", "B", "S1", "S2", 0, "t3", "OK")
        );

        Field field = TransitService.class.getDeclaredField("transitList");
        field.setAccessible(true);
        field.set(service, transits);

        List<CardDto> result = service.getCardTripCounts();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCardId()).isEqualTo("A");
        assertThat(result.get(0).getTotalTrips()).isEqualTo(2);
        assertThat(result.get(1).getCardId()).isEqualTo("B");
        assertThat(result.get(1).getTotalTrips()).isEqualTo(1);
    }
}
