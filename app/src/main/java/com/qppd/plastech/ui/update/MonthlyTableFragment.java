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

        // Data based on actual plastic crushing records for August 20-27, 2025
        // Total: 160 Small + 227 Large = 387 bottles across multiple days
        
        String[][] monthlyData = {
            {"Aug 20, 2025", "2713.7g", "60"},
            {"Aug 21, 2025", "3357.93g", "73"},
            {"Aug 22, 2025", "2956.43g", "65"},
            {"Aug 25, 2025", "1695.62g", "53"},
            {"Aug 26, 2025", "3368.25g", "73"},
            {"Aug 27, 2025", "5015.32g", "109"},
            {"August 2025 Total", "19107.25g", "387"}
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
