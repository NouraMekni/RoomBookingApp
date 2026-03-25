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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageRoomsActivity extends AppCompatActivity {

    private RecyclerView roomsRecycler;
    private RoomAdapter roomAdapter;
    private List<Room> roomList;
    private TextView roomCountBadge;
    private View emptyState, progressOverlay;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_rooms);

        roomsRecycler = findViewById(R.id.roomsRecycler);
        roomCountBadge = findViewById(R.id.roomCountBadge);
        emptyState = findViewById(R.id.emptyState);
        progressOverlay = findViewById(R.id.progressOverlay);

        roomList = new ArrayList<>();

        // When clicked, ask for Delete or Edit
        roomAdapter = new RoomAdapter(this, roomList, room -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle(room.getName())
                    .setMessage("What do you want to do with this suite?")
                    .setPositiveButton("EDIT", (dialog, which) -> {
                        Intent intent = new Intent(this, AdminActivity.class);
                        intent.putExtra("roomId", room.getId());
                        startActivity(intent);
                    })
                    .setNegativeButton("DELETE", (dialog, which) -> deleteRoom(room.getId()))
                    .setNeutralButton("CANCEL", null)
                    .show();
        });

        roomsRecycler.setLayoutManager(new LinearLayoutManager(this));
        roomsRecycler.setAdapter(roomAdapter);

        loadRooms();
    }

    private void loadRooms() {
        showLoading(true);
        db.collection("rooms").get().addOnCompleteListener(task -> {
            showLoading(false);
            if (task.isSuccessful()) {
                roomList.clear();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    roomList.add(doc.toObject(Room.class));
                }
                roomAdapter.notifyDataSetChanged();
                roomCountBadge.setText(String.valueOf(roomList.size()));
                emptyState.setVisibility(roomList.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void deleteRoom(String id) {
        showLoading(true);
        db.collection("rooms").document(id).delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
            loadRooms();
        });
    }

    private void showLoading(boolean s) { progressOverlay.setVisibility(s ? View.VISIBLE : View.GONE); }

    @Override
    protected void onResume() {
        super.onResume();
        loadRooms();
    }
}