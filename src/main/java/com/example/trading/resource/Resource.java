package com.example.trading.resource;

import com.example.trading.economy.ResourceEconomy;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
public class Resource {
    @Id
    @Getter
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID resourceId;

    @Getter
    @Column(unique = true)
    private String name;

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

    @OneToOne(cascade = CascadeType.ALL)
    private ResourceEconomy economy;

    public Resource() {}

    public Resource(String name, int price) {
        this.name = name.toLowerCase();
        this.currentPrice = price;
        this.originalPrice = price;
        this.economy = new ResourceEconomy();
        this.priceHistory = new HashMap<>();
        this.priceHistory.put(0, this.originalPrice);
    }

    public void addHistory(int roundNumber, int amount) {
        this.economy.addHistory(roundNumber, amount);
    }

    public Map<Integer, Integer> getSellHistory() {
        return this.economy.getHistory();
    }

    public void calculateNewPrice(int currentRound) {
        float priceFactor = this.economy.calculateNewPriceFactor(currentRound);
        this.currentPrice = (int)Math.ceil(this.originalPrice * priceFactor);

        this.priceHistory.put(currentRound, this.currentPrice);
//        System.out.println("PriceFactor: " + priceFactor);
//        System.out.println("NewPrice: " + this.currentPrice);
    }
}
