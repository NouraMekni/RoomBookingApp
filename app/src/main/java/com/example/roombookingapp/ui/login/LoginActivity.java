package com.example.roombookingapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.roombookingapp.AdminActivity;
import com.example.roombookingapp.MainActivity;
import com.example.roombookingapp.R;
import com.example.roombookingapp.UserSession;
import com.example.roombookingapp.data.model.LoggedInUser;
import com.example.roombookingapp.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        final EditText emailEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.loginBtn;
        final ProgressBar loadingProgressBar = binding.loading;

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            loginWithFirebase(email, password, loadingProgressBar);
        });

        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginButton.performClick();
            }
            return false;
        });
    }

    private void loginWithFirebase(String email, String password, ProgressBar loading) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // 🔹 User signed in, now fetch Firestore document
                        String uid = mAuth.getCurrentUser().getUid();
                        db.collection("users").document(uid).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    loading.setVisibility(View.GONE);

                                    if (documentSnapshot.exists()) {
                                        String displayName = documentSnapshot.getString("displayName");
                                        String role = documentSnapshot.getString("role");

                                        // 🔹 Save to session
                                        LoggedInUser loggedInUser = new LoggedInUser(uid, displayName, email, role);
                                        UserSession.getInstance().setCurrentUser(loggedInUser);

                                        // 🔹 Redirect based on role
                                        if ("admin".equals(role)) {
                                            startActivity(new Intent(this, AdminActivity.class));
                                        } else {
                                            startActivity(new Intent(this, MainActivity.class));
                                        }
                                        finish();

                                        Toast.makeText(this, "Welcome " + displayName, Toast.LENGTH_LONG).show();

                                    } else {
                                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    loading.setVisibility(View.GONE);
                                    Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                });
                    } else {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}