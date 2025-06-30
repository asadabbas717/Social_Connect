package com.example.socialconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

public class CreatePostActivity extends AppCompatActivity {

    EditText postContent;
    ImageView postImage;
    Button pickImageBtn, postBtn;
    Uri imageUri;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseStorage storage;

    ActivityResultLauncher<Intent> imagePickerLauncher;

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
        storage = FirebaseStorage.getInstance();

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        postImage.setImageURI(imageUri);
                    }
                });

        pickImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        postBtn.setOnClickListener(v -> {
            String content = postContent.getText().toString().trim();
            String uid = mAuth.getCurrentUser().getUid();

            if (content.isEmpty() && imageUri == null) {
                Toast.makeText(this, "Write something or pick an image", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri != null) {
                String fileName = "posts/" + uid + "_" + System.currentTimeMillis() + ".jpg";
                storage.getReference().child(fileName).putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot ->
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                                    savePost(content, uri.toString());
                                }))
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show());
            } else {
                savePost(content, null);
            }
        });
    }

    private void savePost(String text, String imageUrl) {
        String uid = mAuth.getCurrentUser().getUid();
        HashMap<String, Object> post = new HashMap<>();
        post.put("uid", uid);
        post.put("text", text);
        post.put("imageUrl", imageUrl != null ? imageUrl : "");
        post.put("timestamp", FieldValue.serverTimestamp());


        db.collection("posts").add(post)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Post created", Toast.LENGTH_SHORT).show();
                    finish(); // go back to Home
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Post failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
