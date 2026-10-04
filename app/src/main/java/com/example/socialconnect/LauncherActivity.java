package com.example.socialconnect;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
public class LauncherActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        Class<?> destination = FirebaseAuth.getInstance().getCurrentUser() == null
                ? LoginActivity.class : MainActivity.class;
        startActivity(new Intent(this, destination));
        finish();
    }
}
