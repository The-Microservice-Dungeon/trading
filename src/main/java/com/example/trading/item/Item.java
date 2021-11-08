package com.example.trading.item;

import com.example.trading.economy.ItemEconomy;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Locale;
import java.util.UUID;

@Entity
public class Item {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int itemId;

    @Getter
    private String name;

    @Getter
    private String description;

    @Getter
    @Setter
    private int currentPrice;

    @Getter
    private int originalPrice;

    @OneToOne(cascade = CascadeType.ALL)
    private ItemEconomy economy;

    public Item() {}

    public Item(String name, String description, int price) {
        this.name = name.toLowerCase();
        this.description = description;
        this.currentPrice = price;
        this.originalPrice = price;
        this.economy = new ItemEconomy();
    }

    public void addHistory(int roundNumber) {
        this.economy.addHistory(roundNumber, 1);
    }

    public void calculateNewPrice(int currentRound) {
//        this.currentPrice = this.economy.calculateNewPrice(currentRound);
        int temp = this.economy.calculateNewPrice(currentRound);
        System.out.println(temp);
    }
}
