package com.example.weather.utils.exceptions;

/**
 * Exception thrown when the specified location is not found.
 */
public class LocationNotFoundException extends Exception {
    public LocationNotFoundException(String message) {
        super(message);
    }
}
