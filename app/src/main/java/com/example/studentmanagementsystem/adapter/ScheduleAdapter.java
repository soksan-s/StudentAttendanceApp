package com.example.studentmanagementsystem.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.model.ScheduleItem;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private List<ScheduleItem> list;

    public ScheduleAdapter(List<ScheduleItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduleItem item = list.get(position);

        holder.tvStartTime.setText(item.getStartTime());
        holder.tvEndTime.setText(item.getEndTime());
        holder.tvSubject.setText(item.getSubjectName());
        holder.tvTeacher.setText(item.getTeacherName());
        holder.tvRoom.setText(item.getRoomNumber());
        holder.tvStatus.setText(item.getStatus());

        // Color Coding logic
        if ("In Progress".equals(item.getStatus())) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#E3F2FD")); // Light Blue
            holder.tvStatus.setTextColor(Color.parseColor("#2196F3"));
        } else if ("Finished".equals(item.getStatus())) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#F5F5F5")); // Grey
            holder.tvStatus.setTextColor(Color.GRAY);
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Green for Upcoming
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStartTime, tvEndTime, tvSubject, tvTeacher, tvRoom, tvStatus;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStartTime = itemView.findViewById(R.id.tv_schedule_start);
            tvEndTime = itemView.findViewById(R.id.tv_schedule_end);
            tvSubject = itemView.findViewById(R.id.tv_subject_name);
            tvTeacher = itemView.findViewById(R.id.tv_teacher_name);
            tvRoom = itemView.findViewById(R.id.tv_room_number);
            tvStatus = itemView.findViewById(R.id.tv_class_status);
            cardView = itemView.findViewById(R.id.card_schedule);
        }
    }
}
