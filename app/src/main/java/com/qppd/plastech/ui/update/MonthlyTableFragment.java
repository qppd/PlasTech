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

        List<String> headers = Arrays.asList("Period", "Overall Weight", "Total Bottles", "Total Rewards");
        
        List<List<String>> cells = new ArrayList<>();

        // Data based on actual plastic crushing records
        // Aug 22: 9 Small (147g) + 7 Large (320g) = 16 bottles, 467g total, ₱16
        // Aug 23: 6 Small (110g) + 7 Large (290g) = 13 bottles, 400g total, ₱13
        // Total: 15 Small + 14 Large = 29 bottles, 867g total, ₱29
        
        String[][] monthlyData = {
            {"Aug 22, 2025", "467g", "16", "₱16"},
            {"Aug 23, 2025", "400g", "13", "₱13"},
            {"August 2025 Total", "867g", "29", "₱29"}
        };
        
        for (String[] monthData : monthlyData) {
            List<String> cellRow = Arrays.asList(
                    monthData[0], // Period
                    monthData[1], // Overall weight
                    monthData[2], // Total bottles
                    monthData[3]  // Total rewards
            );
            cells.add(cellRow);
        }

        adapter.setAllItems(headers, null, cells);
    }
}
