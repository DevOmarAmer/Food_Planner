package com.example.foodplanner.data.exception;

public class NoNetworkException extends Exception {

    public NoNetworkException() {
        super("No internet connection available");
    }

    public NoNetworkException(String message) {
        super(message);
    }
}
