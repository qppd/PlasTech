package com.qppd.plastech.ui.monitor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.qppd.plastech.R;
import com.qppd.plastech.utils.DummyDataGenerator;

public class DataManagementFragment extends Fragment {
    
    private DummyDataGenerator dummyDataGenerator;
    private Button btnGenerateDummyData;
    private Button btnGenerateTodayData;
    private ProgressBar progressBar;
    private Context context;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_data_management, container, false);
        context = root.getContext();
        
        initializeComponents(root);
        setupClickListeners();
        
        dummyDataGenerator = new DummyDataGenerator();
        
        return root;
    }
    
    private void initializeComponents(View root) {
        btnGenerateDummyData = root.findViewById(R.id.btnGenerateDummyData);
        btnGenerateTodayData = root.findViewById(R.id.btnGenerateTodayData);
        progressBar = root.findViewById(R.id.progressBar);
    }
    
    private void setupClickListeners() {
        btnGenerateDummyData.setOnClickListener(v -> generateDummyData());
        btnGenerateTodayData.setOnClickListener(v -> generateTodayData());
    }
    
    private void generateDummyData() {
        showLoading(true);
        btnGenerateDummyData.setEnabled(false);
        
        dummyDataGenerator.generateAndUploadDummyData(new DummyDataGenerator.DataGenerationCallback() {
            @Override
            public void onSuccess() {
                showLoading(false);
                btnGenerateDummyData.setEnabled(true);
                Toast.makeText(context, "Dummy data generated successfully for past 30 days!", Toast.LENGTH_LONG).show();
            }
            
            @Override
            public void onFailure(Exception e) {
                showLoading(false);
                btnGenerateDummyData.setEnabled(true);
                Toast.makeText(context, "Failed to generate dummy data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void generateTodayData() {
        showLoading(true);
        btnGenerateTodayData.setEnabled(false);
        
        dummyDataGenerator.generateAndUploadDummyData(new DummyDataGenerator.DataGenerationCallback() {
            @Override
            public void onSuccess() {
                showLoading(false);
                btnGenerateTodayData.setEnabled(true);
                Toast.makeText(context, "Today's data generated successfully!", Toast.LENGTH_LONG).show();
            }
            
            @Override
            public void onFailure(Exception e) {
                showLoading(false);
                btnGenerateTodayData.setEnabled(true);
                Toast.makeText(context, "Failed to generate today's data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
