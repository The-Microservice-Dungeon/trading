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

    @OneToOne(cascade = CascadeType.ALL)
    private ItemEconomy economy;

    public Item() {}

    public Item(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.currentPrice = price;
        this.originalPrice = price;
        this.economy = new ItemEconomy();
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
}
