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

public class WeeklyTableFragment extends Fragment {
    private TableView tableView;
    private final SharedRepository sharedRepository = SharedRepository.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_table, container, false);
        tableView = view.findViewById(R.id.weekly_table_view);

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

        List<String> headers = Arrays.asList("Week", "Small Bottles", "Large Bottles", "Overall Weight", "Total Bottles", "Total Rewards");

        List<List<String>> cells = new ArrayList<>();
        Random random = new Random();
        
        // Generate data for weeks in August 1-22, 2025
        String[] weekPeriods = {
            "Aug 1-7, 2025", 
            "Aug 8-14, 2025", 
            "Aug 15-21, 2025", 
            "Aug 22, 2025 (partial)"
        };
        
        for (int i = 0; i < 4; i++) {
            // Realistic weekly bottle collection data for Philippine market
            // Small bottles: 500ml, 350ml bottles (Coke, Royal, Sprite, etc.)
            int smallBottles;
            int largeBottles;
            
            if (i == 3) { // Last partial week (Aug 22 only - 1 day)
                smallBottles = random.nextInt(30) + 15;  // 15-45 small bottles for 1 day
                largeBottles = random.nextInt(15) + 10;   // 10-25 large bottles for 1 day
            } else { // Full weeks
                smallBottles = random.nextInt(180) + 120;  // 120-300 small bottles per week
                largeBottles = random.nextInt(100) + 80;   // 80-180 large bottles per week
            }
            
            int totalBottles = smallBottles + largeBottles;
            
            // Calculate realistic weight
            // Small bottles average: 18g, Large bottles average: 35g
            int overallWeight = (smallBottles * (random.nextInt(8) + 15)) + // 15-22g for small
                              (largeBottles * (random.nextInt(16) + 28));   // 28-43g for large
            
            // Reward calculation: Small ₱1, Large ₱2
            int totalReward = (smallBottles * 1) + (largeBottles * 2); // Simple whole number calculation
            
            List<String> cellRow = Arrays.asList(
                    weekPeriods[i],
                    String.valueOf(smallBottles),
                    String.valueOf(largeBottles),
                    String.valueOf(overallWeight) + "g",
                    String.valueOf(totalBottles),
                    "₱" + String.valueOf(totalReward)
            );
            cells.add(cellRow);
        }

        adapter.setAllItems(headers, null, cells);
    }
}

