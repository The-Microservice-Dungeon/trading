package com.example.trading.event;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.header.Headers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class DomainEvent {
    @Id
    public String eventId;

    @Column(length = 3000)
    public String payload;

    public String type;
    public String transactionId;
    public String version;
    public String timestamp;

    public DomainEvent(String payload, Headers headers) {
        this.eventId = new String(headers.lastHeader("eventId").value(), StandardCharsets.UTF_8);
        this.transactionId = new String(headers.lastHeader("transactionId").value(), StandardCharsets.UTF_8);
        this.version = new String(headers.lastHeader("version").value(), StandardCharsets.UTF_8);
        this.timestamp = new String(headers.lastHeader("timestamp").value(), StandardCharsets.UTF_8);
        this.type = new String(headers.lastHeader("type").value(), StandardCharsets.UTF_8);
        this.payload = payload;
    }

    public DomainEvent(String payload,
                       String eventId,
                       String transactionId,
                       String version,
                       String timestamp,
                       String type) {
        this.eventId = eventId;
        this.transactionId = transactionId;
        this.version = version;
        this.timestamp = timestamp;
        this.type = type;
        this.payload = payload;
    }
}
