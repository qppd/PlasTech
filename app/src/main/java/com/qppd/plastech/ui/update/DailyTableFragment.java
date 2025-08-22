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
import java.util.Calendar;
import java.util.List;
import java.util.Random;
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

        // Philippine popular bottle brands and sizes
        String[] bottleBrands = {
            "Coca-Cola 1.5L", "Coca-Cola 1L", "Coca-Cola 500ml", "Coca-Cola 350ml",
            "Royal Tru-Orange 1.5L", "Royal Tru-Orange 1L", "Royal Tru-Orange 500ml",
            "Royal Tru-Grape 1.5L", "Royal Tru-Grape 500ml",
            "Sprite 1.5L", "Sprite 1L", "Sprite 500ml", "Sprite 350ml",
            "Fanta Orange 1.5L", "Fanta Orange 1L", "Fanta Orange 500ml",
            "Pepsi 1.5L", "Pepsi 1L", "Pepsi 500ml",
            "7-Up 1.5L", "7-Up 1L", "7-Up 500ml",
            "Mirinda Orange 1.5L", "Mirinda Orange 500ml",
            "Mountain Dew 1.5L", "Mountain Dew 500ml",
            "Sarsi 1.5L", "Sarsi 500ml",
            "Pop Cola 1.5L", "Pop Cola 500ml"
        };

        // Weight mapping for different bottle sizes (in grams)
        // 1.5L bottles: 35-50g, 1L bottles: 25-35g, 500ml bottles: 15-25g, 350ml bottles: 12-18g
        
        List<String> headers = Arrays.asList("#","Date & Time", "Size of plastic", "H and W before crushing", "H and W after crushing", "Weight", "Total Rewards");
        
        List<List<String>> cells = new ArrayList<>();
        Random random = new Random();
        Calendar calendar = Calendar.getInstance();
        
        // Generate data for August 1-22, 2025 only
        int[] augustDays = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22};
        
        for (int i = 0; i < 22; i++) {
            // Generate realistic date and time for August 1-22, 2025
            calendar.set(Calendar.HOUR_OF_DAY, random.nextInt(12) + 8); // 8 AM to 8 PM
            calendar.set(Calendar.MINUTE, random.nextInt(60));
            
            String selectedBottle = bottleBrands[random.nextInt(bottleBrands.length)];
            
            // Determine bottle category for realistic dimensions and weight
            String sizeCategory;
            int weight;
            String beforeDimensions, afterDimensions;
            double reward;
            
            if (selectedBottle.contains("1.5L")) {
                sizeCategory = "Large";
                weight = random.nextInt(16) + 35; // 35-50g
                beforeDimensions = (random.nextInt(5) + 30) + " x " + (random.nextInt(3) + 8);
                afterDimensions = (random.nextInt(3) + 3) + " x " + (random.nextInt(3) + 8);
                reward = 2; // ₱2 for large bottles
            } else if (selectedBottle.contains("1L")) {
                sizeCategory = "Large";
                weight = random.nextInt(11) + 25; // 25-35g
                beforeDimensions = (random.nextInt(4) + 25) + " x " + (random.nextInt(2) + 7);
                afterDimensions = (random.nextInt(2) + 3) + " x " + (random.nextInt(2) + 7);
                reward = 2; // ₱2 for 1L bottles
            } else if (selectedBottle.contains("500ml")) {
                sizeCategory = "Small";
                weight = random.nextInt(11) + 15; // 15-25g
                beforeDimensions = (random.nextInt(3) + 20) + " x " + (random.nextInt(2) + 6);
                afterDimensions = (random.nextInt(2) + 2) + " x " + (random.nextInt(2) + 6);
                reward = 1; // ₱1 for small bottles
            } else { // 350ml
                sizeCategory = "Small";
                weight = random.nextInt(7) + 12; // 12-18g
                beforeDimensions = (random.nextInt(2) + 18) + " x " + (random.nextInt(1) + 5);
                afterDimensions = (random.nextInt(1) + 2) + " x " + (random.nextInt(1) + 5);
                reward = 1; // ₱1 for small bottles
            }
            
            String timeStamp = String.format("2025-08-%02d %02d:%02d", 
                augustDays[i], 
                calendar.get(Calendar.HOUR_OF_DAY), 
                calendar.get(Calendar.MINUTE));
            
            List<String> cellRow = Arrays.asList(
                    (i + 1) + "",
                    timeStamp,
                    sizeCategory,
                    beforeDimensions + " cm",
                    afterDimensions + " cm",
                    String.valueOf(weight) + "g",
                    "₱" + String.valueOf((int)reward)
            );
            cells.add(cellRow);
        }

        adapter.setAllItems(headers, null, cells);
    }
}

