package com.example.trading.station;

import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Planet {
    @Id
    @Getter
    @Column(columnDefinition = "BINARY(16)")
    private UUID planetId;

    public Planet() {}

    public Planet(UUID planetId) {
        this.planetId = planetId;
    }
}
