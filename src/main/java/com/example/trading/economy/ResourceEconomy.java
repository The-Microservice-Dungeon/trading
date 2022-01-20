package com.example.trading.economy;

import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
public class ResourceEconomy {
    @Id
    @Getter
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID economyId;

    private int roundCount;
    private int demand;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="resourceHistory")
    @MapKeyColumn(name="roundNumber")
    @Column(name="resourceHistory")
    @Getter
    private Map<Integer, Integer> history;

    public ResourceEconomy() {
        this.history = new HashMap<>();
        this.roundCount = 5;
        this.demand = 10;
    }

    public void addHistory(int roundNumber, int amount) {
        this.history.put(roundNumber, amount);
    }

    public float calculateNewPriceFactor(int currentRound) {
        int boughtAmount = 0;

        for (int i = 0; i < this.roundCount; i++) {
            if (this.history.containsKey(currentRound - i)) {
                boughtAmount += this.history.get(currentRound - i);
            }
        }

        float factor = this.demand / (float)boughtAmount;
        if (factor < 1) return factor;
        else return 1;
    }

    public void patchParameters(int newRoundCount, int newDemand) {
        this.roundCount = newRoundCount;
        this.demand = newDemand;
    }
}
