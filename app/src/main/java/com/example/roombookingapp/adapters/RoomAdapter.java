package com.example.roombookingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roombookingapp.R;
import com.example.roombookingapp.ReservationActivity;
import com.example.roombookingapp.data.model.Room;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private List<Room> roomList;
    private Context context;
    private OnRoomClickListener listener; // listener for admin clicks

    // Interface for click callback
    public interface OnRoomClickListener {
        void onClick(Room room);
    }

    // Updated constructor for admin support
    public RoomAdapter(Context context, List<Room> roomList, OnRoomClickListener listener) {
        this.context = context;
        this.roomList = roomList;
        this.listener = listener;
    }

    // Keep old constructor for regular user
    public RoomAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
        this.listener = null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, capacity;
        Button btnReserve;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            capacity = itemView.findViewById(R.id.capacity);
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

        // Admin click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(room);
            }
        });

        // Reserve button for regular users
        holder.btnReserve.setOnClickListener(v -> {
            if (listener == null) { // only for regular user
                Intent intent = new Intent(context, ReservationActivity.class);
                intent.putExtra("roomId", room.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }
}