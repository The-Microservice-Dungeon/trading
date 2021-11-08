package com.example.trading.economy;

import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
public class ItemEconomy {
    @Id
    @Getter
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID economyId;

    private int roundCount;
    private int stock;

    @ElementCollection
    @CollectionTable(name="history")
    @MapKeyColumn(name="roundNumber")
    @Column(name="amount")
    private Map<Integer, Integer> history;

    public ItemEconomy() {
        this.history = new HashMap<>();
        this.roundCount = 5;
        this.stock = 10;
    }

    public void addHistory(int currentRound, int newAmount) {
        if (this.history.containsKey(currentRound)) {
            int currentRoundAmount = this.history.get(currentRound);
            this.history.put(currentRound, currentRoundAmount + newAmount);
        } else {
            this.history.put(currentRound, newAmount);
        }
    }

    public float calculateNewPriceFactor(int currentRound) {
        int soldStock = 0;

        for (int i = 0; i < this.roundCount; i++) {
            if (this.history.containsKey(currentRound - i)) {
                soldStock += this.history.get(currentRound - i);
            }
        }

        float factor = soldStock / (float)this.stock;
        if (factor > 1) return factor;
        else return 1;
    }
}
