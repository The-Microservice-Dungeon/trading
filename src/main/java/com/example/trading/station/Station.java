package com.example.trading.station;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Station {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int stationId;

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
