package com.example.trading.economy;

import lombok.Getter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
public class ResourceEconomy {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int economyId;

    @ElementCollection
    @CollectionTable(name="history")
    @MapKeyColumn(name="roundNumber")
    @Column(name="amount")
    private Map<Integer, Integer> history;

    public ResourceEconomy() {
        this.history = new HashMap<>();
    }

    public void addHistory(int roundNumber, int amount) {
        this.history.put(roundNumber, amount);
    }

    public int calculateNewPrice() {
        return 1;
    }
}
