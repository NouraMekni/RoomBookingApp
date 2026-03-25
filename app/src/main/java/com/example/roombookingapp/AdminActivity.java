package com.example.roombookingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.roombookingapp.data.model.Room;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    private TextInputEditText roomName, roomCapacity, roomDescription, roomImageUrl;
    private MaterialButton addBtn, updateBtn, viewRoomsBtn, logoutBtn;
    private ImageView roomImagePreview;
    private View progressOverlay;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String roomIdToEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Bind UI Components
        roomName = findViewById(R.id.roomName);
        roomCapacity = findViewById(R.id.roomCapacity);
        roomDescription = findViewById(R.id.roomDescription);
        roomImageUrl = findViewById(R.id.roomImageUrl);
        roomImagePreview = findViewById(R.id.roomImagePreview);

        addBtn = findViewById(R.id.addBtn);
        updateBtn = findViewById(R.id.updateBtn);
        viewRoomsBtn = findViewById(R.id.deleteBtn); // We use the third button to GO TO MANAGE
        viewRoomsBtn.setText("MANAGE ALL");
        viewRoomsBtn.setTextColor(getResources().getColor(android.R.color.white));
        viewRoomsBtn.setBackgroundTintList(null); // Optional: reset colors

        logoutBtn = findViewById(R.id.logoutBtn);
        progressOverlay = findViewById(R.id.progressOverlay);

        // Check if we came here to EDIT an existing room
        roomIdToEdit = getIntent().getStringExtra("roomId");
        if (roomIdToEdit != null) {
            loadRoomData(roomIdToEdit);
            addBtn.setVisibility(View.GONE);
            updateBtn.setVisibility(View.VISIBLE);
        } else {
            addBtn.setVisibility(View.VISIBLE);
            updateBtn.setVisibility(View.GONE);
        }

        // Live Image Preview Logic
        roomImageUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String url = s.toString().trim();
                if (!url.isEmpty()) {
                    Glide.with(AdminActivity.this).load(url).placeholder(R.drawable.ic_image_placeholder).into(roomImagePreview);
                    roomImagePreview.setAlpha(1.0f);
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        addBtn.setOnClickListener(v -> saveRoom(false));
        updateBtn.setOnClickListener(v -> saveRoom(true));

        // NEW: Go to the Management Dashboard
        viewRoomsBtn.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, ManageRoomsActivity.class));
        });

        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            UserSession.getInstance().clear();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void loadRoomData(String id) {
        db.collection("rooms").document(id).get().addOnSuccessListener(doc -> {
            Room r = doc.toObject(Room.class);
            if (r != null) {
                roomName.setText(r.getName());
                roomCapacity.setText(String.valueOf(r.getCapacity()));
                roomDescription.setText(r.getDescription());
                roomImageUrl.setText(r.getImageUrl());
            }
        });
    }

    private void saveRoom(boolean isUpdate) {
        String name = roomName.getText().toString().trim();
        String capStr = roomCapacity.getText().toString().trim();
        String desc = roomDescription.getText().toString().trim();
        String url = roomImageUrl.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(capStr)) {
            Toast.makeText(this, "Complete required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        String id = isUpdate ? roomIdToEdit : db.collection("rooms").document().getId();

        Room room = new Room(id, name, Integer.parseInt(capStr), desc);
        room.setImageUrl(url);

        db.collection("rooms").document(id).set(room).addOnSuccessListener(aVoid -> {
            showLoading(false);
            if (!isUpdate) prepareNotificationEmail(name);
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
            finish(); // Close activity after save
        });
    }

    private void prepareNotificationEmail(String newRoomName) {
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                ArrayList<String> recipients = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String email = doc.getString("email");
                    if (email != null && !email.isEmpty()) recipients.add(email);
                }
                if (!recipients.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mAuth.getCurrentUser().getEmail()});
                    intent.putExtra(Intent.EXTRA_BCC, recipients.toArray(new String[0]));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "✨ New Luxury Suite: " + newRoomName);
                    intent.putExtra(Intent.EXTRA_TEXT, "A new suite \"" + newRoomName + "\" is now available.");
                    startActivity(Intent.createChooser(intent, "Notify Users:"));
                }
            }
        });
    }

    private void showLoading(boolean s) { progressOverlay.setVisibility(s ? View.VISIBLE : View.GONE); }
}