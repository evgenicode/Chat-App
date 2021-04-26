package com.packagename.myapp;

public class ChatMessage {
    private String from;
    private String message;

    public ChatMessage(String from, String message) {
        this.from = from;
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }
}
