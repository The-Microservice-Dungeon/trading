package com.example.trading.player;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
public class Player {
    @Id
    @Getter
    @Column(columnDefinition = "BINARY(16)")
    private UUID playerId;

    @Getter
    private int moneyAmount;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name="roundNumber")
    @Column(name="balance")
    @Getter
    private Map<Integer, Integer> balanceHistory;

    public Player() {}

    public Player(UUID playerId, int startMoney) {
        this.playerId = playerId;
        this.moneyAmount = startMoney;
        this.balanceHistory = new HashMap<>();
        this.addCurrentBalanceToHistory(0);
    }

    public void addCurrentBalanceToHistory(int currentRound) {
        this.balanceHistory.put(currentRound, this.moneyAmount);
    }

    public int getMoneyAmountFromRound(int round) {
        return this.balanceHistory.get(round);
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
}
