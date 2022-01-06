package com.example.trading.event;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.header.Headers;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class DomainEvent {
    @Id
    public String eventId;

    public String payload;
    public String type;
    public String transactionId;
    public String version;
    public String timestamp;

    public DomainEvent(String payload, Headers headers) {
        this.eventId = headers.lastHeader("eventId").value().toString();
        this.transactionId = headers.lastHeader("transactionId").value().toString();
        this.version = headers.lastHeader("version").value().toString();
        this.timestamp = headers.lastHeader("timestamp").value().toString();
        this.type = headers.lastHeader("type").value().toString();
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
