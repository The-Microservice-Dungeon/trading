package com.example.trading.core.exceptions;

import com.example.trading.resource.Resource;

public class ResourceDoesNotExistException extends RuntimeException {
    public ResourceDoesNotExistException(String name) {
        super("The resource '" + name + "' does not exist.");
    }
}
