package com.qppd.plastech.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qppd.plastech.Classes.ActivityLog;
import com.qppd.plastech.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityLogAdapter extends RecyclerView.Adapter<ActivityLogAdapter.ActivityLogViewHolder> {

    private List<ActivityLog> activityLogs = new ArrayList<>();

    public void setActivityLogs(List<ActivityLog> activityLogs) {
        this.activityLogs = activityLogs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActivityLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity_log, parent, false);
        return new ActivityLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityLogViewHolder holder, int position) {
        ActivityLog activityLog = activityLogs.get(position);
        holder.bind(activityLog);
    }

    @Override
    public int getItemCount() {
        return activityLogs.size();
    }

    static class ActivityLogViewHolder extends RecyclerView.ViewHolder {
        private TextView txtAction;
        private TextView txtDescription;
        private TextView txtTimestamp;
        private TextView txtStatus;

        public ActivityLogViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAction = itemView.findViewById(R.id.txtAction);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtTimestamp = itemView.findViewById(R.id.txtTimestamp);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }

        public void bind(ActivityLog activityLog) {
            txtAction.setText(activityLog.getAction());
            txtDescription.setText(activityLog.getDescription());
            txtTimestamp.setText(formatTimestamp(activityLog.getTimestamp()));
            txtStatus.setText(activityLog.getStatus());
            
            // Set status color based on the status value
            switch (activityLog.getStatus().toLowerCase()) {
                case "completed":
                    txtStatus.setTextColor(itemView.getContext().getColor(android.R.color.holo_green_dark));
                    break;
                case "pending":
                    txtStatus.setTextColor(itemView.getContext().getColor(android.R.color.holo_orange_dark));
                    break;
                case "failed":
                    txtStatus.setTextColor(itemView.getContext().getColor(android.R.color.holo_red_dark));
                    break;
                default:
                    txtStatus.setTextColor(itemView.getContext().getColor(android.R.color.darker_gray));
                    break;
            }
        }

        private String formatTimestamp(String timestamp) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
                Date date = inputFormat.parse(timestamp);
                return outputFormat.format(date);
            } catch (ParseException e) {
                return timestamp; // Return original if parsing fails
            }
        }
    }
}
