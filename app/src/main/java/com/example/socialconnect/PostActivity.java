package com.example.socialconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {

    private EditText postText;
    private ImageView postImage;
    private Button selectImageBtn, postBtn;
    private Uri imageUri;

    FirebaseFirestore db;
    FirebaseStorage storage;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postText = findViewById(R.id.postText);
        postImage = findViewById(R.id.postImage);
        selectImageBtn = findViewById(R.id.selectImageBtn);
        postBtn = findViewById(R.id.postBtn);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        selectImageBtn.setOnClickListener(v -> {
            ImagePicker.with(PostActivity.this)
                    .crop()	    			// Crop image(Optional), Check Customization for more option
                    .compress(1024)		    // Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	// Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });

        postBtn.setOnClickListener(v -> uploadPost());
    }

    private void uploadPost() {
        String text = postText.getText().toString().trim();
        String uid = auth.getCurrentUser().getUid();
        String postId = UUID.randomUUID().toString();

        if (text.isEmpty()) {
            postText.setError("Post text is required");
            return;
        }

        if (imageUri != null) {
            String filePath = "posts/" + postId + ".jpg";
            storage.getReference().child(filePath).putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot ->
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                                savePost(text, uid, uri.toString(), postId);
                            }))
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show());
        } else {
            savePost(text, uid, "", postId);
        }
    }

    private void savePost(String text, String uid, String imageUrl, String postId) {
        HashMap<String, Object> post = new HashMap<>();
        post.put("id", postId);
        post.put("text", text);
        post.put("imageUrl", imageUrl);
        post.put("uid", uid);
        post.put("likes", new HashMap<>()); // empty map for likes
        post.put("timestamp", FieldValue.serverTimestamp());

        db.collection("posts").document(postId).set(post)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Post uploaded", Toast.LENGTH_SHORT).show();
                    finish(); // return to previous screen
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to upload post", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            imageUri = data.getData();
            postImage.setImageURI(imageUri);
        }
    }
}
