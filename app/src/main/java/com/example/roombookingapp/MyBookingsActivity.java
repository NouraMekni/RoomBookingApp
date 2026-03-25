package com.example.roombookingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roombookingapp.adapters.RoomAdapter;
import com.example.roombookingapp.data.model.Room;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class MyBookingsActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private RoomAdapter adapter;
    private List<Room> myReservedRooms;
    private TextView emptyState; // 🔹 Move here for easier access

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);


        recyclerView = findViewById(R.id.myBookingsRecyclerView);
        emptyState = findViewById(R.id.emptyStateText);
        Button backBtn = findViewById(R.id.backBtn);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        myReservedRooms = new ArrayList<>();


        adapter = new RoomAdapter(this, myReservedRooms, room -> {
            cancelBooking(room);
        });
        recyclerView.setAdapter(adapter);


        backBtn.setOnClickListener(v -> finish());


        loadMyReservations();
    }

    private void cancelBooking(Room room) {
        db.collection("rooms").document(room.getId())
                .update(
                        "status", "available",
                        "bookedBy", ""
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Réservation annulée ! 🗑️", Toast.LENGTH_SHORT).show();
                    loadMyReservations();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur lors de l'annulation", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadMyReservations() {
        // 1. Get the current user from Firebase Auth
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();

        // Safety check
        if (user == null || user.getEmail() == null) {
            Toast.makeText(this, "Session expirée", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserEmail = user.getEmail(); // 🔹 Use Email instead of UID

        // 2. Query Firestore for rooms where bookedBy matches this email
        db.collection("rooms")
                .whereEqualTo("bookedBy", currentUserEmail) // 🔹 Fixed query
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        myReservedRooms.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            myReservedRooms.add(document.toObject(Room.class));
                        }
                        adapter.notifyDataSetChanged();

                        // Show/Hide empty state message
                        if (myReservedRooms.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                        } else {
                            emptyState.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(this, "Erreur: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}