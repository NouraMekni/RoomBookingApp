package com.example.roombookingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roombookingapp.adapters.RoomAdapter;
import com.example.roombookingapp.data.model.Room;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    RecyclerView roomsRecyclerView;
    RoomAdapter roomAdapter;
    List<Room> roomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // 🔥 Get the current user from singleton
        UserSession session = UserSession.getInstance();

        // 🔥 Check if user session exists
        if (session.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // 🔥 Prevent admin from accessing MainActivity
        if ("admin".equals(session.getCurrentUser().getRole())) {
            startActivity(new Intent(this, AdminActivity.class));
            finish();
            return;
        }

        // Setup RecyclerView
        roomsRecyclerView = findViewById(R.id.roomsRecyclerView);
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(this, roomList);
        roomsRecyclerView.setAdapter(roomAdapter);

        // Dummy rooms
        roomList.add(new Room("room1", "Salle A", 20, "Salle pour réunion"));
        roomList.add(new Room("room2", "Salle B", 10, "Petite salle"));
        roomAdapter.notifyDataSetChanged();

        // Buttons
        Button bookRoomBtn = findViewById(R.id.bookRoomBtn);
        Button viewBookingsBtn = findViewById(R.id.viewBookingsBtn);
        Button logoutBtn = findViewById(R.id.logoutBtn);

        bookRoomBtn.setOnClickListener(v -> startActivity(new Intent(this, BookRoomActivity.class)));
        viewBookingsBtn.setOnClickListener(v -> startActivity(new Intent(this, MyBookingsActivity.class)));

        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();

            // 🔥 Clear session properly
            session.setCurrentUser(null);

            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}