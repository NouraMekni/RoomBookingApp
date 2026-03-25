package com.example.roombookingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roombookingapp.adapters.RoomAdapter;
import com.example.roombookingapp.data.model.Room;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RecyclerView roomsRecyclerView;
    private RoomAdapter roomAdapter;
    private List<Room> roomList;
    private TextView guestNameText;
    private MaterialButton bookRoomBtn, viewBookingsBtn, logoutBtn;

    // Track which room the user clicked
    private Room selectedRoom = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        UserSession session = UserSession.getInstance();

        //  Session & Role Security
        if (session.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if ("admin".equals(session.getCurrentUser().getRole())) {
            startActivity(new Intent(this, AdminActivity.class));
            finish();
            return;
        }

        // Bind Luxury UI Views
        guestNameText = findViewById(R.id.guestNameText);
        roomsRecyclerView = findViewById(R.id.roomsRecyclerView);
        bookRoomBtn = findViewById(R.id.bookRoomBtn);
        viewBookingsBtn = findViewById(R.id.viewBookingsBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        // Set Guest Name (Personalized Welcome)
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getEmail() != null) {
            String email = mAuth.getCurrentUser().getEmail();
            guestNameText.setText(email.split("@")[0].toUpperCase());
        }

        // Setup RecyclerView
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomList = new ArrayList<>();

        // Listener handles selection for the bottom button
        roomAdapter = new RoomAdapter(this, roomList, room -> {
            selectedRoom = room;
            // Visual feedback
            Toast.makeText(this, room.getName() + " selected for booking", Toast.LENGTH_SHORT).show();
            bookRoomBtn.setStrokeWidth(4); // Optional: highlight button when ready
        });
        roomsRecyclerView.setAdapter(roomAdapter);

        loadRoomsFromFirebase();

        // Premium Button Logic
        bookRoomBtn.setOnClickListener(v -> {
            if (selectedRoom != null) {
                Intent intent = new Intent(this, BookRoomActivity.class);
                intent.putExtra("roomId", selectedRoom.getId());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select a suite from the list", Toast.LENGTH_SHORT).show();
            }
        });

        viewBookingsBtn.setOnClickListener(v -> startActivity(new Intent(this, MyBookingsActivity.class)));

        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            session.setCurrentUser(null);
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRoomsFromFirebase();
        selectedRoom = null;
    }

    private void loadRoomsFromFirebase() {
        // Fetch only AVAILABLE rooms for the main list
        db.collection("rooms")
                .whereEqualTo("status", "available")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        roomList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            roomList.add(document.toObject(Room.class));
                        }
                        roomAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error loading suites", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}