package com.example.trading.item;

import com.example.trading.economy.ItemEconomy;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
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
    @Column(unique = true)
    private String name;

    @Getter
    private String description;

    @Getter
    @Setter
    private int currentPrice;

    @Getter
    private int originalPrice;

    @ElementCollection
    @MapKeyColumn(name="roundNumber")
    @Column(name="price")
    @Getter
    private Map<Integer, Integer> priceHistory;

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
            this.priceHistory = new HashMap<>();
            this.priceHistory.put(0, this.originalPrice);
        }
    }

    public void addHistory(int roundNumber) {
        this.economy.addHistory(roundNumber, 1);
    }

    public Map<Integer, Integer> getBuyHistory() {
        return this.economy.getHistory();
    }

    public void calculateNewPrice(int currentRound) {
        float priceFactor = this.economy.calculateNewPriceFactor(currentRound);
        int roundAdjust = (int)Math.floor(200 * (1 / (1 + Math.exp(-0.014 * currentRound) * 199)));
        this.currentPrice = (int)Math.ceil(this.originalPrice * priceFactor * roundAdjust);

        this.priceHistory.put(currentRound, this.currentPrice);
//        System.out.println("PriceFactor: " + priceFactor);
//        System.out.println("RoundAdjust: " + roundAdjust);
//        System.out.println("NewPrice: " + this.currentPrice);
    }
}
