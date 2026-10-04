package com.example.socialconnect;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class CreatePostActivity extends AppCompatActivity {

    EditText postContent;
    ImageView postImage;
    Button pickImageBtn, postBtn;

    FirebaseAuth mAuth;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        postContent = findViewById(R.id.postContent);
        postImage = findViewById(R.id.postImage);
        pickImageBtn = findViewById(R.id.pickImageBtn);
        postBtn = findViewById(R.id.postBtn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        postImage.setVisibility(android.view.View.GONE);
        pickImageBtn.setVisibility(android.view.View.GONE);

        postBtn.setOnClickListener(v -> {
            String content = postContent.getText().toString().trim();
            if (mAuth.getCurrentUser() == null) { finish(); return; }
            String uid = mAuth.getCurrentUser().getUid();

            if (content.isEmpty()) {
                Toast.makeText(this, "Write something to share", Toast.LENGTH_SHORT).show();
                return;
            }

            postBtn.setEnabled(false);
            savePost(content, null);
        });
    }

    private void savePost(String text, String imageUrl) {
        if (mAuth.getCurrentUser() == null) { finish(); return; }
        String uid = mAuth.getCurrentUser().getUid();
        HashMap<String, Object> post = new HashMap<>();
        post.put("uid", uid);
        post.put("text", text);
        post.put("imageUrl", imageUrl != null ? imageUrl : "");
        post.put("timestamp", FieldValue.serverTimestamp());
        post.put("likes", new HashMap<String, Boolean>());


        db.collection("posts").add(post)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Post created", Toast.LENGTH_SHORT).show();
                    finish(); // go back to Home
                })
                .addOnFailureListener(e -> showPostError());
    }
    private void showPostError() {
        postBtn.setEnabled(true);
        Toast.makeText(this, "Could not publish post. Check your connection and try again.", Toast.LENGTH_LONG).show();
    }
}
