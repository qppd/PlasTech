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
        
        // Data based on actual plastic crushing records for Aug 22-23, 2025
        // Aug 22: 9 Small bottles (15g+17g+15g+18g+16g+16g+15g+17g+18g = 147g), 7 Large bottles (45g+46g+45g+44g+47g+45g+48g = 320g)
        // Aug 23: 6 Small bottles (15g+21g+10g+20g+25g+19g = 110g), 7 Large bottles (40g+43g+39g+36g+44g+45g+43g = 290g)
        
        String[][] weeklyData = {
            {"Aug 19-25, 2025", "15", "14", "867g", "29", "₱29"},
            {"Aug 26-31, 2025", "0", "0", "0g", "0", "₱0"}
        };
        
        for (String[] weekData : weeklyData) {
            List<String> cellRow = Arrays.asList(
                    weekData[0], // Week period
                    weekData[1], // Small bottles
                    weekData[2], // Large bottles
                    weekData[3], // Overall weight
                    weekData[4], // Total bottles
                    weekData[5]  // Total rewards
            );
            cells.add(cellRow);
        }

        adapter.setAllItems(headers, null, cells);
    }
}

