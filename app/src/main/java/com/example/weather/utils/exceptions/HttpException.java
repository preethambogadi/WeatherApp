package com.example.weather.utils.exceptions;

/**
 * Exception thrown when an HTTP error occurs.
 */
public class HttpException extends Exception {
    private int code;

    public HttpException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

