package com.qppd.plastech.ui.home.adapter;

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

public class HomeActivityAdapter extends RecyclerView.Adapter<HomeActivityAdapter.ActivityViewHolder> {

    private List<ActivityLog> activities = new ArrayList<>();

    public void setActivities(List<ActivityLog> activities) {
        this.activities = activities;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActivityLog activity = activities.get(position);
        holder.bind(activity);
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        private TextView txtActivityTitle;
        private TextView txtActivityDescription;
        private TextView txtActivityTime;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            txtActivityTitle = itemView.findViewById(R.id.txtActivityTitle);
            txtActivityDescription = itemView.findViewById(R.id.txtActivityDescription);
            txtActivityTime = itemView.findViewById(R.id.txtActivityTime);
        }

        public void bind(ActivityLog activity) {
            txtActivityTitle.setText(activity.getAction());
            txtActivityDescription.setText(activity.getDescription());
            txtActivityTime.setText(formatTime(activity.getTimestamp()));
        }

        private String formatTime(String timestamp) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
                Date date = inputFormat.parse(timestamp);
                return outputFormat.format(date);
            } catch (ParseException e) {
                return timestamp;
            }
        }
    }
}
