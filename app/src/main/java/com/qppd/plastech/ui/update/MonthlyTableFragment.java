package com.qppd.plastech.ui.update;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.qppd.plastech.R;
import com.evrencoskun.tableview.TableView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import com.qppd.plastech.data.SharedRepository;

public class MonthlyTableFragment extends Fragment {
    private TableView tableView;
    private final SharedRepository sharedRepository = SharedRepository.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly_table, container, false);
        tableView = view.findViewById(R.id.monthly_table_view);
        
        initializeTable();
        return view;
    }

    private void initializeTable() {
        PlasticTableAdapter adapter = new PlasticTableAdapter(getContext());
        tableView.setAdapter(adapter);
        tableView.setHasFixedWidth(false);
        tableView.setRowHeaderWidth(0);

        sharedRepository.getEarningsLiveData().observe(getViewLifecycleOwner(), earnings -> {
            if (earnings != null) {
                // Update table data based on earnings
                // Example: adapter.updateData(earnings);
            }
        });

        List<String> headers = Arrays.asList("Period", "Overall Weight", "Total Bottles", "Total Rewards");
        
        List<List<String>> cells = new ArrayList<>();
        Random random = new Random();

        // Show August 2025 data broken down by periods (since we only have Aug 1-22)
        String[] periods = {
            "Aug 1-7, 2025",
            "Aug 8-14, 2025", 
            "Aug 15-21, 2025",
            "Aug 22, 2025",
            "August 1-22 Total"
        };
        
        int totalBottlesMonth = 0;
        int totalWeightMonth = 0;
        int totalRewardMonth = 0;

        for (int i = 0; i < 5; i++) {
            int totalBottles;
            int overallWeight;
            int totalReward;
            
            if (i == 4) { // Total row
                totalBottles = totalBottlesMonth;
                overallWeight = totalWeightMonth;
                totalReward = totalRewardMonth;
            } else if (i == 3) { // Aug 22 (single day)
                totalBottles = random.nextInt(50) + 25; // 25-75 bottles for 1 day
                
                // Calculate realistic weight (mix of small and large bottles)
                // Assume 60% small bottles (avg 18g), 40% large bottles (avg 35g)
                int smallBottles = (int)(totalBottles * 0.6);
                int largeBottles = (int)(totalBottles * 0.4);
                
                overallWeight = (smallBottles * (random.nextInt(8) + 15)) + // 15-22g for small
                              (largeBottles * (random.nextInt(16) + 28));   // 28-43g for large
                
                // Reward calculation: Small ₱1, Large ₱2
                totalReward = (smallBottles * 1) + (largeBottles * 2);
            } else { // Weekly periods (7 days each)
                totalBottles = random.nextInt(500) + 300; // 300-800 bottles per week
                
                // Calculate realistic weight (mix of small and large bottles)
                // Assume 60% small bottles (avg 18g), 40% large bottles (avg 35g)
                int smallBottles = (int)(totalBottles * 0.6);
                int largeBottles = (int)(totalBottles * 0.4);
                
                overallWeight = (smallBottles * (random.nextInt(8) + 15)) + // 15-22g for small
                              (largeBottles * (random.nextInt(16) + 28));   // 28-43g for large
                
                // Reward calculation: Small ₱1, Large ₱2
                totalReward = (smallBottles * 1) + (largeBottles * 2);
            }
            
            if (i < 4) { // Don't add total row values to itself
                totalBottlesMonth += totalBottles;
                totalWeightMonth += overallWeight;
                totalRewardMonth += totalReward;
            }
            
            List<String> cellRow = Arrays.asList(
                    periods[i],
                    String.valueOf(overallWeight) + "g",
                    String.valueOf(totalBottles),
                    "₱" + String.valueOf(totalReward)
            );
            cells.add(cellRow);
        }

        adapter.setAllItems(headers, null, cells);
    }
}
