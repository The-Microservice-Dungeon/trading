package com.example.trading.game;

public class RoundDto {
    public int roundNumber;
    public String roundStatus;

    public RoundDto(int number, String status) {
        this.roundNumber = number;
        this.roundStatus = status;
    }
}
