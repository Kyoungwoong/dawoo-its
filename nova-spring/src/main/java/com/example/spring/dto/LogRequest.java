package com.example.spring.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;

public class LogRequest implements Request {
    private String cardId;
    private String status;
    private String from;
    private String to;
    private int limit;

    public LogRequest() {
        this.limit = 20;
    }

    public LogRequest(String cardId, String status, String from, String to, int limit) {
        this.cardId = cardId;
        this.status = status;
        this.from = from;
        this.to = to;
        this.limit = limit;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
