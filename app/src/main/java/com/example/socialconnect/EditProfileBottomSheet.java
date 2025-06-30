package com.example.socialconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.github.dhaval2404.imagepicker.ImagePicker;
import java.util.HashMap;

public class EditProfileBottomSheet extends BottomSheetDialogFragment {

    private EditText nameInput, bioInput;
    private ImageView profileImage;
    private Button saveBtn;
    private Uri imageUri;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameInput = view.findViewById(R.id.nameInput);
        bioInput = view.findViewById(R.id.bioInput);
        profileImage = view.findViewById(R.id.profileImage);
        saveBtn = view.findViewById(R.id.saveBtn);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String name = snapshot.getString("name");
                        String bio = snapshot.getString("bio");
                        String imageUrl = snapshot.getString("imageUrl");

                        if (name != null) nameInput.setText(name);
                        if (bio != null) bioInput.setText(bio);
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(requireContext()).load(imageUrl).into(profileImage);
                        }
                    }
                });

        profileImage.setOnClickListener(v -> ImagePicker.with(this)
                .crop()
                .compress(512)
                .maxResultSize(512, 512)
                .start());

        saveBtn.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String nameTxt = nameInput.getText().toString().trim();
        String bioTxt = bioInput.getText().toString().trim();

        if (user == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();

        if (imageUri != null) {
            String filePath = "profiles/" + uid + ".jpg";
            storage.getReference().child(filePath).putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot ->
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                                saveToFirestore(uid, nameTxt, bioTxt, uri.toString());
                            }))
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show());
        } else {
            saveToFirestore(uid, nameTxt, bioTxt, "");
        }

    }

    private void saveToFirestore(String uid, String name, String bio, String imageUrl) {
        HashMap<String, Object> profile = new HashMap<>();
        profile.put("name", name);
        profile.put("bio", bio);
        profile.put("imageUrl", imageUrl);
        profile.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());

        db.collection("users").document(uid).set(profile)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                    dismiss();
                    FirebaseMessaging.getInstance().getToken()
                            .addOnSuccessListener(token -> {
                                FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .document(uid)
                                        .update("fcmToken", token);
                            });

                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to update", Toast.LENGTH_SHORT).show());


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }
}
