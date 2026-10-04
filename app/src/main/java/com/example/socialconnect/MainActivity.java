package com.example.socialconnect;

import android.os.Bundle;
import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.socialconnect.fragments.HomeFragment;
import com.example.socialconnect.fragments.ProfileFragment;
import com.example.socialconnect.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token ->
                FirebaseFirestore.getInstance().collection("users").document(uid)
                        .set(Collections.singletonMap("fcmToken", token), SetOptions.merge()));

        bottomNav = findViewById(R.id.bottomNavigationView);

        if (savedInstanceState == null) getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new HomeFragment())
                .commit();

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();
            if (id == R.id.menu_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.menu_profile) {
                selectedFragment = new ProfileFragment();
            } else if (id == R.id.menu_settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });
    }
}
