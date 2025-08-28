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

        List<String> headers = Arrays.asList("Period", "Overall Weight", "Total Bottles");
        
        List<List<String>> cells = new ArrayList<>();

        // Data based on actual plastic crushing records for August 20-21, 2025
        // Total: 46 Small + 53 Large = 99 bottles across 2 days
        
        String[][] monthlyData = {
            {"Aug 20, 2025", "2713.7g", "60"},
            {"Aug 21, 2025", "1651.06g", "39"},
            {"August 2025 Total", "4364.76g", "99"}
        };
        
        for (String[] monthData : monthlyData) {
            List<String> cellRow = Arrays.asList(
                    monthData[0], // Period
                    monthData[1], // Overall weight
                    monthData[2]  // Total bottles
            );
            cells.add(cellRow);
        }

        adapter.setAllItems(headers, null, cells);
    }
}
