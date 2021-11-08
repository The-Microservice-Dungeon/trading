package com.example.trading.station;

import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Station {
    @Id
    @Getter
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID stationId;

    @Getter
    private int x;

    @Getter
    private int y;

    public Station() {}

    public Station(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean checkPosition(int x, int y) {
        return (this.x == x) && (this.y == y);
    }
}
