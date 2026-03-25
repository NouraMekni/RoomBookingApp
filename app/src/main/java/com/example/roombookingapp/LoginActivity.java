package com.example.roombookingapp;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roombookingapp.data.LoginCallback;
import com.example.roombookingapp.data.LoginDataSource;
import com.example.roombookingapp.data.model.LoggedInUser;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button loginBtn;
    private TextView goRegister;
    private ProgressBar loading;

    private LoginDataSource loginDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        loginDataSource = new LoginDataSource();

        // underline "Go Register"
        goRegister.setPaintFlags(goRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        goRegister.setOnClickListener(v -> navigateToRegister());
        loginBtn.setOnClickListener(v -> attemptLogin());
    }

    private void initViews() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        goRegister = findViewById(R.id.goRegister);
        loading = findViewById(R.id.loading);
    }

    private void navigateToRegister() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    private void attemptLogin() {
        String userEmail = email.getText().toString().trim();
        String userPass = password.getText().toString().trim();

        if (!validateInputs(userEmail, userPass)) return;

        setLoadingState(true);

        // Use DataSource with callback
        loginDataSource.login(userEmail, userPass, new LoginCallback() {
            @Override
            public void onSuccess(LoggedInUser user) {
                setLoadingState(false);

                // Save user in session
                UserSession.getInstance().setCurrentUser(user);

                // Role-based navigation
                if ("admin".equalsIgnoreCase(user.getRole())) {
                    startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                } else {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                finish();
            }

            @Override
            public void onError(Exception e) {
                setLoadingState(false);
                Toast.makeText(LoginActivity.this,
                        "Login failed: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            this.email.setError("Email required");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            this.password.setError("Password required");
            return false;
        }
        return true;
    }

    private void setLoadingState(boolean isLoading) {
        loginBtn.setEnabled(!isLoading);
        loading.setVisibility(isLoading ? ProgressBar.VISIBLE : ProgressBar.GONE);
    }
}