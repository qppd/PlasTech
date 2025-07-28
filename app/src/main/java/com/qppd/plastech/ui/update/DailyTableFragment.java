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

public class DailyTableFragment extends Fragment {
    private TableView tableView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_table, container, false);
        tableView = view.findViewById(R.id.daily_table_view);
        
        initializeTable();
        return view;
    }

    private void initializeTable() {
        PlasticTableAdapter adapter = new PlasticTableAdapter(getContext());
        tableView.setAdapter(adapter);
        tableView.setHasFixedWidth(false);

        List<String> headers = Arrays.asList("Date & Time", "Size of plastic", "H and W before crushing", "H and W after crushing", "Weight", "Total Rewards");
        
        List<String> rowHeaders = new ArrayList<>();
        for (int i = 1; i <= 20; i++) rowHeaders.add(String.valueOf(i));

        List<List<String>> cells = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            List<String> cellRow = Arrays.asList(
                "2024-05-" + (random.nextInt(30) + 1),
                random.nextBoolean() ? "Small" : "Large",
                (random.nextInt(10) + 5) + "x" + (random.nextInt(10) + 5),
                (random.nextInt(5) + 1) + "x" + (random.nextInt(5) + 1),
                String.valueOf(random.nextInt(500) + 50) + "g",
                "$" + String.format("%.2f", random.nextDouble() * 5)
            );
            cells.add(cellRow);
        }

        adapter.setAllItems(headers, rowHeaders, cells);
    }
}

