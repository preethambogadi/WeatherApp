package com.example.weather.utils.exceptions;

/**
 * Exception thrown when a network error occurs.
 */
public class NetworkException extends Exception {
    public NetworkException(String message) {
        super(message);
    }
}

