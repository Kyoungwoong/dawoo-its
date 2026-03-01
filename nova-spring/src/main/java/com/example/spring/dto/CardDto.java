package com.example.spring.dto;

public class CardDto implements Comparable<CardDto> {
    String cardId;
    int totalTrips;

    public CardDto(String cardId, int totalTrips) {
        this.cardId = cardId;
        this.totalTrips = totalTrips;
    }

    public String getCardId() {
        return cardId;
    }

    public int getTotalTrips() {
        return totalTrips;
    }

    @Override
    public int compareTo(CardDto cardDto) {
        if (this.totalTrips == cardDto.getTotalTrips()) {
            return this.cardId.compareTo(cardDto.getCardId());
        }
        return Integer.compare(cardDto.getTotalTrips(), this.getTotalTrips());
    }
}
