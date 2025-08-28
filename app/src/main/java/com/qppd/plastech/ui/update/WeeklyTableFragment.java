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

    String[][] weeklyData = {
            {"Aug 20, 2025", "32", "28", "2713.7g", "60"},
            {"Aug 21, 2025", "14", "25", "1651.06g", "39"}
    };
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

        List<String> headers = Arrays.asList("Date", "Small Bottles",
                "Large Bottles", "Overall Weight", "Total Bottles");

        List<List<String>> cells = new ArrayList<>();

        String[][] weeklyData = {
                {"Aug 20, 2025", "32", "28", "2713.7g", "60"},
                {"Aug 21, 2025", "18", "55", "3357.93g", "73"},
                {"Aug 22, 2025", "26", "39", "2956.43g", "65"},
                {"Aug 25, 2025", "7", "2", "333.83g", "9"},
                {"Aug 26, 2025", "32", "33", "3023.78g", "65"},
                {"Aug 27, 2025", "32", "33", "3016.69g", "65"}};
        
        for (String[] weekData : weeklyData) {
            List<String> cellRow = Arrays.asList(
                    weekData[0], // Date
                    weekData[1], // Small bottles
                    weekData[2], // Large bottles
                    weekData[3], // Overall weight
                    weekData[4]  // Total bottles
            );
            cells.add(cellRow);
        }

        adapter.setAllItems(headers, null, cells);
    }
}

