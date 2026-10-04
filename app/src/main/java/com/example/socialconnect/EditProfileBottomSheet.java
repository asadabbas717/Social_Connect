package com.example.socialconnect;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.socialconnect.data.ProfileStore;

public class EditProfileBottomSheet extends BottomSheetDialogFragment {

    private EditText nameInput, bioInput;
    private ImageView profileImage;
    private Button saveBtn;

    private FirebaseFirestore db;
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
                    if (!isAdded() || getView() == null) return;
                    if (snapshot.exists()) {
                        String name = snapshot.getString("name");
                        String bio = snapshot.getString("bio");

                        if (name != null) nameInput.setText(name);
                        if (bio != null) bioInput.setText(bio);
                    }
                }).addOnFailureListener(e -> showSaveError());

        profileImage.setVisibility(View.GONE);

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

        saveBtn.setEnabled(false);
        saveToFirestore(uid, nameTxt, bioTxt, null);

    }

    private void saveToFirestore(String uid, String name, String bio, String imageUrl) {
        ProfileStore.save(uid, name, bio, imageUrl)
                .addOnSuccessListener(unused -> {
                    if (!isAdded()) return;
                    Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> showSaveError());

    }

    private void showSaveError() {
        if (!isAdded() || getView() == null) return;
        saveBtn.setEnabled(true);
        Toast.makeText(getContext(), "Could not save profile. Check your connection and try again.", Toast.LENGTH_LONG).show();
    }


}
