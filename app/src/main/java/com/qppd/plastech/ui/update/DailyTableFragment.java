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
import com.qppd.plastech.data.SharedRepository;

public class DailyTableFragment extends Fragment {
    private TableView tableView;
    private final SharedRepository sharedRepository = SharedRepository.getInstance();
    private PlasticTableAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_table, container, false);
        tableView = view.findViewById(R.id.daily_table_view);
        
        initializeTable();
        
        // Set up the observer only once in onCreateView
        sharedRepository.getEarningsLiveData().observe(getViewLifecycleOwner(), earnings -> {
            if (earnings != null && adapter != null) {
                // Update table data based on earnings
                // Example: adapter.updateData(earnings);
            }
        });
        
        return view;
    }

    private void initializeTable() {
        adapter = new PlasticTableAdapter(getContext());
        tableView.setAdapter(adapter);
        tableView.setHasFixedWidth(false);
        tableView.setRowHeaderWidth(0);

        List<String> headers = Arrays.asList("#","Date & Time", "Size of plastic", "H and W before crushing", "H and W after crushing", "Weight", "Total Rewards");
        
        List<List<String>> cells = new ArrayList<>();
        
        // Actual plastic crushing data for August 22-23, 2025
        String[][] plasticData = {
            // August 22, 2025 data
            {"8/22/2025 8:05:00", "Large", "9.5\" x 9\"", "", "45g", "₱1.00"},
            {"8/22/2025 8:09:00", "Small", "5\" x 5\"", "", "15g", "₱1.00"},
            {"8/22/2025 8:12:00", "Small", "6.6\" x 6\"", "", "17g", "₱1.00"},
            {"8/22/2025 8:16:00", "Large", "9.5\" x 9\"", "", "46g", "₱1.00"},
            {"8/22/2025 8:21:00", "Large", "10\" x 9\"", "", "45g", "₱1.00"},
            {"8/22/2025 8:30:00", "Small", "5.5\" x 5\"", "", "15g", "₱1.00"},
            {"8/22/2025 8:37:00", "Small", "5.6\" x 5\"", "", "18g", "₱1.00"},
            {"8/22/2025 8:42:00", "Large", "9.4\" x 8\"", "", "44g", "₱1.00"},
            {"8/22/2025 8:50:00", "Small", "5.5\" x 5\"", "", "16g", "₱1.00"},
            {"8/22/2025 9:03:00", "Small", "5\" x 5\"", "", "16g", "₱1.00"},
            {"8/22/2025 9:25:00", "Small", "5\" x 5\"", "", "15g", "₱1.00"},
            {"8/22/2025 9:34:00", "Large", "9\" x 8.5\"", "", "47g", "₱1.00"},
            {"8/22/2025 9:46:00", "Large", "9.5\" x 9\"", "", "45g", "₱1.00"},
            {"8/22/2025 9:58:00", "Small", "6.7\" x 5\"", "", "17g", "₱1.00"},
            {"8/22/2025 10:06:00", "Large", "9.8\" x 8.7\"", "", "48g", "₱1.00"},
            {"8/22/2025 10:19:00", "Small", "6.4\" x 5\"", "", "18g", "₱1.00"},
            
            // August 23, 2025 data
            {"8/23/2025 9:34:00", "Small", "5\" x 5\"", "", "15g", "₱1.00"},
            {"8/23/2025 9:47:00", "Small", "6.5\" x 5\"", "", "21g", "₱1.00"},
            {"8/23/2025 10:02:00", "Small", "5.5\" x 5\"", "", "10g", "₱1.00"},
            {"8/23/2025 10:14:00", "Large", "9.5\" x 8\"", "", "40g", "₱1.00"},
            {"8/23/2025 10:32:00", "Large", "9.5\" x 9\"", "", "43g", "₱1.00"},
            {"8/23/2025 10:44:00", "Small", "5.5\" x 5\"", "", "20g", "₱1.00"},
            {"8/23/2025 10:59:00", "Small", "5.7\" x 5\"", "", "25g", "₱1.00"},
            {"8/23/2025 11:16:00", "Large", "10\" x 8\"", "", "39g", "₱1.00"},
            {"8/23/2025 11:43:00", "Large", "8.4\" x 8\"", "", "36g", "₱1.00"},
            {"8/23/2025 11:56:00", "Small", "7.2\" x 6\"", "", "19g", "₱1.00"},
            {"8/23/2025 12:07:00", "Large", "9.5\" x 9\"", "", "44g", "₱1.00"},
            {"8/23/2025 12:18:00", "Large", "9.9\" x 8\"", "", "45g", "₱1.00"},
            {"8/23/2025 12:33:00", "Large", "8.7\" x 9\"", "", "43g", "₱1.00"}
        };
        
        for (int i = 0; i < plasticData.length; i++) {
            List<String> cellRow = Arrays.asList(
                    String.valueOf(i + 1),
                    plasticData[i][0], // Date & Time
                    plasticData[i][1], // Size of plastic
                    plasticData[i][2], // H and W before crushing
                    plasticData[i][3], // H and W after crushing (empty in source data)
                    plasticData[i][4], // Weight
                    plasticData[i][5]  // Rewards
            );
            cells.add(cellRow);
        }

        adapter.setAllItems(headers, null, cells);
    }
}

