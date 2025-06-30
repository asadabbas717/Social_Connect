package com.example.socialconnect.models;

import com.google.firebase.Timestamp;
public class Post {
    public String id;
    public String text;
    public String imageUrl;
    public String uid;
    public Timestamp timestamp;

    public Post() {}

    public Post(String id, String text, String imageUrl, String uid, Timestamp timestamp) {
        this.id = id;
        this.text = text;
        this.imageUrl = imageUrl;
        this.uid = uid;
        this.timestamp = timestamp;
    }
}