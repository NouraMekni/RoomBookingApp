package com.example.roombookingapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roombookingapp.adapters.RoomAdapter;
import com.example.roombookingapp.data.model.Room;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    // 🔹 Updated to match your new XML (TextInputEditText and MaterialButton)
    private TextInputEditText roomName, roomCapacity, roomDescription;
    private MaterialButton addBtn, updateBtn, deleteBtn, logoutBtn;
    private RecyclerView roomsRecycler;
    private TextView roomCountBadge;
    private View emptyState, progressOverlay;

    private RoomAdapter roomAdapter;
    private List<Room> roomList;
    private Room selectedRoom = null;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // BIND VIEWS (Must match your new XML IDs)
        roomName = findViewById(R.id.roomName);
        roomCapacity = findViewById(R.id.roomCapacity);
        roomDescription = findViewById(R.id.roomDescription);
        addBtn = findViewById(R.id.addBtn);
        updateBtn = findViewById(R.id.updateBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        roomsRecycler = findViewById(R.id.roomsRecycler);
        roomCountBadge = findViewById(R.id.roomCountBadge);
        emptyState = findViewById(R.id.emptyState);
        progressOverlay = findViewById(R.id.progressOverlay);

        // SETUP RECYCLER
        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(this, roomList, room -> {
            selectedRoom = room;
            roomName.setText(room.getName());
            roomCapacity.setText(String.valueOf(room.getCapacity()));
            roomDescription.setText(room.getDescription());
            Toast.makeText(this, room.getName() + " selected", Toast.LENGTH_SHORT).show();
        });
        roomsRecycler.setLayoutManager(new LinearLayoutManager(this));
        roomsRecycler.setAdapter(roomAdapter);

        loadRoomsFromFirebase();

        // LOGOUT (EXIT)
        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            UserSession.getInstance().clear();
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // 🔹 4. ADD ROOM
        addBtn.setOnClickListener(v -> {
            String name = roomName.getText().toString().trim();
            String capStr = roomCapacity.getText().toString().trim();
            String desc = roomDescription.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(capStr)) {
                roomName.setError("Required");
                return;
            }

            showLoading(true);
            String roomId = db.collection("rooms").document().getId();
            Room newRoom = new Room(roomId, name, Integer.parseInt(capStr), desc);

            db.collection("rooms").document(roomId).set(newRoom)
                    .addOnSuccessListener(aVoid -> {
                        showLoading(false);
                        loadRoomsFromFirebase();
                        clearInputs();
                        Toast.makeText(this, "Luxury Suite Added! ✨", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> showLoading(false));
        });

        // 🔹 5. UPDATE ROOM
        updateBtn.setOnClickListener(v -> {
            if (selectedRoom == null) {
                Toast.makeText(this, "Please select a suite first", Toast.LENGTH_SHORT).show();
                return;
            }
            showLoading(true);
            Room updatedRoom = new Room(selectedRoom.getId(),
                    roomName.getText().toString().trim(),
                    Integer.parseInt(roomCapacity.getText().toString().trim()),
                    roomDescription.getText().toString().trim());

            updatedRoom.setStatus(selectedRoom.getStatus());
            updatedRoom.setBookedBy(selectedRoom.getBookedBy());

            db.collection("rooms").document(selectedRoom.getId()).set(updatedRoom)
                    .addOnSuccessListener(aVoid -> {
                        showLoading(false);
                        loadRoomsFromFirebase();
                        clearInputs();
                        Toast.makeText(this, "Suite Refined 🔄", Toast.LENGTH_SHORT).show();
                    });
        });

        // 🔹 6. DELETE ROOM
        deleteBtn.setOnClickListener(v -> {
            if (selectedRoom == null) return;
            showLoading(true);
            db.collection("rooms").document(selectedRoom.getId()).delete()
                    .addOnSuccessListener(aVoid -> {
                        showLoading(false);
                        loadRoomsFromFirebase();
                        clearInputs();
                        Toast.makeText(this, "Suite Removed 🗑️", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void loadRoomsFromFirebase() {
        db.collection("rooms").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                roomList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    roomList.add(document.toObject(Room.class));
                }
                roomAdapter.notifyDataSetChanged();

                // Update UI Badges
                roomCountBadge.setText(String.valueOf(roomList.size()));
                emptyState.setVisibility(roomList.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void showLoading(boolean show) {
        progressOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        progressOverlay.animate().alpha(show ? 1f : 0f).setDuration(300);
    }

    private void clearInputs() {
        roomName.setText("");
        roomCapacity.setText("");
        roomDescription.setText("");
        selectedRoom = null;
    }
}