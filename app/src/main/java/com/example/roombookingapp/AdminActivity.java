package com.example.roombookingapp;
import com.example.roombookingapp.R;
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

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    EditText roomName, roomCapacity, roomDescription;
    Button addBtn, updateBtn, deleteBtn;
    RecyclerView roomsRecycler;

    RoomAdapter roomAdapter;
    List<Room> roomList;
    Room selectedRoom = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
            // On room clicked -> select it
            selectedRoom = room;
            roomName.setText(room.getName());
            roomCapacity.setText(String.valueOf(room.getCapacity()));
            roomDescription.setText(room.getDescription());
        });
        roomsRecycler.setLayoutManager(new LinearLayoutManager(this));
        roomsRecycler.setAdapter(roomAdapter);

        // Add room
        addBtn.setOnClickListener(v -> {
            String name = roomName.getText().toString().trim();
            String capacityStr = roomCapacity.getText().toString().trim();
            String description = roomDescription.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(capacityStr)) {
                Toast.makeText(this, "Name and Capacity required", Toast.LENGTH_SHORT).show();
                return;
            }

            int capacity = Integer.parseInt(capacityStr);
            Room newRoom = new Room("room" + (roomList.size() + 1), name, capacity, description);
            roomList.add(newRoom);
            roomAdapter.notifyDataSetChanged();
            clearInputs();
            Toast.makeText(this, "Room added", Toast.LENGTH_SHORT).show();
        });

        // Update room
        updateBtn.setOnClickListener(v -> {
            if (selectedRoom == null) {
                Toast.makeText(this, "Select a room first", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = roomName.getText().toString().trim();
            String capacityStr = roomCapacity.getText().toString().trim();
            String description = roomDescription.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(capacityStr)) {
                Toast.makeText(this, "Name and Capacity required", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedRoom = new Room(selectedRoom.getId(), name, Integer.parseInt(capacityStr), description);
            int index = roomList.indexOf(selectedRoom);
            if (index != -1) {
                roomList.set(index, selectedRoom);
                roomAdapter.notifyDataSetChanged();
                clearInputs();
                selectedRoom = null;
                Toast.makeText(this, "Room updated", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete room
        deleteBtn.setOnClickListener(v -> {
            if (selectedRoom == null) {
                Toast.makeText(this, "Select a room first", Toast.LENGTH_SHORT).show();
                return;
            }
            roomList.remove(selectedRoom);
            roomAdapter.notifyDataSetChanged();
            clearInputs();
            selectedRoom = null;
            Toast.makeText(this, "Room deleted", Toast.LENGTH_SHORT).show();
        });
    }

    private void clearInputs() {
        roomName.setText("");
        roomCapacity.setText("");
        roomDescription.setText("");
    }
}