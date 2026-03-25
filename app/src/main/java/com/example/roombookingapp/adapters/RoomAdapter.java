package com.example.roombookingapp.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.roombookingapp.R;
import com.example.roombookingapp.data.model.Room;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private Context context;
    private List<Room> roomList;
    private OnRoomClickListener listener;

    public interface OnRoomClickListener {
        void onRoomClick(Room room);
    }

    public RoomAdapter(Context context, List<Room> roomList, OnRoomClickListener listener) {
        this.context = context;
        this.roomList = roomList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);

        // 1. Set basic text
        holder.name.setText(room.getName());
        holder.capacity.setText("Up to " + room.getCapacity() + " Guests");

        // 2. Dynamic UI based on Status (BOOK vs ANNULER)
        if ("available".equals(room.getStatus())) {
            // Screen: MainActivity
            holder.btnReserve.setText("BOOK");
            holder.btnReserve.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9C27B0"))); // Purple
            holder.status.setText("Available");
            holder.status.setTextColor(Color.parseColor("#9C27B0"));
        } else {
            // Screen: MyBookingsActivity
            holder.btnReserve.setText("ANNULER");
            holder.btnReserve.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF6B6B"))); // Red
            holder.status.setText("Reserved");
            holder.status.setTextColor(Color.parseColor("#FF6B6B"));
        }

        // 3. Image Loading with Glide
        if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(room.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .centerCrop()
                    .into(holder.roomImage);
        } else {
            holder.roomImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // 4. Click Listeners
        holder.btnReserve.setOnClickListener(v -> listener.onRoomClick(room));
        holder.itemView.setOnClickListener(v -> listener.onRoomClick(room));
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    // 🔹 FIXED: Only ONE ViewHolder class with all bindings
    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView name, capacity, status;
        ImageView roomImage;
        MaterialButton btnReserve;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            capacity = itemView.findViewById(R.id.capacity);
            status = itemView.findViewById(R.id.status);
            roomImage = itemView.findViewById(R.id.roomImage);
            btnReserve = itemView.findViewById(R.id.btnReserve);
        }
    }
}