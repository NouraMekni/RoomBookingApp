package com.example.roombookingapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ReservationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        // Récupérer l'ID de la salle passée depuis l'adapter
        String roomId = getIntent().getStringExtra("roomId");

        // Pour test, tu peux afficher dans le log
        // Log.d("ReservationActivity", "Room ID: " + roomId);
    }
}