package com.example.trading.player;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Player {
    @Id
    @Getter
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID playerId;

    @Getter
    @Setter
    private int robotCount;

    @Getter
    private int moneyAmount;

    public Player() {}

    public Player(int startMoney) {
        this.robotCount = 1;
        this.moneyAmount = startMoney;
    }

    public int reduceMoney(int amount) {
        if (amount > this.moneyAmount || amount < 0)
            throw new IllegalArgumentException("The amount of money can not be negative.");

        this.moneyAmount -= amount;
        return this.moneyAmount;
    }

    public int addMoney(int amount) {
        this.moneyAmount += amount;
        return this.moneyAmount;
    }

    // @TODO: safe money to mysql database
}
