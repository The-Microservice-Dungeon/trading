package com.example.trading.station;

import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Planet {
    @Id
    @Getter
//    @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID planetId;

    @Getter
    private String type;

    public Planet() {}

    public Planet(UUID planetId, String type) {
        this.planetId = planetId;
        this.type = type;
    }

    public boolean isStationOrSpawn() {
        return this.type == "station" || this.type == "spawn";
    }
}
