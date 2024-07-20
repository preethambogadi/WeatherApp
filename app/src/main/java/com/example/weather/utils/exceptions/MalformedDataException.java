package com.example.weather.utils.exceptions;

/**
 * Exception thrown when the received data is malformed.
 */
public class MalformedDataException extends Exception {
    public MalformedDataException(String message) {
        super(message);
    }
}
