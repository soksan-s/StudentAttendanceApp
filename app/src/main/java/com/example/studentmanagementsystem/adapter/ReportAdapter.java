package com.example.studentmanagementsystem.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.model.AttendanceDataItem; // Use your existing model or create ReportItem

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<AttendanceDataItem> reportList;

    public ReportAdapter(List<AttendanceDataItem> reportList) {
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_row, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        AttendanceDataItem item = reportList.get(position);

        // NOTE: Depending on your API, item might have 'studentName' or just 'studentId'.
        // Ideally, the report API returns the name. If not, display ID.
        holder.tvName.setText(item.getStudentId()); // Change to getStudentName() if available
        holder.tvDate.setText(item.getDate().split("T")[0]); // Show just YYYY-MM-DD

        String status = item.getStatus();
        holder.tvStatus.setText(status);

        // Color coding
        switch (status.toUpperCase()) {
            case "PRESENT": holder.tvStatus.setTextColor(Color.parseColor("#4CAF50")); break; // Green
            case "ABSENT": holder.tvStatus.setTextColor(Color.parseColor("#F44336")); break; // Red
            case "LATE": holder.tvStatus.setTextColor(Color.parseColor("#FF9800")); break; // Orange
            case "EXCUSED": holder.tvStatus.setTextColor(Color.parseColor("#2196F3")); break; // Blue
        }
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvStatus;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_report_student_name);
            tvDate = itemView.findViewById(R.id.tv_report_date);
            tvStatus = itemView.findViewById(R.id.tv_report_status);
        }
    }
}
