package com.example.trading.core;


import org.apache.kafka.common.header.Headers;

import java.util.UUID;

public class DomainEvent {
    private String payload;
    private String type;
    private String key;
    private String version;
    private String timestamp;
    private String id;

    public DomainEvent(String payload, Headers headers) {
        this.payload = payload;
        this.type = headers.lastHeader("type").value().toString();
        this.key = headers.lastHeader("transactionId").value().toString();
        this.version = headers.lastHeader("version").value().toString();
        this.timestamp = headers.lastHeader("timestamp").value().toString();
        this.id = headers.lastHeader("eventId").value().toString();
    }
}
