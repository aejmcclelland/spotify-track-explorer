package com.amcclelland.ste_server.domain;

public record Message(String value) {
    public Message {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("message cannot be blank");
    }
}
