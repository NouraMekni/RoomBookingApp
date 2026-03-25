package com.example.roombookingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roombookingapp.AdminActivity;
import com.example.roombookingapp.BookRoomActivity;
import com.example.roombookingapp.R;
import com.example.roombookingapp.data.model.Room;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private List<Room> roomList;
    private Context context;
    private OnRoomClickListener listener;

    public interface OnRoomClickListener {
        void onClick(Room room);
    }

    public RoomAdapter(Context context, List<Room> roomList, OnRoomClickListener listener) {
        this.context = context;
        this.roomList = roomList;
        this.listener = listener;
    }

    public RoomAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
        this.listener = null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, capacity, status;
        Button btnReserve;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            capacity = itemView.findViewById(R.id.capacity);
            status = itemView.findViewById(R.id.status);
            btnReserve = itemView.findViewById(R.id.btnReserve);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = roomList.get(position);

        holder.name.setText(room.getName());
        holder.capacity.setText("Capacity: " + room.getCapacity());

        boolean isAdmin = (context instanceof AdminActivity);

        String roomStatus = (room.getStatus() != null) ? room.getStatus() : "available";

        if ("reserved".equalsIgnoreCase(roomStatus)) {
            holder.status.setTextColor(Color.WHITE);


            if (isAdmin && "reserved".equalsIgnoreCase(roomStatus)) {
                holder.status.setText("RESERVED BY: " + room.getBookedBy());
            } else {
                holder.status.setText(roomStatus.toUpperCase());
            }

            // Button Logic for Reserved status
            if (isAdmin) {
                holder.btnReserve.setVisibility(View.GONE);
            } else if (listener != null) {
                // This is MyBookingsActivity
                holder.btnReserve.setVisibility(View.VISIBLE);
                holder.btnReserve.setText("Annuler");
                holder.btnReserve.setEnabled(true);
                holder.btnReserve.setBackgroundColor(Color.parseColor("#E91E63"));
            } else {
                // This is MainActivity
                holder.btnReserve.setVisibility(View.VISIBLE);
                holder.btnReserve.setText("Occupé");
                holder.btnReserve.setEnabled(false);
                holder.btnReserve.setBackgroundColor(Color.GRAY);
            }
        }
        else {
            // Available Status
            holder.status.setTextColor(Color.parseColor("#FFFFFF"));
            holder.status.setText("Available");

            if (isAdmin) {
                holder.btnReserve.setVisibility(View.GONE);
            } else {
                holder.btnReserve.setVisibility(View.VISIBLE);
                holder.btnReserve.setText("Réserver");
                holder.btnReserve.setEnabled(true);
                holder.btnReserve.setBackgroundColor(Color.parseColor("#6200EE"));
            }
        }

        // Entire item click (Used by Admin to select room)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(room);
        });

        // Button click logic (User only)
        holder.btnReserve.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(room); // Triggers cancelBooking in MyBookings
            } else {
                Intent intent = new Intent(context, BookRoomActivity.class);
                intent.putExtra("roomId", room.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() { return roomList.size(); }
}