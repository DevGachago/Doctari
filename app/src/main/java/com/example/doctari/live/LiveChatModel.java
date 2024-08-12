package com.example.doctari.live;


import java.util.HashMap;
import java.util.Map;

public class LiveChatModel {
    public String host;
    public String participant;
    public String createdAt;

    public LiveChatModel() {
        // Default constructor required for calls to DataSnapshot.getValue(VideoChatroom.class)
    }

    public LiveChatModel(String host, String participant, String createdAt) {
        this.host = host;
        this.participant = participant;
        this.createdAt = createdAt;
    }

    // Getters and setters (optional) if needed
}


