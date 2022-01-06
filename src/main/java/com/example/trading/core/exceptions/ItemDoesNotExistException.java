package com.example.trading.core.exceptions;

public class ItemDoesNotExistException extends RuntimeException {
    public ItemDoesNotExistException(String name) {
        super("The item/upgrade '" + name + "' does not exist.");
    }
}
