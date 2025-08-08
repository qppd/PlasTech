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

public class MonthlyTableFragment extends Fragment {
    private TableView tableView;

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

        List<String> headers = Arrays.asList("Month", "Overall Weight", "Total number of bottle", "Total amount of reward");
        
        List<List<String>> cells = new ArrayList<>();
        Random random = new Random();
        String[] months = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        for (int i = 0; i < 12; i++) {
            int totalBottles = random.nextInt(500) + 100;
            int overallWeight = totalBottles * (random.nextInt(10) + 5);
            double totalReward = totalBottles * (random.nextDouble() * 0.1);
            List<String> cellRow = Arrays.asList(
                    months[i],
                    String.valueOf(overallWeight) + "g",
                    String.valueOf(totalBottles),
                    "$" + String.format("%.2f", totalReward)
            );
            cells.add(cellRow);
        }

        adapter.setAllItems(headers, null, cells);
    }
}
