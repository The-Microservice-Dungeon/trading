package com.example.trading.core.exceptions;

public class PlayerDoesNotExistException extends RuntimeException {
    public PlayerDoesNotExistException(String player) {
        super("Player '" + player + "' does not exist.");
    }
}
