package com.example.doctari.live;

import java.util.HashMap;
import java.util.Map;

public class VideoChatroom {
    public String host;
    public Map<String, Boolean> participants;
    public int participantCount;
    public String createdAt;

    public VideoChatroom() {
        // Default constructor required for calls to DataSnapshot.getValue(VideoChatroom.class)
    }

    public VideoChatroom(String host, String createdAt) {
        this.host = host;
        this.participants = new HashMap<>();
        this.participants.put(host, true);  // Add the host as a participant
        this.participantCount = 1;          // Initialize participant count with the host
        this.createdAt = createdAt;
    }

    // Getters and setters (optional) if needed
}

