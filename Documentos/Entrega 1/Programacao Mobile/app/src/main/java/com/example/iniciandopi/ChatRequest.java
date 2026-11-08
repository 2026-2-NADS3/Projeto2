package com.example.iniciandopi;

public class ChatRequest {
    private String message;
    private String audience;

    public ChatRequest(String message, String audience) {
        this.message = message;
        this.audience = audience;
    }

    public String getMessage() { return message; }
    public String getAudience() { return audience; }
}
