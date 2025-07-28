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

public class WeeklyTableFragment extends Fragment {
    private TableView tableView;

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

        List<String> headers = Arrays.asList("No. of Week", "Small Bottle", "Large Bottle", "Overall Weight", "Total no. of bottles", "Total amount of reward");
        List<String> rowHeaders = new ArrayList<>();
        for (int i = 1; i <= 4; i++) rowHeaders.add("Week " + i);

        List<List<String>> cells = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            int smallBottles = random.nextInt(50) + 10;
            int largeBottles = random.nextInt(30) + 5;
            int totalBottles = smallBottles + largeBottles;
            int overallWeight = totalBottles * (random.nextInt(10) + 5);
            double totalReward = totalBottles * (random.nextDouble() * 0.1);
            List<String> cellRow = Arrays.asList(
                    String.valueOf(i + 1),
                    String.valueOf(smallBottles),
                    String.valueOf(largeBottles),
                    String.valueOf(overallWeight) + "g",
                    String.valueOf(totalBottles),
                    "$" + String.format("%.2f", totalReward)
            );
            cells.add(cellRow);
        }

        adapter.setAllItems(headers, rowHeaders, cells);
    }
}

