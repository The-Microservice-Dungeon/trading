package com.example.trading.resource;

import com.example.trading.economy.ResourceEconomy;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Locale;
import java.util.UUID;

@Entity
public class Resource {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int resourceId;

    @Getter
    private String name;

    @Getter
    @Setter
    private int currentPrice;

    @Getter
    private int originalPrice;

    @OneToOne(cascade = CascadeType.ALL)
    private ResourceEconomy economy;

    public Resource() {}

    public Resource(String name, int price) {
        this.name = name.toLowerCase();
        this.currentPrice = price;
        this.originalPrice = price;
        this.economy = new ResourceEconomy();
    }

    public void addHistory(int amount, int roundNumber) {
        this.economy.addHistory(roundNumber, amount);
    }
}
