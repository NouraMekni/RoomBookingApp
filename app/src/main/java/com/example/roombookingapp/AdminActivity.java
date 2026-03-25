package com.example.roombookingapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roombookingapp.adapters.RoomAdapter;
import com.example.roombookingapp.data.model.Room;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    EditText roomName, roomCapacity, roomDescription;
    Button addBtn, updateBtn, deleteBtn;
    RecyclerView roomsRecycler;

    RoomAdapter roomAdapter;
    List<Room> roomList;
    Room selectedRoom = null;

    // 1. Initialize Firestore correctly
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Bind views
        roomName = findViewById(R.id.roomName);
        roomCapacity = findViewById(R.id.roomCapacity);
        roomDescription = findViewById(R.id.roomDescription);
        addBtn = findViewById(R.id.addBtn);
        updateBtn = findViewById(R.id.updateBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        roomsRecycler = findViewById(R.id.roomsRecycler);

        // Setup RecyclerView
        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(this, roomList, room -> {
            selectedRoom = room;
            roomName.setText(room.getName());
            roomCapacity.setText(String.valueOf(room.getCapacity()));
            roomDescription.setText(room.getDescription());
        });
        roomsRecycler.setLayoutManager(new LinearLayoutManager(this));
        roomsRecycler.setAdapter(roomAdapter);

        // 2. Load existing rooms from Firebase immediately
        loadRoomsFromFirebase();

        // --- ADD ROOM ---
        addBtn.setOnClickListener(v -> {
            String name = roomName.getText().toString().trim();
            String capStr = roomCapacity.getText().toString().trim();
            String desc = roomDescription.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(capStr)) {
                Toast.makeText(this, "Name and Capacity required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new document with an auto-generated ID
            String roomId = db.collection("rooms").document().getId();
            Room newRoom = new Room(roomId, name, Integer.parseInt(capStr), desc);

            db.collection("rooms").document(roomId).set(newRoom)
                    .addOnSuccessListener(aVoid -> {
                        roomList.add(newRoom);
                        roomAdapter.notifyDataSetChanged();
                        clearInputs();
                        Toast.makeText(this, "Room added to Firebase ✅", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        // --- UPDATE ROOM ---
        updateBtn.setOnClickListener(v -> {
            if (selectedRoom == null) {
                Toast.makeText(this, "Select a room from the list first", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = roomName.getText().toString().trim();
            String capStr = roomCapacity.getText().toString().trim();
            String desc = roomDescription.getText().toString().trim();

            Room updatedRoom = new Room(selectedRoom.getId(), name, Integer.parseInt(capStr), desc);

            // Update the document in Firestore using its ID
            db.collection("rooms").document(selectedRoom.getId()).set(updatedRoom)
                    .addOnSuccessListener(aVoid -> {
                        loadRoomsFromFirebase(); // Refresh the list
                        clearInputs();
                        selectedRoom = null;
                        Toast.makeText(this, "Room updated in Firebase 🔄", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
        });

        // --- DELETE ROOM ---
        deleteBtn.setOnClickListener(v -> {
            if (selectedRoom == null) {
                Toast.makeText(this, "Select a room first", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("rooms").document(selectedRoom.getId()).delete()
                    .addOnSuccessListener(aVoid -> {
                        roomList.remove(selectedRoom);
                        roomAdapter.notifyDataSetChanged();
                        clearInputs();
                        selectedRoom = null;
                        Toast.makeText(this, "Room deleted from Firebase 🗑️", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show());
        });

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadRoomsFromFirebase() {
        db.collection("rooms").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                roomList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Room room = document.toObject(Room.class);
                    roomList.add(room);
                }
                roomAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Failed to load rooms", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearInputs() {
        roomName.setText("");
        roomCapacity.setText("");
        roomDescription.setText("");
    }
}