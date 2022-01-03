package com.example.trading.core.exceptions;

public class ResourceDoesNotExistException extends RuntimeException {
    public ResourceDoesNotExistException(String name) {
        super("The resource '" + name + "' does not exist.");
    }
}
