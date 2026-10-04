package com.example.socialconnect;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    EditText signupEmail, signupPassword;
    Button registerBtn;
    TextView loginRedirect;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupEmail = findViewById(R.id.signupEmail);
        signupPassword = findViewById(R.id.signupPassword);
        registerBtn = findViewById(R.id.registerBtn);
        loginRedirect = findViewById(R.id.loginRedirect);
        mAuth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(v -> {
            String email = signupEmail.getText().toString().trim();
            String password = signupPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }

            registerBtn.setEnabled(false);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        registerBtn.setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Account could not be created. Check your email, password and connection.", Toast.LENGTH_LONG).show();
                        }
                    });
        });

        loginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }
}
