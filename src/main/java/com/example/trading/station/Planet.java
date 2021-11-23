package com.example.trading.station;

import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Planet {
    @Id
    @Getter
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID planetId;

    @Getter
    @Column(columnDefinition = "BINARY(16)")
    private UUID originalPlanetId;

    @Getter
    private String type;

    public Planet() {}

    public Planet(UUID originalPlanetId, String type) {
        this.originalPlanetId = originalPlanetId;
        this.type = type;
    }

    public boolean isStationOrSpawn() {
        return this.type == "station" || this.type == "spawn";
    }
}
