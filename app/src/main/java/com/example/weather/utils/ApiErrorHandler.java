package com.example.weather.utils;

import android.content.Context;

import com.example.weather.R;
import com.example.weather.utils.exceptions.HttpException;
import com.example.weather.utils.exceptions.MalformedDataException;
import com.example.weather.utils.exceptions.NetworkException;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

public class ApiErrorHandler {

    /**
     * Handles the exception and returns the appropriate custom exception.
     *
     * @param e The exception to handle.
     * @param context The context to get string resources.
     * @return The appropriate custom exception.
     */
    public static Exception handleException(Exception e, Context context) {
        if (e instanceof HttpException httpException) {
            return new HttpException(httpException.getCode(), context.getString(R.string.http_error));
        } else if (e instanceof IOException) {
            return new NetworkException(context.getString(R.string.network_error));
        } else if (e instanceof JsonSyntaxException) {
            return new MalformedDataException(context.getString(R.string.parsing_error));
        } else {
            return new Exception(context.getString(R.string.unknown_error));
        }
    }
}
