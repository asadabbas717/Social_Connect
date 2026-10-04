package com.example.socialconnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialconnect.EditProfileBottomSheet;
import com.example.socialconnect.R;
import com.example.socialconnect.adapters.PostAdapter;
import com.example.socialconnect.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView nameText, bioText;
    private ImageView profileImage;
    private Button editProfileBtn;
    private FirebaseFirestore db;


    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameText = view.findViewById(R.id.nameText);
        bioText = view.findViewById(R.id.bioText);
        profileImage = view.findViewById(R.id.profileImage);
        editProfileBtn = view.findViewById(R.id.editProfileBtn);

        db = FirebaseFirestore.getInstance();

        loadUserProfile();

        editProfileBtn.setOnClickListener(v -> {
            EditProfileBottomSheet sheet = new EditProfileBottomSheet();
            sheet.show(getChildFragmentManager(), "EditProfile");
        });

    }

    private void loadUserProfile() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!isAdded() || getView() == null) return;
                    if (snapshot.exists()) {
                        String name = snapshot.getString("name");
                        String bio = snapshot.getString("bio");

                        nameText.setText(name != null ? name : "No Name");
                        bioText.setText(bio != null ? bio : "No Bio");

                    }
                })
                .addOnFailureListener(e -> {
                    if (!isAdded() || getView() == null) return;
                    nameText.setText("Failed to load");
                    bioText.setText("");
                });
    }


}
