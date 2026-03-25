package com.example.roombookingapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roombookingapp.data.model.Room;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class BookRoomActivity extends AppCompatActivity {

    private TextView roomDetailsText;
    private Button confirmBtn, cancelBtn;
    private String roomId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_room);

        db = FirebaseFirestore.getInstance();

        // 1. Get Room ID passed from the Adapter
        roomId = getIntent().getStringExtra("roomId");

        // Bind Views
        roomDetailsText = findViewById(R.id.roomDetailsText);
        confirmBtn = findViewById(R.id.confirmBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        // Optional: Fetch room details to show the name on screen
        if (roomId != null) {
            fetchRoomDetails();
        }

        // 2. Confirm Booking Logic
        confirmBtn.setOnClickListener(v -> {
            bookRoomInFirestore();
        });

        // 3. Cancel Logic
        cancelBtn.setOnClickListener(v -> finish());
    }

    private void fetchRoomDetails() {
        db.collection("rooms").document(roomId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Room room = documentSnapshot.toObject(Room.class);
                    if (room != null) {
                        roomDetailsText.setText("Voulez-vous réserver la " + room.getName() + " ?");
                    }
                });
    }

    private void bookRoomInFirestore() {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (userEmail == null || userEmail.isEmpty()) {
            userEmail = "Unknown User";
        }

        db.collection("rooms").document(roomId)
                .update(
                        "status", "reserved",
                        "bookedBy", userEmail
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Réservation réussie ! ✅", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}