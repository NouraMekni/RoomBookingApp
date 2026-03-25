package com.example.roombookingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roombookingapp.adapters.RoomAdapter;
import com.example.roombookingapp.data.model.Room;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    // 1. Initialize Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView roomsRecyclerView;
    RoomAdapter roomAdapter;
    List<Room> roomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        UserSession session = UserSession.getInstance();

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

        // Setup RecyclerView
        roomsRecyclerView = findViewById(R.id.roomsRecyclerView);
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomList = new ArrayList<>();

        // Pass a listener if your RoomAdapter needs it for clicking a room
        roomAdapter = new RoomAdapter(this, roomList);
        roomsRecyclerView.setAdapter(roomAdapter);

        // 2. 🔥 REPLACE Dummy rooms with Firestore data
        loadRoomsFromFirebase();

        // Buttons
        Button bookRoomBtn = findViewById(R.id.bookRoomBtn);
        Button viewBookingsBtn = findViewById(R.id.viewBookingsBtn);
        Button logoutBtn = findViewById(R.id.logoutBtn);

        bookRoomBtn.setOnClickListener(v -> startActivity(new Intent(this, BookRoomActivity.class)));
        viewBookingsBtn.setOnClickListener(v -> startActivity(new Intent(this, MyBookingsActivity.class)));

        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            session.setCurrentUser(null);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // 3. New Method to fetch rooms
    private void loadRoomsFromFirebase() {
        db.collection("rooms")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        roomList.clear(); // Clear local list first
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Firebase automatically maps the document to your Room class
                            Room room = document.toObject(Room.class);
                            roomList.add(room);
                        }
                        roomAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error fetching rooms: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}