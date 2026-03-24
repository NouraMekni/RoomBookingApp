package com.example.roombookingapp;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    EditText email, password;
    Button loginBtn;
    TextView goRegister;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        goRegister = findViewById(R.id.goRegister);
        loading = findViewById(R.id.loading);

        // underline "Go Register"
        goRegister.setPaintFlags(goRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Login button click
        loginBtn.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            String userPass = password.getText().toString().trim();

            // Input validation
            if (TextUtils.isEmpty(userEmail)) {
                email.setError("Email required");
                return;
            }

            if (TextUtils.isEmpty(userPass)) {
                password.setError("Password required");
                return;
            }

            loginBtn.setEnabled(false); // prevent multiple clicks
            loading.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(userEmail, userPass)
                    .addOnCompleteListener(task -> {
                        loading.setVisibility(View.GONE);
                        loginBtn.setEnabled(true);

                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Login failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Go to Register screen
        goRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
}