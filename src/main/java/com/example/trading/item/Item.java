package com.example.trading.item;

import com.example.trading.economy.ItemEconomy;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Locale;
import java.util.UUID;

@Entity
public class Item {
    @Id
    @Getter
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID itemId;

    @Getter
    private String name;

    @Getter
    private String description;

    @Getter
    @Setter
    private int currentPrice;

    @Getter
    private int originalPrice;

    @Getter
    private ItemType itemType;

    @OneToOne(cascade = CascadeType.ALL)
    @Getter
    private ItemEconomy economy;

    public Item() {}

    public Item(String name, String description, ItemType type, int price) {
        this.name = name;
        this.description = description;
        this.itemType = type;
        this.currentPrice = price;
        this.originalPrice = price;

        // only "real" items need an economy
        // rest are static prices
        if (this.itemType == ItemType.ITEM) {
            this.economy = new ItemEconomy();
        }
    }

    public void addHistory(int roundNumber) {
        this.economy.addHistory(roundNumber, 1);
    }

    public void calculateNewPrice(int currentRound) {
        float priceFactor = this.economy.calculateNewPriceFactor(currentRound);
        System.out.println("PriceFactor: " + priceFactor);
        this.currentPrice = (int)Math.ceil(this.originalPrice * priceFactor);
        System.out.println("NewPrice: " + this.currentPrice);
    }

    public void changeEconomyParameters(int roundCount, int stock) {
        this.economy.patchParameters(roundCount, stock);
    }
}
