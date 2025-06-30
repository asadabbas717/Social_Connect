package com.example.socialconnect.models;

import com.google.firebase.Timestamp;

public class Comment {
    private String id;
    private String uid;
    private String text;
    private Timestamp timestamp;

    // Required empty constructor
    public Comment() {}

    public Comment(String id, String uid, String text, Timestamp timestamp) {
        this.id = id;
        this.uid = uid;
        this.text = text;
        this.timestamp = timestamp;
    }

    // Getters (optional but clean)
    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getText() {
        return text;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
