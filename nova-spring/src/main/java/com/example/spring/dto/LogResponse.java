package com.example.spring.dto;

import com.example.spring.common.exception.DawooException;
import com.example.spring.domain.ErrorCode;
import com.example.spring.domain.Transit;

import java.util.Map;

public class LogResponse {
    private String logId;
    private String cardId;
    private String stationIn;
    private String stationOut;
    private int fare;
    private String timestamp;
    private String status;

    public LogResponse(String logId, String cardId, String stationIn, String stationOut, int fare, String timestamp, String status) {
        this.logId = logId;
        this.cardId = cardId;
        this.stationIn = stationIn;
        this.stationOut = stationOut;
        this.fare = fare;
        this.timestamp = timestamp;
        this.status = status;
    }

    public static LogResponse createLogResponse(Transit t) {
        int fare;
        try {
            fare = Integer.parseInt(String.valueOf(t.getFare()));
        } catch (NumberFormatException e) {
            throw new DawooException(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "Invalid fare value",
                    Map.of("logId", t.getLogId(), "fare", String.valueOf(t.getFare()))
            );
        }

        return new LogResponse(
                t.getLogId(),
                t.getCardId(),
                t.getStationIn(),
                t.getStationOut(),
                fare,
                t.getTimestamp(),
                t.getStatus()
        );
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getStationIn() {
        return stationIn;
    }

    public void setStationIn(String stationIn) {
        this.stationIn = stationIn;
    }

    public String getStationOut() {
        return stationOut;
    }

    public void setStationOut(String stationOut) {
        this.stationOut = stationOut;
    }

    public int getFare() {
        return fare;
    }

    public void setFare(int fare) {
        this.fare = fare;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
