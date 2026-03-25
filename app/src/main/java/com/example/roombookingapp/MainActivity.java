package com.example.roombookingapp;

import android.content.Intent;
import android.os.Bundle;
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

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RecyclerView roomsRecyclerView;
    private RoomAdapter roomAdapter;
    private List<Room> roomList = new ArrayList<>();

    private TextView guestNameText;
    private MaterialButton bookRoomBtn, viewBookingsBtn, logoutBtn;
    private Room selectedRoom = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserSession session = UserSession.getInstance();
        if (session.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Redirect Admin
        if ("admin".equals(session.getCurrentUser().getRole())) {
            startActivity(new Intent(this, AdminActivity.class));
            finish();
            return;
        }

        // 1. Bind UI Views
        guestNameText = findViewById(R.id.guestNameText);
        roomsRecyclerView = findViewById(R.id.roomsRecyclerView);
        bookRoomBtn = findViewById(R.id.bookRoomBtn);
        viewBookingsBtn = findViewById(R.id.viewBookingsBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        // Personalized Welcome
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getEmail() != null) {
            guestNameText.setText(mAuth.getCurrentUser().getEmail().split("@")[0].toUpperCase());
        }

        // 2. Setup RecyclerView
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Passing 'this' as context is vital for Glide inside the adapter
        roomAdapter = new RoomAdapter(this, roomList, room -> {
            selectedRoom = room;
            Toast.makeText(this, room.getName() + " selected", Toast.LENGTH_SHORT).show();
        });
        roomsRecyclerView.setAdapter(roomAdapter);

        loadRoomsFromFirebase();

        // 3. Listeners
        bookRoomBtn.setOnClickListener(v -> {
            if (selectedRoom != null) {
                Intent intent = new Intent(this, BookRoomActivity.class);
                intent.putExtra("roomId", selectedRoom.getId());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select a suite first", Toast.LENGTH_SHORT).show();
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

    private void loadRoomsFromFirebase() {
        db.collection("rooms").whereEqualTo("status", "available").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        roomList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            // This automatically maps the 'imageUrl' from Firestore to your Room object
                            Room room = doc.toObject(Room.class);
                            roomList.add(room);
                        }
                        roomAdapter.notifyDataSetChanged();
                    }
                });
    }
}