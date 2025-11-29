package com.example.studentmanagementsystem.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.model.EventItem;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<EventItem> eventList;

    public EventAdapter(List<EventItem> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventItem item = eventList.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvDate.setText(item.getDate());
        holder.tvDesc.setText(item.getDescription());

        // Use standard resource loading for now
        holder.ivImage.setImageResource(item.getImageResId());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvDesc;
        ImageView ivImage;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_event_title);
            tvDate = itemView.findViewById(R.id.tv_event_date);
            tvDesc = itemView.findViewById(R.id.tv_event_desc);
            ivImage = itemView.findViewById(R.id.iv_event_image);
        }
    }
}
