package com.example.roombookingapp.data;

import com.example.roombookingapp.data.model.LoggedInUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginDataSource {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void login(String email, String password, LoginCallback callback) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        String uid = mAuth.getCurrentUser().getUid();

                        // Get user role from Firestore
                        db.collection("users").document(uid).get()
                                .addOnSuccessListener(document -> {

                                    if (document.exists()) {

                                        String name = document.getString("displayName");
                                        String role = document.getString("role");

                                        LoggedInUser user = new LoggedInUser(
                                                uid,
                                                name,
                                                role
                                        );

                                        callback.onSuccess(user);

                                    } else {
                                        callback.onError(new Exception("User data not found"));
                                    }
                                })
                                .addOnFailureListener(callback::onError);

                    } else {
                        callback.onError(task.getException());
                    }
                });
    }

    public void logout() {
        mAuth.signOut();
    }
}