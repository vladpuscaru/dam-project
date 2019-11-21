package com.example.damproject.util;

public class InputError {
    private String message;

    public InputError(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
