package com.example.spring.domain;

public class Transit {
    String logId;
    String cardId;
    String stationIn;
    String stationOut;
    Object fare;
    String timestamp;
    String status;

    public Transit() {}

    public Transit(String logId, String cardId, String stationIn, String stationOut, Object fare, String timestamp, String status) {
        this.logId = logId;
        this.cardId = cardId;
        this.stationIn = stationIn;
        this.stationOut = stationOut;
        this.fare = fare;
        this.timestamp = timestamp;
        this.status = status;
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

    public Object getFare() {
        return fare;
    }

    public void setFare(Object fare) {
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
